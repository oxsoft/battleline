package com.oxsoft.battleline.ai;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import com.oxsoft.battleline.ai.GameStateInput.OnUpdateListener;
import com.oxsoft.battleline.container.CardContainer;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.Phase;
import com.oxsoft.battleline.model.Side;
import com.oxsoft.battleline.view.GameView;
import com.oxsoft.battleline.view.GameView.EventListener;
import com.oxsoft.battleline.view.GameView.OnEndTurnListener;
import com.oxsoft.battleline.view.GameView.OnPlayCardListener;
import com.oxsoft.battleline.view.GameView.OnPutCardListener;
import com.oxsoft.battleline.view.GameView.OnResolveFlagListener;
import com.oxsoft.battleline.view.GameView.OnReturnCardsListener;
import com.oxsoft.battleline.view.GameView.OnScoutDeckListener;

public class AIManager {

	public static void showVsComputerMenu() {
		ArrayList<Class<? extends ArtificialIntelligence>> AIs = AIFinder.getAIs();
		JRadioButton[] radio = new JRadioButton[AIs.size()];
		ButtonGroup group = new ButtonGroup();
		Box verticalBox = Box.createVerticalBox();
		for (int i = 0; i < radio.length; i++) {
			radio[i] = new JRadioButton(AIs.get(i).getSimpleName());
			group.add(radio[i]);
			verticalBox.add(radio[i]);
		}
		radio[0].setSelected(true);
		int status = JOptionPane.showOptionDialog(null, verticalBox, "Select Opponent", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			for (int i = 0; i < radio.length; i++) {
				if (radio[i].isSelected()) {
					try {
						vsComputer(AIs.get(i));
					} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					return;
				}
			}
		}
	}

