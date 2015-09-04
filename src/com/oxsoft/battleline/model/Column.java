package com.oxsoft.battleline.model;

import java.util.ArrayList;

import com.oxsoft.battleline.model.card.EnvironmentTactics;
import com.oxsoft.battleline.model.card.Fog;
import com.oxsoft.battleline.model.card.Mud;
import com.oxsoft.battleline.model.card.Troop;

public class Column {
	private final Slot persia;
	private final Slot macedonia;
	private final ArrayList<EnvironmentTactics> environments;
	private Flag flag;

	public Column() {
		persia = new Slot();
		macedonia = new Slot();
		environments = new ArrayList<EnvironmentTactics>();
		flag = Flag.UNCLAIMED;
	}

	private int getSlotCapacity() {
		for (EnvironmentTactics environment : environments) {
			if (environment instanceof Mud) {
				return 4;
			}
		}
		return 3;
	}

	private boolean isConsecutiveValues(Troop[] formation) {
		int cards = 0;
		for (int i = 0; i < formation.length; i++) {
			cards |= 1 << formation[i].getValue();
		}
		if (cards % ((1 << formation.length) - 1) == 0) {
			int q = cards / ((1 << formation.length) - 1);
			if ((q & q - 1) == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean isSameColor(Troop[] formation) {
		for (int i = 1; i < formation.length; i++) {
			if (formation[i].getColor() != formation[0].getColor()) {
				return false;
			}
		}
		return true;
	}

	private boolean isSameValue(Troop[] formation) {
		for (int i = 1; i < formation.length; i++) {
			if (formation[i].getValue() != formation[0].getValue()) {
				return false;
			}
		}
		return true;
	}

	private int calcSum(Troop[] formation) {
		int sum = 0;
		for (int i = 0; i < formation.length; i++) {
			sum += formation[i].getValue();
		}
		return sum;
	}

	private int calcScore(Troop[] formation) {
		int score = calcSum(formation);
		for (EnvironmentTactics environment : environments) {
			if (environment instanceof Fog) {
				return score;
			}
		}
		boolean isSameColor = isSameColor(formation);
		boolean isConsecutiveValues = isConsecutiveValues(formation);
		if (isSameColor && isConsecutiveValues) {
			score += Formation.WEDGE;
		} else if (isSameValue(formation)) {
			score += Formation.PHALANX;
		} else if (isSameColor) {
			score += Formation.BATTALION_ORDER;
		} else if (isConsecutiveValues) {
			score += Formation.SKIRMISH_LINE;
		} else {
			score += Formation.HOST;
		}
		return score;
	}

	private int calcMaxScore(Placeable[] placed, Troop[] available) {
		return calcMaxScore(new Troop[getSlotCapacity()], placed, available, new ArrayList<Integer>(), 0);
	}

	private int calcMaxScore(Troop[] formation, Placeable[] placed, Troop[] available, ArrayList<Integer> notAvailable, int index) {
		if (index == getSlotCapacity()) return calcScore(formation);
		int maxScore = 0;
		Troop[] troops = (index < placed.length) ? placed[index].getAvailableTroops() : available;
		for (int i = 0; i < troops.length; i++) {
			if ((index >= placed.length) && (notAvailable.contains(i))) continue;
			formation[index] = troops[i];
			Integer integer = new Integer(i);
			notAvailable.add(integer);
			int score = calcMaxScore(formation, placed, available, notAvailable, index + 1);
			notAvailable.remove(integer);
			if (score > maxScore) maxScore = score;
		}
		return maxScore;
	}

	public boolean resolveFlag(Side side, Troop[] availableTroops) {
		if (flag != Flag.UNCLAIMED) return false;
		Slot prover, verifier;
		switch (side) {
		case PERSIA:
			prover = persia;
			verifier = macedonia;
			break;
		case MACEDONIA:
			prover = macedonia;
			verifier = persia;
			break;
		default:
			return false;
		}
		if (prover.size() < getSlotCapacity()) return false;
		int proverScore = calcMaxScore(prover.getCards(), availableTroops);
		int verfierScore = calcMaxScore(verifier.getCards(), availableTroops);
		if (proverScore >= verfierScore) {
			flag = (side == Side.PERSIA) ? Flag.PERSIA : (side == Side.MACEDONIA) ? Flag.MACEDONIA : Flag.UNCLAIMED;
			return true;
		}
		return false;
	}

	public boolean putCard(Side side, Placeable card) {
		if (flag != Flag.UNCLAIMED) return false;
		switch (side) {
		case PERSIA:
			if (persia.size() >= getSlotCapacity()) return false;
			return persia.add(card);
		case MACEDONIA:
			if (macedonia.size() >= getSlotCapacity()) return false;
			return macedonia.add(card);
		default:
			return false;
		}
	}

	public Placeable getCard(Side side, int index) {
		switch (side) {
		case PERSIA:
			return persia.getCard(index);
		case MACEDONIA:
			return macedonia.getCard(index);
		default:
			return null;
		}
	}

	public Placeable removeCard(Side side, int cardIndex) {
		if (flag != Flag.UNCLAIMED) return null;
		switch (side) {
		case PERSIA:
			return persia.remove(cardIndex);
		case MACEDONIA:
			return macedonia.remove(cardIndex);
		default:
			return null;
		}
	}

	public int getCardLength(Side side) {
		switch (side) {
		case PERSIA:
			return persia.size();
		case MACEDONIA:
			return macedonia.size();
		default:
			return 0;
		}
	}

	public Flag getFlag() {
		return flag;
	}

	public EnvironmentTactics[] getEnvironmentTactics() {
		return environments.toArray(new EnvironmentTactics[environments.size()]);
	}

	public boolean putEnvironment(EnvironmentTactics environmentTactics) {
		if (flag != Flag.UNCLAIMED) return false;
		environments.add(environmentTactics);
		return true;
	}

	public boolean hasFreeSpace(Side side) {
		if (flag != Flag.UNCLAIMED) return false;
		switch (side) {
		case PERSIA:
			return persia.size() < getSlotCapacity();
		case MACEDONIA:
			return macedonia.size() < getSlotCapacity();
		default:
			return false;
		}
	}
}
