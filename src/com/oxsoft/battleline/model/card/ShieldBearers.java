package com.oxsoft.battleline.model.card;

import java.util.ArrayList;

/**
 * Play this card like any Troop card, but define its color and its value not
 * larger than 3 when the Flag is resolved.
 */
public class ShieldBearers extends MoraleTactics {
	private static Troop[] availableTroops = null;

	@Override
	public Troop[] getAvailableTroops() {
		if (availableTroops == null) {
			ArrayList<Troop> troops = new ArrayList<Troop>();
			for (Color color : Color.values()) {
				for (int i = 0; i < 3; i++) {
					troops.add(new Troop(color, i));
				}
			}
			availableTroops = troops.toArray(new Troop[troops.size()]);
		}
		return availableTroops;
	}
}
