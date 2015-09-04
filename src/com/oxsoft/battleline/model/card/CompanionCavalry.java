package com.oxsoft.battleline.model.card;

import java.util.ArrayList;

/**
 * Play this card like any Troop card of value 8, but define its color when the
 * Flag is resolved.
 */
public class CompanionCavalry extends MoraleTactics {
	private static Troop[] availableTroops = null;

	@Override
	public Troop[] getAvailableTroops() {
		if (availableTroops == null) {
			ArrayList<Troop> troops = new ArrayList<Troop>();
			for (Color color : Color.values()) {
				troops.add(new Troop(color, 7));
			}
			availableTroops = troops.toArray(new Troop[troops.size()]);
		}
		return availableTroops;
	}
}
