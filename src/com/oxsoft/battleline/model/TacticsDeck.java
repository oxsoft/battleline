package com.oxsoft.battleline.model;

import java.util.ArrayList;
import java.util.Collections;

import com.oxsoft.battleline.model.card.Alexander;
import com.oxsoft.battleline.model.card.CompanionCavalry;
import com.oxsoft.battleline.model.card.Darius;
import com.oxsoft.battleline.model.card.Deserter;
import com.oxsoft.battleline.model.card.Fog;
import com.oxsoft.battleline.model.card.Mud;
import com.oxsoft.battleline.model.card.Redeploy;
import com.oxsoft.battleline.model.card.Scout;
import com.oxsoft.battleline.model.card.ShieldBearers;
import com.oxsoft.battleline.model.card.Tactics;
import com.oxsoft.battleline.model.card.Traitor;

public class TacticsDeck extends Deck<Tactics> {

	public TacticsDeck() {
		cards = new ArrayList<Tactics>();
		cards.add(new Alexander());
		cards.add(new Darius());
		cards.add(new CompanionCavalry());
		cards.add(new ShieldBearers());
		cards.add(new Fog());
		cards.add(new Mud());
		cards.add(new Scout());
		cards.add(new Redeploy());
		cards.add(new Deserter());
		cards.add(new Traitor());
		Collections.shuffle(cards);
	}
}
