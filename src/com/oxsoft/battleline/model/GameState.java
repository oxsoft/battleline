package com.oxsoft.battleline.model;

import java.util.ArrayList;
import java.util.EnumMap;

import com.oxsoft.battleline.model.card.Card;
import com.oxsoft.battleline.model.card.Color;
import com.oxsoft.battleline.model.card.Deserter;
import com.oxsoft.battleline.model.card.EnvironmentTactics;
import com.oxsoft.battleline.model.card.GuileTactics;
import com.oxsoft.battleline.model.card.Leader;
import com.oxsoft.battleline.model.card.MoraleTactics;
import com.oxsoft.battleline.model.card.Redeploy;
import com.oxsoft.battleline.model.card.Scout;
import com.oxsoft.battleline.model.card.Tactics;
import com.oxsoft.battleline.model.card.Traitor;
import com.oxsoft.battleline.model.card.Troop;

public class GameState {
	public static final int MAX_COLUMN = 9;
	private final Column[] columns;
	private final TroopDeck troopDeck;
	private final TacticsDeck tacticsDeck;
	private final Hand persia;
	private final Hand macedonia;
	private Side turn;
	private Phase phase;
	private int leaderCount;
	private int tacticsCount;
	private final ArrayList<Placeable> discarded;
	private final ArrayList<GuileTactics> usedGuileTactics;

