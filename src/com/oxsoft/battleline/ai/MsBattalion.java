package com.oxsoft.battleline.ai;

import java.util.ArrayList;
import java.util.EnumMap;

import com.oxsoft.battleline.container.FlagContainer;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.card.Color;
import com.oxsoft.battleline.model.card.Troop;

public class MsBattalion extends ArtificialIntelligence {

	public MsBattalion(GameStateInput gameStateInput) {
		super(gameStateInput);
	}

	@Override
	public void action(GameStateContainer gsc) {
		if (input.passTurn()) return;
		EnumMap<Color, Integer> targetColumn = new EnumMap<Color, Integer>(Color.class);
		for (Color color : Color.values()) {
			targetColumn.put(color, -1);
		}
		ArrayList<Integer> emptyColumn = new ArrayList<Integer>();
		ArrayList<Integer> inconsistentColumn = new ArrayList<Integer>();
		for (int i = 0; i < gsc.columns.length; i++) {
			if (gsc.columns[i].flag == FlagContainer.UNCLAIMED) {
				if (gsc.columns[i].me.length > 0) {
					boolean same = true;
					for (int j = 1; j < gsc.columns[i].me.length; j++) {
						if (gsc.columns[i].me[j].color != gsc.columns[i].me[0].color) {
							same = false;
							break;
						}
					}
					if (same) {
						if (targetColumn.get(gsc.columns[i].me[0].color) == -1) {
							targetColumn.put(gsc.columns[i].me[0].color, i);
						} else {
							if (gsc.columns[i].me.length > gsc.columns[targetColumn.get(gsc.columns[i].me[0].color)].me.length) {
								targetColumn.put(gsc.columns[i].me[0].color, i);
							}
						}
					} else {
						if (gsc.columns[i].me.length < 3) inconsistentColumn.add(i);
					}
				} else {
					emptyColumn.add(i);
				}
			}
		}
		for (int i = 0; i < gsc.myHand.length; i++) {
			if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
				if (targetColumn.get(gsc.myHand[i].color) >= 0) {
					boolean success = input.putCard(i, targetColumn.get(gsc.myHand[i].color));
					if (success) return;
				}
			}
		}
		// count necessary card for battalion
		EnumMap<Color, Integer> necessary = new EnumMap<Color, Integer>(Color.class);
		for (Color color : Color.values()) {
			necessary.put(color, 3);
		}
		for (int i = 0; i < gsc.myHand.length; i++) {
			if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
				necessary.put(gsc.myHand[i].color, necessary.get(gsc.myHand[i].color) - 1);
			}
		}
		// count exist card in the troop deck
		EnumMap<Color, Integer> exist = new EnumMap<Color, Integer>(Color.class);
		for (Color color : Color.values()) {
			exist.put(color, Troop.MAX_VALUE);
		}
		for (int i = 0; i < gsc.columns.length; i++) {
			for (int j = 0; j < gsc.columns[i].me.length; j++) {
				if (gsc.columns[i].me[j].type.equals(Troop.class.getSimpleName())) {
					exist.put(gsc.columns[i].me[j].color, exist.get(gsc.columns[i].me[j].color) - 1);
				}
			}
			for (int j = 0; j < gsc.columns[i].opponent.length; j++) {
				if (gsc.columns[i].opponent[j].type.equals(Troop.class.getSimpleName())) {
					exist.put(gsc.columns[i].opponent[j].color, exist.get(gsc.columns[i].opponent[j].color) - 1);
				}
			}
		}
		for (int i = 0; i < gsc.discarded.length; i++) {
			if (gsc.discarded[i].type.equals(Troop.class.getSimpleName())) {
				exist.put(gsc.discarded[i].color, exist.get(gsc.discarded[i].color) - 1);
			}
		}
		// count opponent troop
		int opponentHand = 0;
		for (int i = 0; i < gsc.opponentHand.length; i++) {
			if (gsc.opponentHand[i].type.equals(Troop.class.getSimpleName())) {
				opponentHand++;
			}
		}
		// calculate probability to make battalion
		EnumMap<Color, Double> probability = new EnumMap<Color, Double>(Color.class);
		for (Color color : Color.values()) {
			probability.put(color, calcProbability(necessary.get(color), exist.get(color), gsc.troopDeck, opponentHand));
		}
		double maxProbability = Double.MIN_VALUE;
		double minProbability = Double.MAX_VALUE;
		int maxHand = -1;
		int minHand = -1;
		for (int i = 0; i < gsc.myHand.length; i++) {
			if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
				if (probability.get(gsc.myHand[i].color) > maxProbability) {
					maxProbability = probability.get(gsc.myHand[i].color);
					maxHand = i;
				}
				if (probability.get(gsc.myHand[i].color) < minProbability) {
					minProbability = probability.get(gsc.myHand[i].color);
					minHand = i;
				}
			}
		}
		// make new battalion
		if (emptyColumn.size() > 0) {
			boolean success = input.putCard(maxHand, emptyColumn.get((int) (Math.random() * emptyColumn.size())));
			if (success) return;
		}
		// trash card
		if (inconsistentColumn.size() > 0) {
			boolean success = input.putCard(minHand, inconsistentColumn.get((int) (Math.random() * inconsistentColumn.size())));
			if (success) return;
		}
		// make new trash column
		double minProbability2 = Double.MAX_VALUE;
		int minColumn = -1;
		for (int i = 0; i < gsc.columns.length; i++) {
			if (gsc.columns[i].flag == FlagContainer.UNCLAIMED) {
				if ((gsc.columns[i].me.length > 0) && (gsc.columns[i].me.length < 3)) {
					if (gsc.columns[i].me[0].type.equals(Troop.class.getSimpleName())) {
						if (probability.get(gsc.columns[i].me[0].color) < minProbability2) {
							minProbability2 = probability.get(gsc.columns[i].me[0].color);
							minColumn = i;
						}
					}
				}
			}
		}
		if (minColumn >= 0) {
			boolean success = input.putCard(minHand, minColumn);
			if (success) return;
		}
		// last resort for safety
		int handIndex = -1;
		while (handIndex == -1) {
			int h = (int) (Math.random() * gsc.myHand.length);
			if (gsc.myHand[h].type.equals(Troop.class.getSimpleName())) handIndex = h;
		}
		boolean success = false;
		while (!success) {
			success = input.putCard(handIndex, (int) (Math.random() * GameState.MAX_COLUMN));
		}
	}

	@Override
	public void draw(GameStateContainer gsc) {
		if (gsc.troopDeck > 0) {
			input.drawTroop();
		} else if (gsc.tacticsDeck > 0) {
			input.drawTactics();
		} else {
			input.endTurnWithoutDraw();
		}
	}

	@Override
	public void returnCard(GameStateContainer gsc) {
		input.returnCards(new int[] { 0, 1 });
	}

	private double calcProbability(int necessary, int exist, int troopDeck, int opponentHand) {
		if (necessary <= 0) return 1.0;
		if (necessary > exist) return 0.0;
		if (necessary > troopDeck / 2) return 0.0;
		double probability = 0.0;
		while (necessary <= exist && necessary <= troopDeck / 2) {
			probability += binomialCoefficient(troopDeck / 2, necessary) * binomialCoefficient(troopDeck / 2 + opponentHand, exist - necessary);
			necessary++;
		}
		probability /= binomialCoefficient(troopDeck + opponentHand, exist);
		return probability;
	}

	private int binomialCoefficient(int n, int k) {
		if ((k < 0) || (k > n)) return 0;
		int nCk = 1;
		for (int i = 0; i < k; i++) {
			nCk = nCk * (n - i) / (i + 1);
		}
		return nCk;
	}
}
