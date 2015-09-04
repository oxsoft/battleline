package com.oxsoft.battleline.network;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.container.MethodContainer;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.Phase;
import com.oxsoft.battleline.model.Side;

public class Server {
	private final ArrayList<Player> players;
	private final GameState gameState;

	private boolean isRunning = true;

	public Server(int port) throws IOException {
		ServerView serverView = new ServerView(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
				super.windowClosing(e);
			}
		});
		InetAddress inetAddress = InetAddress.getLocalHost();
		serverView.print("Waiting players at " + inetAddress.getHostAddress() + " : " + port);
		ServerSocket listener = new ServerSocket();
		listener.setReuseAddress(true);
		listener.bind(new InetSocketAddress(port));
		players = new ArrayList<Player>();
		while (players.size() < 2) {
			Socket socket = listener.accept();
			Side side = (players.size() == 0) ? Side.PERSIA : Side.MACEDONIA;
			players.add(new Player(socket, side));
			serverView.print("Connected to Player " + players.size() + " [" + socket.getInetAddress().getHostName() + "(" + socket.getInetAddress().getHostAddress() + ")]");
		}
		listener.close();
		serverView.print("Game start!");
		gameState = new GameState();
		broadcastGameState();
		startInputThreads();
	}

	private void close() {
		isRunning = false;
		for (Player player : players) {
			try {
				player.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void broadcastGameState() {
		try {
			for (Player player : players) {
				Gson gson = new Gson();
				String json = gson.toJson(new GameStateContainer(gameState, player.getSide()));
				player.write(json);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startInputThreads() {
		for (final Player player : players) {
			new Thread() {
				@Override
				public void run() {
					while (isRunning) {
						try {
							String input = player.read();
							if (input != null) {
								System.out.println(input);
								if (player.getSide() != gameState.getTurn()) continue;
								Gson gson = new Gson();
								MethodContainer mc = gson.fromJson(input, MethodContainer.class);
								// TODO: Validation
								boolean success = false;
								switch (mc.method) {
								case PUT_CARD:
									success = gameState.putCard(mc.handIndex, mc.dstColumnIndex);
									break;
								case PLAY_CARD:
									success = gameState.playCard(mc.handIndex, mc.srcColumnIndex, mc.srcCardIndex, mc.dstColumnIndex);
									break;
								case DRAW_TROOP_DECK:
									success = gameState.drawTroop();
									break;
								case DRAW_TACTICS_DECK:
									success = gameState.drawTactics();
									break;
								case SCOUT_DECK:
									success = gameState.scoutDeck(mc.handIndex, mc.troopLength);
									break;
								case RETURN_CARD:
									success = gameState.returnCards(mc.handIndices);
									break;
								case RESOLVE_FLAG:
									success = gameState.resolveFlag(mc.dstColumnIndex);
									break;
								case END_TURN:
									success = (gameState.getPhase() == Phase.ACTION) ? gameState.passTurn() : (gameState.getPhase() == Phase.DRAW) ? gameState.endTurnWithoutDraw() : false;
									break;
								}
								if (success) broadcastGameState();
							}
						} catch (SocketException e) {
							// Socket closed
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JsonSyntaxException e) {
							// Invalid input
						}
					}
					try {
						player.closeInput();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}
