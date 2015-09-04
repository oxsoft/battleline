package com.oxsoft.battleline.model;

import com.oxsoft.battleline.model.card.Troop;

public interface Placeable {
	public Troop[] getAvailableTroops();
}
