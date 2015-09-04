package com.oxsoft.battleline.network;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

import com.google.gson.Gson;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.container.MethodContainer;
import com.oxsoft.battleline.view.GameView;
import com.oxsoft.battleline.view.GameView.EventListener;
import com.oxsoft.battleline.view.GameView.OnEndTurnListener;
import com.oxsoft.battleline.view.GameView.OnPlayCardListener;
import com.oxsoft.battleline.view.GameView.OnPutCardListener;
import com.oxsoft.battleline.view.GameView.OnResolveFlagListener;
import com.oxsoft.battleline.view.GameView.OnReturnCardsListener;
import com.oxsoft.battleline.view.GameView.OnScoutDeckListener;

public class Client {
	private static final int READ_MAX = 16777216;
	private boolean isRunning = true;
	private final Socket socket;
	private final InputStreamReader inputStreamReader;
	private final OutputStreamWriter outputStreamWriter;
	private final GameView gameView;

	public Client(String host, int port) throws IOException {
		socket = new Socket(host, port);
		inputStreamReader = new InputStreamReader(socket.getInputStream(), Constants.CHARSET);
		outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), Constants.CHARSET);
		gameView = new GameView(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
				super.windowClosing(e);
			}
		});
		final Gson gson = new Gson();
		gameView.setOnPutCardListener(new OnPutCardListener() {
			@Override
			public void onPutCard(int handIndex, int columnIndex) {
				write(gson.toJson(new MethodContainer(handIndex, columnIndex)));
			}
		});
		gameView.setOnPlayCardListener(new OnPlayCardListener() {
			@Override
			public void onPlayCard(int handIndex, int srcColumnIndex, int srcCardIndex, int dstColumnIndex) {
				write(gson.toJson(new MethodContainer(handIndex, srcColumnIndex, srcCardIndex, dstColumnIndex)));
			}
		});
		gameView.setOnDrawTroopListener(new EventListener() {
			@Override
			public void onEvent() {
				write(gson.toJson(new MethodContainer(Method.DRAW_TROOP_DECK)));
			}
		});
		gameView.setOnDrawTacticsListener(new EventListener() {
			@Override
			public void onEvent() {
				write(gson.toJson(new MethodContainer(Method.DRAW_TACTICS_DECK)));
			}
		});
		gameView.setOnResolveFlagListener(new OnResolveFlagListener() {

			@Override
			public void onResolveFlag(int columnIndex) {
				write(gson.toJson(new MethodContainer(Method.RESOLVE_FLAG, columnIndex)));
			}
		});
		gameView.setOnScoutDeckListener(new OnScoutDeckListener() {

			@Override
			public void onScoutDeck(int handIndex, int troopLength) {
				write(gson.toJson(new MethodContainer(handIndex, troopLength)));
			}
		});
		gameView.setOnReturnCardsListener(new OnReturnCardsListener() {
			@Override
			public void onReturnCards(int[] handIndices) {
				write(gson.toJson(new MethodContainer(handIndices)));
			}
		});
		gameView.setOnEndTurnListener(new OnEndTurnListener() {
			@Override
			public void onEndTurn() {
				write(gson.toJson(new MethodContainer(Method.END_TURN)));
			}
		});
		gameView.updateStatus("Waiting for opponent...");
		new Thread() {
			@Override
			public void run() {
				input();
			}
		}.start();
	}

	private void close() {
		try {
			outputStreamWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		isRunning = false;
	}

	private void input() {
		try {
			char[] data = new char[READ_MAX];
			while (isRunning) {
				int length = inputStreamReader.read(data, 0, READ_MAX);
				if (length >= 0) {
					Gson gson = new Gson();
					GameStateContainer gameStateContainer = gson.fromJson(new String(data, 0, length), GameStateContainer.class);
					gameView.update(gameStateContainer);
				}
			}
		} catch (SocketException e) {
			// Socket closed
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			inputStreamReader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void write(String data) {
		try {
			outputStreamWriter.write(data);
			outputStreamWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
