package com.oxsoft.battleline.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import com.oxsoft.battleline.container.FlagContainer;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.card.Color;
import com.oxsoft.battleline.model.card.Troop;

public class DrWedge extends ArtificialIntelligence {

	public DrWedge(GameStateInput gameStateInput) {
		super(gameStateInput);
	}

	@Override
	public void action(GameStateContainer gsc) {
		if (input.passTurn()) return;
		// count opponent troop
		int opponentHand = 0;
		for (int i = 0; i < gsc.opponentHand.length; i++) {
			if (gsc.opponentHand[i].type.equals(Troop.class.getSimpleName())) {
				opponentHand++;
			}
		}
		// initialize probability
		EnumMap<Color, double[]> probability = new EnumMap<Color, double[]>(Color.class);
		for (Color color : Color.values()) {
			double[] value = new double[Troop.MAX_VALUE];
			for (int i = 0; i < Troop.MAX_VALUE; i++) {
				value[i] = (double) (gsc.troopDeck / 2) / (gsc.troopDeck + opponentHand);
			}
			probability.put(color, value);
		}
		// check hand
		for (int i = 0; i < gsc.myHand.length; i++) {
			if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
				probability.get(gsc.myHand[i].color)[gsc.myHand[i].value] = 1.0;
			}
		}
		// check field
		for (int i = 0; i < gsc.columns.length; i++) {
			for (int j = 0; j < gsc.columns[i].me.length; j++) {
				if (gsc.columns[i].me[j].type.equals(Troop.class.getSimpleName())) {
					probability.get(gsc.columns[i].me[j].color)[gsc.columns[i].me[j].value] = 0.0;
				}
			}
			for (int j = 0; j < gsc.columns[i].opponent.length; j++) {
				if (gsc.columns[i].opponent[j].type.equals(Troop.class.getSimpleName())) {
					probability.get(gsc.columns[i].opponent[j].color)[gsc.columns[i].opponent[j].value] = 0.0;
				}
			}
		}
		for (int i = 0; i < gsc.discarded.length; i++) {
			if (gsc.discarded[i].type.equals(Troop.class.getSimpleName())) {
				probability.get(gsc.discarded[i].value)[gsc.discarded[i].value] = 0.0;
			}
		}
		ArrayList<Integer> targetColumns = new ArrayList<Integer>();
		ArrayList<Integer> emptyColumns = new ArrayList<Integer>();
		ArrayList<Integer> inconsistentColumns = new ArrayList<Integer>();
		for (int i = 0; i < gsc.columns.length; i++) {

			if (gsc.columns[i].flag == FlagContainer.UNCLAIMED) {
				if (gsc.columns[i].me.length > 0) {
					boolean same = true;
					for (int j = 1; j < gsc.columns[i].me.length; j++) {
						if (!gsc.columns[i].me[j].color.equals(gsc.columns[i].me[0].color)) {
							same = false;
							break;
						}
					}
					if (same) {
						targetColumns.add(i);
					} else {
						if (gsc.columns[i].me.length < 3) inconsistentColumns.add(i);
					}
				} else {
					emptyColumns.add(i);
				}
			}
		}
		double maxProbabilityOfHand = Double.MIN_VALUE;
		double minProbabilityOfHand = Double.MAX_VALUE;
		int maxHand = -1;
		int minHand = -1;
		for (int i = 0; i < gsc.myHand.length; i++) {
			if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
				double p = calcWedgeProbability(probability.get(gsc.myHand[i].color), new int[] { gsc.myHand[i].value }, 3);
				if (p > maxProbabilityOfHand) {
					maxProbabilityOfHand = p;
					maxHand = i;
				}
				if (p < minProbabilityOfHand) {
					minProbabilityOfHand = p;
					minHand = i;
				}
			}
		}
		double maxProbabilityOfColumn = Double.MIN_VALUE;
		double minProbabilityOfColumn = Double.MIN_VALUE;
		int maxHandToColumn = -1;
		int maxColumn = -1;
		int minColumn = -1;
		for (Integer targetColumn : targetColumns) {
			Color color = gsc.columns[targetColumn].me[0].color;
			for (int i = 0; i < gsc.myHand.length; i++) {
				if (gsc.myHand[i].type.equals(Troop.class.getSimpleName())) {
					if (gsc.myHand[i].color == color) {
						int[] troops = new int[gsc.columns[targetColumn].me.length + 1];
						troops[0] = gsc.myHand[i].value;
						for (int j = 1; j < troops.length; j++) {
							troops[j] = gsc.columns[targetColumn].me[j - 1].value;
						}
						double p = calcWedgeProbability(probability.get(color), troops, 3);
						if (p > maxProbabilityOfColumn) {
							maxProbabilityOfColumn = p;
							maxHandToColumn = i;
							maxColumn = targetColumn;
						}
					}
				}
			}
			int[] troops = new int[gsc.columns[targetColumn].me.length];
			for (int j = 0; j < troops.length; j++) {
				troops[j] = gsc.columns[targetColumn].me[j].value;
			}
			double p = calcWedgeProbability(probability.get(color), troops, 3);
			if (p < minProbabilityOfColumn) {
				minProbabilityOfColumn = p;
				minColumn = targetColumn;
			}
		}
		// Grow Wedge
		if (maxProbabilityOfColumn > 0.3) {
			boolean success = input.putCard(maxHandToColumn, maxColumn);
			if (success) return;
		}
		// Make new Wedge
		if ((maxProbabilityOfHand > 0.3) && (emptyColumns.size() > 0)) {
			boolean success = input.putCard(maxHand, emptyColumns.get((int) (Math.random() * emptyColumns.size())));
			if (success) return;
		}
		// Grow Wedge
		if (maxProbabilityOfColumn > 1e-5) {
			boolean success = input.putCard(maxHandToColumn, maxColumn);
			if (success) return;
		}
		// Trash card
		if (inconsistentColumns.size() > 0) {
			boolean success = input.putCard(minHand, inconsistentColumns.get((int) (Math.random() * inconsistentColumns.size())));
			if (success) return;
		}
		// Make new trash column
		if (minProbabilityOfColumn < 1e-5) {
			boolean success = input.putCard(minHand, minColumn);
			if (success) return;
		}
		// Make new Wedge
		if ((maxProbabilityOfHand > 1e-5) && (emptyColumns.size() > 0)) {
			boolean success = input.putCard(maxHand, emptyColumns.get((int) (Math.random() * emptyColumns.size())));
			if (success) return;
		}
		// Make new trash column
		boolean success = input.putCard(minHand, minColumn);
		if (success) return;
		// last resort for safety
		int handIndex = -1;
		while (handIndex == -1) {
			int h = (int) (Math.random() * gsc.myHand.length);
			if (gsc.myHand[h].type.equals(Troop.class.getSimpleName())) handIndex = h;
		}
		success = false;
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

	private double calcWedgeProbability(double[] probabilities, int[] values, int length) {
		if ((values.length == 0) || (values.length > length)) return 0.0;
		double probability = 1.0;
		for (int i = 0; i < length; i++) {
			int start = values[0] + i - length + 1;
			int end = values[0] + i;
			if ((start < 0) || (end >= Troop.MAX_VALUE)) continue;
			boolean include = true;
			for (int j = 1; j < values.length; j++) {
				if ((values[j] < start) || (values[j] > end)) include = false;
			}
			if (!include) continue;
			double[] correctedProbabilities = Arrays.copyOf(probabilities, probabilities.length);
			for (int j = 0; j < values.length; j++) {
				correctedProbabilities[values[j]] = 1.0;
			}
			double p = 1.0;
			for (int j = 0; j < length; j++) {
				p *= correctedProbabilities[start + j];
			}
			probability *= 1.0 - p;
		}
		return 1.0 - probability;
	}
}
