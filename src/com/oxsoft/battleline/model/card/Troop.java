package com.oxsoft.battleline.model.card;

import com.oxsoft.battleline.model.Placeable;

/**
 * A player selects one of his Troop cards and places it face up on his side of
 * the line adjacent to one Flag. There are three imaginary card "slots" on each
 * side of the flag. In the course of the game each player may place a maximum
 * of three Troop cards adjacent to each Flag, in order to create formations.
 * The order in which the cards are played is irrelevant. To save space, later
 * cards are placed partially covering former cards at the same flag.
 * 
 * When a player is unable to play any Troop card, he may pass or play a Tactics
 * card on his turn, but his opponent still continues to play until the game is
 * decided. Such situations may occur in two ways: a player may have filled all
 * his available slots on his side of the battle line, or a player may have only
 * Tactics cards in his hand.
 */
public class Troop extends Card implements Placeable {
	public static final int MAX_VALUE = 10;
	private final Color color;
	private final int value;

	public Troop(Color color, int value) {
		if ((value < 0) || (value >= MAX_VALUE)) throw new IllegalArgumentException();
		this.color = color;
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Troop other = (Troop) obj;
		if (color != other.color) return false;
		if (value != other.value) return false;
		return true;
	}

	public Color getColor() {
		return color;
	}

	public int getValue() {
		return value;
	}

	@Override
	public Troop[] getAvailableTroops() {
		return new Troop[] { this };
	}
}
