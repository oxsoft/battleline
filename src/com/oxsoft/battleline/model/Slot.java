package com.oxsoft.battleline.model;

import java.util.ArrayList;

public class Slot {
	private final ArrayList<Placeable> cards;

	public Slot() {
		cards = new ArrayList<Placeable>();
	}

	public boolean add(Placeable card) {
		return cards.add(card);
	}

	public Placeable remove(int index) {
		return cards.remove(index);
	}

	public Placeable getCard(int index) {
		if (index < 0) throw new IllegalArgumentException();
		if (index >= cards.size()) return null;
		return cards.get(index);
	}

	public Placeable[] getCards() {
		return cards.toArray(new Placeable[cards.size()]);
	}

	public int size() {
		return cards.size();
	}
}
