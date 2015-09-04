package com.oxsoft.battleline.model.card;

import java.util.ArrayList;

/**
 * The Leaders are wild cards. Play a Leader like any Troop card, but define the
 * color and value when the Flag is resolved. For example, you have a Blue 8 in
 * your layout. If you add a Leader, it gives you the option of adding a Blue 6,
 * 7, 9 or 10 to make a Wedge, or an 8 to form a Phalanx. Each player may only
 * have one Leader on his side of the battle line. If drawn, a second Leader
 * remains unplayable in the player's hand until the end of the game.
 */
public abstract class Leader extends MoraleTactics {
	private static Troop[] availableTroops = null;

	@Override
	public Troop[] getAvailableTroops() {
		if (availableTroops == null) {
			ArrayList<Troop> troops = new ArrayList<Troop>();
			for (Color color : Color.values()) {
				for (int i = 0; i < Troop.MAX_VALUE; i++) {
					troops.add(new Troop(color, i));
				}
			}
			availableTroops = troops.toArray(new Troop[troops.size()]);
		}
		return availableTroops;
	}
}
