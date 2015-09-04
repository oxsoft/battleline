package com.oxsoft.battleline.container;

import com.oxsoft.battleline.network.Method;

public class MethodContainer {
	public Method method;
	public Integer handIndex;
	public Integer dstColumnIndex;
	public Integer srcColumnIndex;
	public Integer srcCardIndex;
	public Integer troopLength;
	public int[] handIndices;

	public MethodContainer(Method method) {
		// Method.DRAW_TROOP_DECK or
		// Method.DRAW_TACTICS_DECK or
		// Method.END_TURN
		this.method = method;
	}

	public MethodContainer(int handIndex, int dstColumnIndex) {
		this.method = Method.PUT_CARD;
		this.handIndex = handIndex;
		this.dstColumnIndex = dstColumnIndex;
	}

	public MethodContainer(int handIndex, int dstColumnIndex, int srcColumnIndex, int srcCardIndex) {
		this.method = Method.PLAY_CARD;
		this.handIndex = handIndex;
		this.dstColumnIndex = dstColumnIndex;
		this.srcColumnIndex = srcColumnIndex;
		this.srcCardIndex = srcCardIndex;
	}

	public MethodContainer(Method method, int dstColumnIndex) {
		this.method = method; // Method.RESOLVE_FLAG
		this.dstColumnIndex = dstColumnIndex;
	}

	public MethodContainer(int troopLength) {
		this.method = Method.SCOUT_DECK;
		this.troopLength = troopLength;
	}

	public MethodContainer(int[] handIndices) {
		this.method = Method.RETURN_CARD;
		this.handIndices = handIndices;
	}
}
