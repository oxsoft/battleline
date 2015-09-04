package com.oxsoft.battleline.model;

import java.util.ArrayList;
import java.util.Collections;

import com.oxsoft.battleline.model.card.Color;
import com.oxsoft.battleline.model.card.Troop;

public class TroopDeck extends Deck<Troop> {

	public TroopDeck() {
		cards = new ArrayList<Troop>();
		for (Color color : Color.values()) {
			for (int i = 0; i < Troop.MAX_VALUE; i++) {
				cards.add(new Troop(color, i));
			}
		}
		Collections.shuffle(cards);
	}
}
