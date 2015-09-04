package com.oxsoft.battleline.model;

import java.util.ArrayList;

import com.oxsoft.battleline.model.card.Card;

public class Hand {
	public static final int MAX_HAND = 7;
	private final ArrayList<Card> hand;

	public Hand() {
		hand = new ArrayList<Card>();
	}

	public boolean add(Card card) {
		if (drawable()) return hand.add(card);
		return false;
	}

	public Card get(int index) {
		return hand.get(index);
	}

	public int size() {
		return hand.size();
	}

	public boolean drawable() {
		return hand.size() < MAX_HAND;
	}

	public Card remove(int index) {
		return hand.remove(index);
	}

	public boolean addForScout(Card card) {
		return hand.add(card);
	}

	public boolean remove(Card card) {
		return hand.remove(card);
	}
}
