package com.oxsoft.battleline.model;

import java.util.ArrayList;

import com.oxsoft.battleline.model.card.Card;

public abstract class Deck<T extends Card> {
	protected ArrayList<T> cards;

	public final T draw() {
		if (cards.size() == 0) return null;
		return cards.remove(0);
	}

	public final int size() {
		return cards.size();
	}

	public final boolean drawable() {
		return cards.size() > 0;
	}

	public final boolean returnCard(T card) {
		cards.add(0, card);
		return true;
	}
}