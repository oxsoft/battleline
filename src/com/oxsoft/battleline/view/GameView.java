package com.oxsoft.battleline.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import com.oxsoft.battleline.container.CardContainer;
import com.oxsoft.battleline.container.GameStateContainer;
import com.oxsoft.battleline.model.Column;
import com.oxsoft.battleline.model.GameState;
import com.oxsoft.battleline.model.Hand;
import com.oxsoft.battleline.model.Phase;
import com.oxsoft.battleline.model.Side;
import com.oxsoft.battleline.model.card.Deserter;
import com.oxsoft.battleline.model.card.Redeploy;
import com.oxsoft.battleline.model.card.Scout;
import com.oxsoft.battleline.model.card.Tactics;
import com.oxsoft.battleline.model.card.Traitor;
import com.oxsoft.battleline.model.card.Troop;
import com.oxsoft.battleline.view.ColumnView.NorthOrSouth;
import com.oxsoft.battleline.view.ColumnView.OnClickListener;
import com.oxsoft.battleline.view.ColumnView.OnSelectSlotListener;

public class GameView {
	private final JFrame frame;
	private final JPanel northHand;
	private final JPanel southHand;
	private final DeckView troopDeck;
	private final DeckView tacticsDeck;
	private final ColumnView[] columnViews;
	private final JButton endTurnButton;
	private final JLabel status;

	private EventListener onDrawTroop = null;
	private EventListener onDrawTactics = null;
	private OnResolveFlagListener onResolveFlag = null;
	private OnPutCardListener onPutCard = null;
	private OnPlayCardListener onPlayCard = null;
	private OnScoutDeckListener onScoutDeck = null;
	private OnReturnCardsListener onReturnCards = null;
	private OnEndTurnListener onEndTurn = null;

	private int selectedCard = -1;
	private final int[] returnCards = new int[Scout.LENGTH - 1];

	private enum GuileMode {
		NO_GUILE, SCOUT, REDEPLOY, DESERTER, TRAITOR
	}

	private GuileMode guileMode = GuileMode.NO_GUILE;
	private int srcColumnIndex = -1;
	private int srcCardIndex = -1;

	public GameView() {
		this(null);
	}