	public static void showAIvsAIMenu() {
		ArrayList<Class<? extends ArtificialIntelligence>> AIs = AIFinder.getAIs();
		JRadioButton[] radio0 = new JRadioButton[AIs.size()];
		JRadioButton[] radio1 = new JRadioButton[AIs.size()];
		ButtonGroup group0 = new ButtonGroup();
		ButtonGroup group1 = new ButtonGroup();
		Box horizontalBox = Box.createHorizontalBox();
		Box verticalBox0 = Box.createVerticalBox();
		Box verticalBox1 = Box.createVerticalBox();
		verticalBox0.add(new JLabel("AI 0"));
		verticalBox1.add(new JLabel("AI 1"));
		for (int i = 0; i < AIs.size(); i++) {
			radio0[i] = new JRadioButton(AIs.get(i).getSimpleName());
			radio1[i] = new JRadioButton(AIs.get(i).getSimpleName());
			group0.add(radio0[i]);
			group1.add(radio1[i]);
			verticalBox0.add(radio0[i]);
			verticalBox1.add(radio1[i]);
		}
		radio0[0].setSelected(true);
		radio1[0].setSelected(true);
		horizontalBox.add(verticalBox0);
		horizontalBox.add(verticalBox1);
		int status = JOptionPane.showOptionDialog(null, horizontalBox, "Select AIs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			for (int i = 0; i < radio0.length; i++) {
				if (radio0[i].isSelected()) {
					for (int j = 0; j < radio1.length; j++) {
						if (radio1[j].isSelected()) {
							try {
								watchAI(AIs.get(i), AIs.get(j), false);
							} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
							}
							return;
						}
					}
					return;
				}
			}
		}
	}

	private static void think() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void playAI(final ArtificialIntelligence ai, final GameState gameState, final Side side) {
		if (gameState.getTurn() != side) return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (gameState.getPhase() == Phase.ACTION) {
					think();
					ai.resolveFlags(new GameStateContainer(gameState, side));
					if (gameState.getPhase() == Phase.GAME_OVER) {
						ai.win(new GameStateContainer(gameState, side));
					} else {
						ai.action(new GameStateContainer(gameState, side));
						if (gameState.getTurn() == side) {
							if (gameState.getPhase() == Phase.ACTION) throw new IllegalStateException();
							else if (gameState.getPhase() == Phase.DRAW) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										think();
										ai.resolveFlags(new GameStateContainer(gameState, side));
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												think();
												ai.draw(new GameStateContainer(gameState, side));
												if (gameState.getTurn() == side) {
													if (gameState.getPhase() == Phase.GAME_OVER) {
														ai.win(new GameStateContainer(gameState, side));
														// TODO
													} else {
														throw new IllegalStateException();
													}
												}
											}
										});
									}
								});
							} else if (gameState.getPhase() == Phase.RETURN_CARD) {
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										think();
										ai.returnCard(new GameStateContainer(gameState, side));
										if (gameState.getTurn() == side) {
											throw new IllegalStateException();
										}
									}
								});
							}
						}
					}
				}
			}
		});
	}

	private static void vsComputer(Class<? extends ArtificialIntelligence> aiClass) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Side me = Side.MACEDONIA;
		final Side opponent = Side.PERSIA;
		final GameState gs = new GameState();
		final GameView gv = new GameView();
		final GameStateInput gsi = new GameStateInput(gs, opponent);
		gsi.setOnUpdateListener(new OnUpdateListener() {
			@Override
			public void onUpdate() {
				gv.update(new GameStateContainer(gs, me));
			}
		});
		Constructor<? extends ArtificialIntelligence> constructor = aiClass.getConstructor(GameStateInput.class);
		final ArtificialIntelligence ai = constructor.newInstance(gsi);
		gv.updateSouthHand(gs.getHand(me));
		gv.updateNorthHand(gs.getHand(opponent));
		gv.updateColumns(gs.getColumns(), opponent, me);
		gv.updateTroopDeck(gs.getTroopDeckSize());
		gv.updateTacticsDeck(gs.getTacticsDeckSize());
		gv.updateDiscarded(CardContainer.getCardContainers(gs.getDiscarded(), false));
		gv.setOnDrawTroopListener(new EventListener() {
			@Override
			public void onEvent() {
				if (gs.getTurn() != me) return;
				boolean success = gs.drawTroop();
				if (success) {
					gv.updateTroopDeck(gs.getTroopDeckSize());
					gv.updateSouthHand(gs.getHand(me));
					playAI(ai, gs, opponent);
				}
			}
		});
		gv.setOnDrawTacticsListener(new EventListener() {
			@Override
			public void onEvent() {
				if (gs.getTurn() != me) return;
				boolean success = gs.drawTactics();
				if (success) {
					gv.updateTacticsDeck(gs.getTacticsDeckSize());
					gv.updateSouthHand(gs.getHand(me));
					playAI(ai, gs, opponent);
				}
			}
		});
		gv.setOnResolveFlagListener(new OnResolveFlagListener() {
			@Override
			public void onResolveFlag(int columnIndex) {
				if (gs.getTurn() != me) return;
				boolean success = gs.resolveFlag(columnIndex);
				if (success) {
					gv.updateColumn(gs.getColumn(columnIndex), columnIndex, opponent, me);
					if (gs.getPhase() == Phase.GAME_OVER) {
						gv.showMessageDialog("You WIN!");
						ai.lose(new GameStateContainer(gs, opponent));
					}
				}
			}
		});
		gv.setOnPutCardListener(new OnPutCardListener() {
			@Override
			public void onPutCard(int handIndex, int columnIndex) {
				if (gs.getTurn() != me) return;
				boolean success = gs.putCard(handIndex, columnIndex);
				if (success) {
					gv.updateColumn(gs.getColumn(columnIndex), columnIndex, opponent, me);
					gv.updateSouthHand(gs.getHand(me));
				}
			}
		});
		gv.setOnPlayCardListener(new OnPlayCardListener() {
			@Override
			public void onPlayCard(int handIndex, int srcColumnIndex, int srcCardIndex, int dstColumnIndex) {
				if (gs.getTurn() != me) return;
				boolean success = gs.playCard(handIndex, srcColumnIndex, srcCardIndex, dstColumnIndex);
				if (success) {
					gv.updateColumn(gs.getColumn(srcColumnIndex), srcColumnIndex, opponent, me);
					if ((dstColumnIndex >= 0) && (dstColumnIndex < GameState.MAX_COLUMN)) gv.updateColumn(gs.getColumn(dstColumnIndex), dstColumnIndex, opponent, me);
					gv.updateSouthHand(gs.getHand(me));
					gv.updateDiscarded(CardContainer.getCardContainers(gs.getDiscarded(), false));
					gv.updateUsedTactics(CardContainer.getCardContainers(gs.getUsedGuileTactics(), false));
				}
			}
		});
		gv.setOnScoutDeckListener(new OnScoutDeckListener() {
			@Override
			public void onScoutDeck(int handIndex, int troopLength) {
				if (gs.getTurn() != me) return;
				boolean success = gs.scoutDeck(handIndex, troopLength);
				if (success) {
					gv.updateSouthHand(gs.getHand(me));
					gv.updateTroopDeck(gs.getTroopDeckSize());
					gv.updateTacticsDeck(gs.getTacticsDeckSize());
					gv.updateUsedTactics(CardContainer.getCardContainers(gs.getUsedGuileTactics(), false));
				}
			}
		});
		gv.setOnReturnCardsListener(new OnReturnCardsListener() {
			@Override
			public void onReturnCards(int[] handIndices) {
				if (gs.getTurn() != me) return;
				boolean success = gs.returnCards(handIndices);
				if (success) {
					gv.updateSouthHand(gs.getHand(me));
					gv.updateTroopDeck(gs.getTroopDeckSize());
					gv.updateTacticsDeck(gs.getTacticsDeckSize());
					playAI(ai, gs, opponent);
				}
			}
		});
		gv.setOnEndTurnListener(new OnEndTurnListener() {
			@Override
			public void onEndTurn() {
				if (gs.getTurn() != me) return;
				boolean success = (gs.getPhase() == Phase.ACTION) ? gs.passTurn() : (gs.getPhase() == Phase.DRAW) ? gs.endTurnWithoutDraw() : false;
				if (success) {
					playAI(ai, gs, opponent);
				}
			}
		});
	}

	private static boolean playAIWithoutSleep(ArtificialIntelligence ai, GameState gameState, Side side) {
		ai.resolveFlags(new GameStateContainer(gameState, side));
		if (gameState.getPhase() == Phase.GAME_OVER) {
			ai.win(new GameStateContainer(gameState, side));
			return true;
		}
		ai.action(new GameStateContainer(gameState, side));
		if (gameState.getTurn() == side) {
			if (gameState.getPhase() == Phase.ACTION) {
				System.out.println("Action Error");
				return false;
			} else if (gameState.getPhase() == Phase.DRAW) {
				ai.resolveFlags(new GameStateContainer(gameState, side));
				ai.draw(new GameStateContainer(gameState, side));
				if (gameState.getTurn() == side) {
					if (gameState.getPhase() == Phase.GAME_OVER) {
						ai.win(new GameStateContainer(gameState, side));
						return true;
					} else {
						System.out.println("Draw Error");
						return false;
					}
				} else {
					return true;
				}
			} else if (gameState.getPhase() == Phase.RETURN_CARD) {
				ai.returnCard(new GameStateContainer(gameState, side));
				if (gameState.getTurn() == side) {
					System.out.println("Return Card Error");
					return false;
				} else {
					return true;
				}
			} else {
				System.out.println("Unknown Error");
				return false;
			}
		} else {
			return true;
		}
	}

	private static final int GAME_LENGTH = 100;

	public static void watchAI(Class<? extends ArtificialIntelligence> aiClass0, Class<? extends ArtificialIntelligence> aiClass1, boolean showDebug) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Side[] side = new Side[2];
		ArtificialIntelligence[] ai = new ArtificialIntelligence[2];
		int[] win = new int[2];
		win[0] = 0;
		win[1] = 0;
		int[] disqualified = new int[2];
		disqualified[0] = 0;
		disqualified[1] = 0;
		Constructor<? extends ArtificialIntelligence> constructor0 = aiClass0.getConstructor(GameStateInput.class);
		Constructor<? extends ArtificialIntelligence> constructor1 = aiClass1.getConstructor(GameStateInput.class);
		SpectatorView spectatorView = new SpectatorView(aiClass0.getSimpleName(), aiClass1.getSimpleName());
		for (int i = 0; i < GAME_LENGTH; i++) {
			final GameState gs = new GameState();
			side[0] = i < GAME_LENGTH / 2 ? Side.MACEDONIA : Side.PERSIA;
			side[1] = i < GAME_LENGTH / 2 ? Side.PERSIA : Side.MACEDONIA;
			ai[0] = constructor0.newInstance(new GameStateInput(gs, side[0]));
			ai[1] = constructor1.newInstance(new GameStateInput(gs, side[1]));
			int p = i < GAME_LENGTH / 2 ? 0 : 1;
			while (true) {
				boolean success = playAIWithoutSleep(ai[p], gs, side[p]);
				if (success) {
					if (gs.getPhase() == Phase.GAME_OVER) {
						win[p]++;
						spectatorView.update(p, win[p], disqualified[p]);
						break;
					}
				} else {
					disqualified[p]++;
					win[(p + 1) % 2]++;
					spectatorView.update(p, win[p], disqualified[p]);
					if (showDebug) {
						GameView gameView = new GameView();
						gameView.update(new GameStateContainer(gs, side[p]));
					}
					break;
				}
				p = (p + 1) % 2;
			}
		}
		if (win[0] != win[1]) {
			spectatorView.updateWinner(win[0] > win[1] ? 0 : 1);
		}
	}
}
