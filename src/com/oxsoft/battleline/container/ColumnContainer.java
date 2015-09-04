package com.oxsoft.battleline.container;

import com.oxsoft.battleline.model.Column;
import com.oxsoft.battleline.model.Flag;
import com.oxsoft.battleline.model.Side;
import com.oxsoft.battleline.model.card.Card;
import com.oxsoft.battleline.model.card.EnvironmentTactics;

public class ColumnContainer {
	public CardContainer[] me;
	public CardContainer[] opponent;
	public CardContainer[] environments;
	public FlagContainer flag;

	public ColumnContainer(Column column, Side side) {
		me = new CardContainer[column.getCardLength(side)];
		for (int i = 0; i < me.length; i++) {
			me[i] = new CardContainer((Card) column.getCard(side, i), false);
		}
		Side opposite = (side == Side.PERSIA) ? Side.MACEDONIA : Side.PERSIA;
		opponent = new CardContainer[column.getCardLength(opposite)];
		for (int i = 0; i < opponent.length; i++) {
			opponent[i] = new CardContainer((Card) column.getCard(opposite, i), false);
		}
		EnvironmentTactics[] environmentTactics = column.getEnvironmentTactics();
		environments = new CardContainer[environmentTactics.length];
		for (int i = 0; i < environments.length; i++) {
			environments[i] = new CardContainer(environmentTactics[i], false);
		}
		if (column.getFlag() == Flag.UNCLAIMED) {
			flag = FlagContainer.UNCLAIMED;
		} else if (column.getFlag() == Flag.PERSIA) {
			flag = side == Side.PERSIA ? FlagContainer.ME : FlagContainer.OPPONENT;
		} else if (column.getFlag() == Flag.MACEDONIA) {
			flag = side == Side.MACEDONIA ? FlagContainer.ME : FlagContainer.OPPONENT;
		}
	}
}