	public GameView(WindowListener l) {
		for (int i = 0; i < returnCards.length; i++) {
			returnCards[i] = -1;
		}
		frame = new JFrame("Battle Line");
		frame.setBounds(160, 90, 1600, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		northHand = getNorthHand();
		frame.add(northHand, BorderLayout.NORTH);
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.PAGE_AXIS));
		southHand = getSouthHand();
		status = new JLabel(" ");
		south.add(southHand);
		south.add(status);
		frame.add(south, BorderLayout.SOUTH);
		troopDeck = getTroopDeck();
		frame.add(troopDeck, BorderLayout.WEST);
		tacticsDeck = getTacticsDeck();
		frame.add(tacticsDeck, BorderLayout.EAST);
		columnViews = new ColumnView[GameState.MAX_COLUMN];
		frame.add(getColumns(), BorderLayout.CENTER);
		endTurnButton = getEndTurnButton();
		frame.addWindowListener(l);
		frame.setVisible(true);
	}

	public static interface EventListener {
		public void onEvent();
	}

	public static interface OnPutCardListener {
		public void onPutCard(int handIndex, int columnIndex);
	}

	public static interface OnPlayCardListener {
		public void onPlayCard(int handIndex, int srcColumnIndex, int srcCardIndex, int dstColumnIndex);
	}

	public static interface OnResolveFlagListener {
		public void onResolveFlag(int columnIndex);
	}

	public static interface OnScoutDeckListener {
		public void onScoutDeck(int handIndex, int troopLength);
	}

	public static interface OnReturnCardsListener {
		public void onReturnCards(int[] handIndices);
	}

	public static interface OnEndTurnListener {
		public void onEndTurn();
	}

	public void setOnDrawTroopListener(EventListener eventListener) {
		onDrawTroop = eventListener;
	}

	public void setOnDrawTacticsListener(EventListener eventListener) {
		onDrawTactics = eventListener;
	}

	public void setOnResolveFlagListener(OnResolveFlagListener resolveFlagListener) {
		onResolveFlag = resolveFlagListener;
	}

	public void setOnPutCardListener(OnPutCardListener onPutCardListener) {
		onPutCard = onPutCardListener;
	}

	public void setOnPlayCardListener(OnPlayCardListener onPlayCardListener) {
		onPlayCard = onPlayCardListener;
	}

	public void setOnScoutDeckListener(OnScoutDeckListener onScoutDeckListener) {
		onScoutDeck = onScoutDeckListener;
	}

	public void setOnReturnCardsListener(OnReturnCardsListener onReturnCardsListener) {
		onReturnCards = onReturnCardsListener;
	}

	public void setOnEndTurnListener(OnEndTurnListener onEndTurnListener) {
		onEndTurn = onEndTurnListener;
	}

	private JPanel getNorthHand() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1350, 80));
		panel.setBackground(new Color(255, 127, 127));
		return panel;
	}

	private JPanel getSouthHand() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(1350, 80));
		panel.setBackground(new Color(127, 127, 255));
		return panel;
	}

	private DeckView getTroopDeck() {
		return new DeckView(new CardContainer(Troop.class.getSimpleName(), null, null), new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (onDrawTroop != null) onDrawTroop.onEvent();
			}
		});
	}

	private DeckView getTacticsDeck() {
		return new DeckView(new CardContainer(Tactics.class.getSimpleName(), null, null), new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (onDrawTactics != null) onDrawTactics.onEvent();
			}
		});
	}

	private JPanel getColumns() {
		JPanel panel = new JPanel();
		JPanel content = new JPanel();
		content.setPreferredSize(new Dimension(1350, 660));
		content.setLayout(new BoxLayout(content, BoxLayout.LINE_AXIS));
		for (int i = 0; i < GameState.MAX_COLUMN; i++) {
			final int index = i;
			columnViews[i] = new ColumnView(new OnClickListener() {
				@Override
				public void onClick() {
					if (guileMode == GuileMode.NO_GUILE) {
						if (selectedCard != -1) if (onPutCard != null) onPutCard.onPutCard(selectedCard, index);
						selectedCard = -1;
					}
				}
			}, new OnClickListener() {
				@Override
				public void onClick() {
					if (onResolveFlag != null) onResolveFlag.onResolveFlag(index);
				}
			}, new OnSelectSlotListener() {
				@Override
				public void onSelectSlot(NorthOrSouth northOrSouth, int cardIndex) {
					if (selectedCard != -1) {
						switch (guileMode) {
						case REDEPLOY:
							if (northOrSouth == NorthOrSouth.SOUTH) {
								if ((srcColumnIndex == -1) && (srcCardIndex == -1)) {
									srcColumnIndex = index;
									srcCardIndex = cardIndex;
								} else {
									if (onPlayCard != null) onPlayCard.onPlayCard(selectedCard, srcColumnIndex, srcCardIndex, index);
									selectedCard = -1;
									guileMode = GuileMode.NO_GUILE;
									srcColumnIndex = -1;
									srcCardIndex = -1;
								}
							}
							break;
						case DESERTER:
							if (northOrSouth == NorthOrSouth.NORTH) {
								if (onPlayCard != null) onPlayCard.onPlayCard(selectedCard, index, cardIndex, -1);
								selectedCard = -1;
								guileMode = GuileMode.NO_GUILE;
								srcColumnIndex = -1;
								srcCardIndex = -1;
							}
							break;
						case TRAITOR:
							switch (northOrSouth) {
							case NORTH:
								srcColumnIndex = index;
								srcCardIndex = cardIndex;
								break;
							case SOUTH:
								if ((srcColumnIndex != -1) && (srcCardIndex != -1)) {
									if (onPlayCard != null) onPlayCard.onPlayCard(selectedCard, srcColumnIndex, srcCardIndex, index);
									selectedCard = -1;
									guileMode = GuileMode.NO_GUILE;
									srcColumnIndex = -1;
									srcCardIndex = -1;
								}
								break;
							}
							break;
						default:
							break;
						}
					}
				}
			});
			content.add(columnViews[i]);
		}
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.add(new JPanel()); // Dummy panel
		panel.add(content);
		panel.add(new JPanel()); // Dummy panel
		return panel;
	}

	private JButton getEndTurnButton() {
		JButton endTurn = new JButton("End turn");
		endTurn.setPreferredSize(new Dimension(150, 50));
		endTurn.setFont(new Font("", Font.PLAIN, 20));
		endTurn.setFocusPainted(false);
		endTurn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (onEndTurn != null) onEndTurn.onEndTurn();
			}
		});
		return endTurn;
	}

	public void updateColumns(Column[] columns, Side north, Side south) {
		for (int i = 0; i < columns.length; i++) {
			updateColumn(columns[i], i, north, south);
		}
	}

	public void updateColumn(Column column, int index, Side north, Side south) {
		columnViews[index].update(column, north, south);
	}

	public void updateTroopDeck(int length) {
		troopDeck.update(length);
	}

	public void updateTacticsDeck(int length) {
		tacticsDeck.update(length);
	}

	public void updateNorthHand(Hand hand) {
		updateNorthHand(CardContainer.getCardContainers(hand, true));
	}

	public void updateSouthHand(Hand hand) {
		updateSouthHand(CardContainer.getCardContainers(hand, false));
	}

	public void updateNorthHand(CardContainer[] cardContainers) {
		northHand.removeAll();
		for (int i = 0; i < cardContainers.length; i++) {
			JPanel handPanel = new CardBackView(cardContainers[i]);
			northHand.add(handPanel);
		}
		northHand.revalidate();
		northHand.repaint();
	}

	public void updateSouthHand(final CardContainer[] cardContainers) {
		southHand.removeAll();
		final JPanel[] handPanels = new CardView[cardContainers.length];
		for (int i = 0; i < cardContainers.length; i++) {
			handPanels[i] = new CardView(cardContainers[i]);
			southHand.add(handPanels[i]);
		}
		for (int i = 0; i < cardContainers.length; i++) {
			final int index = i;
			handPanels[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (int j = 0; j < handPanels.length; j++) {
						handPanels[j].setBorder(null);
					}
					if (guileMode == GuileMode.SCOUT) {
						if (index == returnCards[0]) {
							returnCards[0] = -1;
						} else if (returnCards[0] >= 0) {
							returnCards[1] = index;
							onReturnCards.onReturnCards(returnCards);
							guileMode = GuileMode.NO_GUILE;
						} else {
							returnCards[0] = index;
							handPanels[index].setBorder(new LineBorder(Color.RED, 5));
						}
					} else {
						if (index == selectedCard) {
							selectedCard = -1;
							guileMode = GuileMode.NO_GUILE;
						} else {
							selectedCard = index;
							handPanels[index].setBorder(new LineBorder(Color.DARK_GRAY, 5));
							if (cardContainers[index].type.equals(Scout.class.getSimpleName())) {
								guileMode = GuileMode.SCOUT;
								int troop = showScoutDialog();
								if (troop >= 0) {
									for (int j = 0; j < returnCards.length; j++) {
										returnCards[j] = -1;
									}
									if (onScoutDeck != null) onScoutDeck.onScoutDeck(index, troop);
								} else {
									guileMode = GuileMode.NO_GUILE;
								}
							} else if (cardContainers[index].type.equals(Redeploy.class.getSimpleName())) {
								guileMode = GuileMode.REDEPLOY;
							} else if (cardContainers[index].type.equals(Deserter.class.getSimpleName())) {
								guileMode = GuileMode.DESERTER;
							} else if (cardContainers[index].type.equals(Traitor.class.getSimpleName())) {
								guileMode = GuileMode.TRAITOR;
							} else {
								guileMode = GuileMode.NO_GUILE;
							}
						}
					}
				}
			});
		}
		southHand.add(endTurnButton);
		southHand.revalidate();
		southHand.repaint();
	}

	public void updateDiscarded(final CardContainer[] cardContainers) {
		troopDeck.updateDiscarded(cardContainers);
	}

	public void updateUsedTactics(final CardContainer[] cardContainers) {
		tacticsDeck.updateDiscarded(cardContainers);
	}

	public void updateStatus(String text) {
		status.setText(text);
	}

	public void showMessageDialog(String text) {
		JLabel label = new JLabel(text);
		JOptionPane.showMessageDialog(null, label);
	}

	public void update(GameStateContainer gameStateContainer) {
		updateNorthHand(gameStateContainer.opponentHand);
		updateSouthHand(gameStateContainer.myHand);
		updateTroopDeck(gameStateContainer.troopDeck);
		updateTacticsDeck(gameStateContainer.tacticsDeck);
		for (int i = 0; i < columnViews.length; i++) {
			columnViews[i].update(gameStateContainer.columns[i]);
		}
		updateDiscarded(gameStateContainer.discarded);
		updateUsedTactics(gameStateContainer.usedGuileTactics);
		updateStatus(gameStateContainer.myTurn ? "Your turn" : "Opponent's turn");
		if (gameStateContainer.phase == Phase.GAME_OVER) {
			showMessageDialog(gameStateContainer.myTurn ? "You WIN!" : "You Lose...");
		}
	}

	private int showScoutDialog() {
		JRadioButton[] radio = new JRadioButton[4];
		ButtonGroup group = new ButtonGroup();
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("How many cards do you draw?"));
		for (int i = 0; i < radio.length; i++) {
			radio[i] = new JRadioButton(i + " Troop and " + (Scout.LENGTH - i) + " Tactics");
			group.add(radio[i]);
			verticalBox.add(radio[i]);
		}
		radio[0].setSelected(true);
		int status = JOptionPane.showOptionDialog(null, verticalBox, "Scout", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			for (int i = 0; i < radio.length; i++) {
				if (radio[i].isSelected()) {
					return i;
				}
			}
		}
		return -1;
	}
}
