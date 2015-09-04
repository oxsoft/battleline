package com.oxsoft.battleline.ai;

import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.GameState;

public abstract class ArtificialIntelligence {
	protected final GameStateInput input;

	public ArtificialIntelligence(GameStateInput gameStateInput) {
		input = gameStateInput;
	}

	public abstract void action(GameStateContainer gsc);

	public abstract void draw(GameStateContainer gsc);

	public abstract void returnCard(GameStateContainer gsc);

	public void resolveFlags(GameStateContainer gsc) {
		for (int i = 0; i < GameState.MAX_COLUMN; i++) {
			input.resolveFlag(i);
		}
	}

	public void win(GameStateContainer gsc) {
		// YEAH!
	}

	public void lose(GameStateContainer gsc) {
		// OH MY GOD!
	}
}
