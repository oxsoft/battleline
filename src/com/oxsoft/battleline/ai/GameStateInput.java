package com.oxsoft.battleline.ai;

import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.Side;

public class GameStateInput {
	private final GameState gameState;
	private final Side side;

	public GameStateInput(GameState gameState, Side side) {
		this.gameState = gameState;
		this.side = side;
	}

	// -----------------------------------------------------
	// Interface for visualization
	// -----------------------------------------------------

	public static interface OnUpdateListener {
		public void onUpdate();
	}

	private OnUpdateListener onUpdateListener = null;

	public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
		this.onUpdateListener = onUpdateListener;
	}

	// -----------------------------------------------------
	// Action
	// -----------------------------------------------------

	public boolean putCard(int handIndex, int columnIndex) {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.putCard(handIndex, columnIndex);
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	public boolean playCard(int handIndex, int srcColumnIndex, int srcCardIndex, int dstColumnIndex) {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.playCard(handIndex, srcColumnIndex, srcCardIndex, dstColumnIndex);
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	public boolean scoutDeck(int handIndex, int troopLength) {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.scoutDeck(handIndex, troopLength);
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	public boolean passTurn() {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.passTurn();
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	// -----------------------------------------------------
	// Draw
	// -----------------------------------------------------

	public boolean drawTroop() {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.drawTroop();
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	public boolean drawTactics() {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.drawTactics();
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	public boolean endTurnWithoutDraw() {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.endTurnWithoutDraw();
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	// -----------------------------------------------------
	// Return card
	// -----------------------------------------------------

	public boolean returnCards(int[] handIndices) {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.returnCards(handIndices);
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}

	// -----------------------------------------------------
	// Resolve Flag
	// -----------------------------------------------------

	public boolean resolveFlag(int columnIndex) {
		if (gameState.getTurn() != side) return false;
		boolean success = gameState.resolveFlag(columnIndex);
		if (success && onUpdateListener != null) onUpdateListener.onUpdate();
		return success;
	}
}
