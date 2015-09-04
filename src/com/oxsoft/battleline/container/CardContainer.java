package com.oxsoft.battleline.container;

import com.oxsoft.battleline.model.Hand;
import com.oxsoft.battleline.model.Placeable;
import com.oxsoft.battleline.model.card.Card;
import com.oxsoft.battleline.model.card.Color;
import com.oxsoft.battleline.model.card.GuileTactics;
import com.oxsoft.battleline.model.card.Tactics;
import com.oxsoft.battleline.model.card.Troop;

public class CardContainer {
	public String type;
	public Color color;
	public Integer value;

	public CardContainer(String type, Color color, Integer value) {
		this.type = type;
		this.color = color;
		this.value = value;
	}

	public CardContainer(Card card, boolean secret) {
		if (card == null) {
			type = null;
			color = null;
			value = null;
			return;
		}
		if (secret) {
			type = (card instanceof Troop) ? Troop.class.getSimpleName() : Tactics.class.getSimpleName();
		} else {
			type = card.getClass().getSimpleName();
			if (card instanceof Troop) {
				Troop troop = (Troop) card;
				color = troop.getColor();
				value = troop.getValue();
			}
		}
	}

	public static CardContainer[] getCardContainers(Placeable[] cards, boolean secret) {
		CardContainer[] cardContainers = new CardContainer[cards.length];
		for (int i = 0; i < cards.length; i++) {
			cardContainers[i] = new CardContainer((Card) cards[i], secret);
		}
		return cardContainers;
	}

	public static CardContainer[] getCardContainers(GuileTactics[] cards, boolean secret) {
		CardContainer[] cardContainers = new CardContainer[cards.length];
		for (int i = 0; i < cards.length; i++) {
			cardContainers[i] = new CardContainer((GuileTactics) cards[i], secret);
		}
		return cardContainers;
	}

	public static CardContainer[] getCardContainers(Hand hand, boolean secret) {
		CardContainer[] cardContainers = new CardContainer[hand.size()];
		for (int i = 0; i < cardContainers.length; i++) {
			cardContainers[i] = new CardContainer(hand.get(i), secret);
		}
		return cardContainers;
	}
}
