package com.oxsoft.battleline.model;

/**
 * On his turn and before drawing his card from the deck, a player may claim one
 * or more Flags. In order to do so, he must have a completed formation of three
 * cards on his side of the Flag, and he must be able to prove that Troop cards
 * on the opponent's side of the Flag cannot beat this formation.
 * 
 * If the opponent's side also contains three cards, the situation is obvious.
 * If the opponent'> side contains less than three cards, the claiming player
 * must prove that the opponent will not be able to create a winning formation,
 * regardless of what Troop cards the opponent might play. For this purpose, the
 * player may use the open information from the layout to show that certain
 * cards are no longer available. The player may not use any information from
 * his own hand. Unplayed Tactics cannot prevent a Flag claim.
 * 
 * If the formations on either side of a Flag arc tied or at best could be tied,
 * then the player who played (or would play) the last card into the formations
 * loses the Flag. His opponent may claim it on his next turn.
 * 
 * When a player successfully claims a Flag, he takes the Flag and moves it to
 * his side, beyond the cards that he played. Then neither player may place
 * further cards adjacent to that Flag, nor may any Tactics card affect those
 * cards.
 */
public enum Flag {
	UNCLAIMED, PERSIA, MACEDONIA
}