	public GameState() {
		columns = new Column[MAX_COLUMN];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new Column();
		}
		troopDeck = new TroopDeck();
		tacticsDeck = new TacticsDeck();
		persia = new Hand();
		macedonia = new Hand();
		for (int i = 0; i < Hand.MAX_HAND; i++) {
			persia.add(troopDeck.draw());
			macedonia.add(troopDeck.draw());
		}
		turn = Side.MACEDONIA;
		phase = Phase.ACTION;
		leaderCount = 0;
		tacticsCount = 0;
		discarded = new ArrayList<Placeable>();
		usedGuileTactics = new ArrayList<GuileTactics>();
	}

	public Column[] getColumns() {
		return columns;
	}

	public Column getColumn(int index) {
		if ((index < 0) || (index >= MAX_COLUMN)) return null;
		return columns[index];
	}

	public Side getTurn() {
		return turn;
	}

	public Phase getPhase() {
		return phase;
	}

	private Troop[] calcAvailableTroops() {
		EnumMap<Color, boolean[]> map = new EnumMap<Color, boolean[]>(Color.class);
		for (Color color : Color.values()) {
			boolean[] available = new boolean[Troop.MAX_VALUE];
			for (int i = 0; i < Troop.MAX_VALUE; i++) {
				available[i] = true;
			}
			map.put(color, available);
		}
		for (Side side : Side.values()) {
			for (int i = 0; i < columns.length; i++) {
				for (int j = 0; j < 4; j++) {
					Placeable card = columns[i].getCard(side, j);
					if (card instanceof Troop) {
						Troop troop = (Troop) card;
						map.get(troop.getColor())[troop.getValue()] = false;
					}
				}
			}
		}
		for (Placeable card : discarded) {
			if (card instanceof Troop) {
				Troop troop = (Troop) card;
				map.get(troop.getColor())[troop.getValue()] = false;
			}
		}
		ArrayList<Troop> availableTroops = new ArrayList<Troop>();
		for (Color color : Color.values()) {
			boolean[] available = map.get(color);
			for (int i = 0; i < Troop.MAX_VALUE; i++) {
				if (available[i]) {
					availableTroops.add(new Troop(color, i));
				}
			}
		}
		return availableTroops.toArray(new Troop[availableTroops.size()]);
	}

	private boolean isWin() {
		int count = 0;
		int total = 0;
		Flag flag = (turn == Side.PERSIA) ? Flag.PERSIA : (turn == Side.MACEDONIA) ? Flag.MACEDONIA : null;
		for (Column column : columns) {
			if (column.getFlag() == flag) {
				count++;
				total++;
				if (count == 3) return true;
				if (total == 5) return true;
			} else {
				count = 0;
			}
		}
		return false;
	}

	public boolean resolveFlag(int columnIndex) {
		if ((columnIndex < 0) || (columnIndex >= MAX_COLUMN)) return false;
		boolean success = columns[columnIndex].resolveFlag(turn, calcAvailableTroops());
		if (success) {
			if (isWin()) {
				phase = Phase.GAME_OVER;
			}
		}
		return success;
	}

	public Hand getHand(Side side) {
		switch (side) {
		case PERSIA:
			return persia;
		case MACEDONIA:
			return macedonia;
		default:
			return null;
		}
	}

	private void changeTurn() {
		switch (turn) {
		case PERSIA:
			turn = Side.MACEDONIA;
			break;
		case MACEDONIA:
			turn = Side.PERSIA;
			break;
		}
		phase = Phase.ACTION;
	}

	private boolean drawCard(Deck<?> deck) {
		if (phase != Phase.DRAW) return false;
		if (!deck.drawable()) return false;
		Hand hand = getHand(turn);
		if (!hand.drawable()) return false;
		hand.add(deck.draw());
		changeTurn();
		return true;
	}

	public boolean drawTroop() {
		return drawCard(troopDeck);
	}

	public boolean drawTactics() {
		return drawCard(tacticsDeck);
	}

	public boolean putCard(int handIndex, int columnIndex) {
		if ((handIndex < 0) || (handIndex >= Hand.MAX_HAND)) return false;
		if ((columnIndex < 0) || (columnIndex >= MAX_COLUMN)) return false;
		if (phase != Phase.ACTION) return false;
		Hand hand = getHand(turn);
		Card card = hand.get(handIndex);
		if (card instanceof Placeable) {
			if ((card instanceof MoraleTactics) && (!canPlayTactics())) return false;
			if ((card instanceof Leader) && (!canPlayLeader())) return false;
			Placeable placeableCard = (Placeable) card;
			boolean success = columns[columnIndex].putCard(turn, placeableCard);
			if (success) {
				hand.remove(handIndex);
				if (card instanceof MoraleTactics) {
					updateTacticsCount();
					if (card instanceof Leader) {
						updateLeaderCount();
					}
				}
				phase = Phase.DRAW;
				return true;
			}
		} else if (card instanceof EnvironmentTactics) {
			if (!canPlayTactics()) return false;
			EnvironmentTactics environmentTactics = (EnvironmentTactics) card;
			boolean success = columns[columnIndex].putEnvironment(environmentTactics);
			if (success) {
				hand.remove(handIndex);
				updateTacticsCount();
				phase = Phase.DRAW;
				return true;
			}
		}
		return false;
	}

	public int getTroopDeckSize() {
		return troopDeck.size();
	}

	public int getTacticsDeckSize() {
		return tacticsDeck.size();
	}

	public Placeable[] getDiscarded() {
		return discarded.toArray(new Placeable[discarded.size()]);
	}

	public GuileTactics[] getUsedGuileTactics() {
		return usedGuileTactics.toArray(new GuileTactics[usedGuileTactics.size()]);
	}

	private boolean canPlayLeader() {
		return Math.abs(leaderCount + getTacticsDelta(turn)) < 2;
	}

	private void updateLeaderCount() {
		leaderCount += getTacticsDelta(turn);
	}

	private int getTacticsDelta(Side turn) {
		return (turn == Side.PERSIA) ? 1 : (turn == Side.MACEDONIA) ? -1 : 0;
	}

	private boolean canPlayTactics() {
		return Math.abs(tacticsCount + getTacticsDelta(turn)) < 2;
	}

	private void updateTacticsCount() {
		tacticsCount += getTacticsDelta(turn);
	}

	public boolean playCard(int handIndex, int srcColumnIndex, int srcCardIndex, int dstColumnIndex) {
		if ((handIndex < 0) || (handIndex >= Hand.MAX_HAND)) return false;
		if ((srcColumnIndex < 0) || (srcColumnIndex >= MAX_COLUMN)) return false;
		if (phase != Phase.ACTION) return false;
		if (!canPlayTactics()) return false;
		if (columns[srcColumnIndex].getFlag() != Flag.UNCLAIMED) return false;
		Hand hand = getHand(turn);
		Card card = hand.get(handIndex);
		Side opposite = (turn == Side.PERSIA) ? Side.MACEDONIA : (turn == Side.MACEDONIA) ? Side.PERSIA : null;
		if (card instanceof Redeploy) {
			Placeable src = columns[srcColumnIndex].getCard(turn, srcCardIndex);
			if (src == null) return false;
			if ((dstColumnIndex < 0) || (dstColumnIndex >= MAX_COLUMN)) {
				columns[srcColumnIndex].removeCard(turn, srcCardIndex);
				discarded.add(src);
				hand.remove(handIndex);
				usedGuileTactics.add((Redeploy) card);
				updateTacticsCount();
				phase = Phase.DRAW;
				return true;
			} else {
				boolean success = columns[dstColumnIndex].putCard(turn, src);
				if (success) {
					columns[srcColumnIndex].removeCard(turn, srcCardIndex);
					hand.remove(handIndex);
					usedGuileTactics.add((Redeploy) card);
					updateTacticsCount();
					phase = Phase.DRAW;
					return true;
				}
			}
		} else if (card instanceof Deserter) {
			Placeable src = columns[srcColumnIndex].getCard(opposite, srcCardIndex);
			if (src == null) return false;
			columns[srcColumnIndex].removeCard(opposite, srcCardIndex);
			discarded.add(src);
			hand.remove(handIndex);
			usedGuileTactics.add((Deserter) card);
			updateTacticsCount();
			phase = Phase.DRAW;
			return true;
		} else if (card instanceof Traitor) {
			if ((dstColumnIndex < 0) || (dstColumnIndex >= MAX_COLUMN)) return false;
			Placeable src = columns[srcColumnIndex].getCard(opposite, srcCardIndex);
			if (src == null) return false;
			if (columns[srcColumnIndex].getCard(opposite, srcCardIndex) instanceof Troop) {
				boolean success = columns[dstColumnIndex].putCard(turn, src);
				if (success) {
					columns[srcColumnIndex].removeCard(opposite, srcCardIndex);
					hand.remove(handIndex);
					usedGuileTactics.add((Traitor) card);
					updateTacticsCount();
					phase = Phase.DRAW;
					return true;
				}
			}
		}
		return false;
	}

	public boolean scoutDeck(int handIndex, int troopLength) {
		if ((handIndex < 0) || (handIndex >= Hand.MAX_HAND)) return false;
		if ((troopLength < 0) || (troopLength > Scout.LENGTH)) return false;
		if (phase != Phase.ACTION) return false;
		if (!canPlayTactics()) return false;
		Hand hand = getHand(turn);
		Card card = hand.get(handIndex);
		if (card instanceof Scout) {
			if (troopDeck.size() < troopLength) return false;
			if (tacticsDeck.size() < Scout.LENGTH - troopLength) return false;
			// TODO: troopDeck.size() + tacticsDeck.size() < Scout.LENGTH
			hand.remove(handIndex);
			usedGuileTactics.add((Scout) card);
			updateTacticsCount();
			for (int i = 0; i < troopLength; i++) {
				hand.addForScout(troopDeck.draw());
			}
			for (int i = 0; i < Scout.LENGTH - troopLength; i++) {
				hand.addForScout(tacticsDeck.draw());
			}
			phase = Phase.RETURN_CARD;
			return true;
		}
		return false;
	}

	public boolean returnCards(int[] handIndices) {
		if (handIndices.length != Scout.LENGTH - 1) return false;
		if (phase != Phase.RETURN_CARD) return false;
		int duplicationChecker = 0;
		for (int i = 0; i < handIndices.length; i++) {
			int tmp = 1 << handIndices[i];
			if ((tmp & duplicationChecker) != 0) return false;
			duplicationChecker |= tmp;
		}
		Hand hand = getHand(turn);
		Card[] cards = new Card[handIndices.length];
		for (int i = 0; i < cards.length; i++) {
			if ((handIndices[i] < 0) || handIndices[i] >= hand.size()) return false;
			cards[i] = hand.get(handIndices[i]);
		}
		for (Card card : cards) {
			if (card instanceof Troop) {
				troopDeck.returnCard((Troop) card);
			} else if (card instanceof Tactics) {
				tacticsDeck.returnCard((Tactics) card);
			} else {
				throw new IllegalStateException();
			}
			boolean success = hand.remove(card);
			if (!success) {
				throw new IllegalStateException();
			}
		}
		changeTurn();
		return true;
	}

	private boolean hasTroop() {
		Hand hand = getHand(turn);
		for (int i = 0; i < hand.size(); i++) {
			if (hand.get(i) instanceof Troop) {
				return true;
			}
		}
		return false;
	}

	private boolean hasFreeSpace() {
		for (int i = 0; i < MAX_COLUMN; i++) {
			if (columns[i].hasFreeSpace(turn)) {
				return true;
			}
		}
		return false;
	}

	public boolean passTurn() {
		if (phase != Phase.ACTION) return false;
		if (hasTroop() && hasFreeSpace()) return false;
		changeTurn();
		return true;
	}

	public boolean endTurnWithoutDraw() {
		if (phase != Phase.DRAW) return false;
		if (troopDeck.drawable()) return false;
		if (tacticsDeck.drawable()) return false;
		changeTurn();
		return true;
	}
}
