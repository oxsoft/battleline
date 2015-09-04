package com.oxsoft.battleline.container;

import com.oxsoft.battleline.model.Column;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.Phase;
import com.oxsoft.battleline.model.Side;

public class GameStateContainer {
	public ColumnContainer[] columns;
	public CardContainer[] myHand;
	public CardContainer[] opponentHand;
	public Boolean myTurn;
	public Integer troopDeck;
	public Integer tacticsDeck;
	public CardContainer[] discarded;
	public CardContainer[] usedGuileTactics;
	public Phase phase;

	public GameStateContainer(GameState gameState, Side side) {
		Column[] columnArray = gameState.getColumns();
		columns = new ColumnContainer[columnArray.length];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new ColumnContainer(columnArray[i], side);
		}
		myHand = CardContainer.getCardContainers(gameState.getHand(side), false);
		Side opposite = (side == Side.PERSIA) ? Side.MACEDONIA : Side.PERSIA;
		opponentHand = CardContainer.getCardContainers(gameState.getHand(opposite), true);
		myTurn = gameState.getTurn() == side;
		troopDeck = gameState.getTroopDeckSize();
		tacticsDeck = gameState.getTacticsDeckSize();
		discarded = CardContainer.getCardContainers(gameState.getDiscarded(), false);
		usedGuileTactics = CardContainer.getCardContainers(gameState.getUsedGuileTactics(), false);
		phase = gameState.getPhase();
	}
}
