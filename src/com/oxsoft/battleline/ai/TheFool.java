package com.oxsoft.battleline.ai;

import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.card.Troop;

public class TheFool extends ArtificialIntelligence {

	public TheFool(GameStateInput gameStateInput) {
		super(gameStateInput);
	}

	@Override
	public void action(GameStateContainer gsc) {
		if (input.passTurn()) return;
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
}
