package com.oxsoft.battleline.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oxsoft.battleline.container.CardContainer;

@SuppressWarnings("serial")
public class DeckView extends JPanel {
	private final JLabel cardLength;
	private final CardBackView deck;
	private final JPanel discarded;

	public DeckView(CardContainer c, MouseListener l) {
		setPreferredSize(new Dimension(150, 600));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		cardLength = new JLabel();
		cardLength.setFont(new Font("", Font.PLAIN, 50));
		deck = new CardBackView(c);
		deck.addMouseListener(l);
		discarded = new JPanel();
		add(new JPanel()); // Dummy panel
		add(cardLength);
		add(deck);
		add(discarded);
		add(new JPanel()); // Dummy panel
	}

	public void update(int length) {
		cardLength.setText(String.valueOf(length));
		deck.setVisible(length > 0);
		revalidate();
		repaint();
	}

	public void updateDiscarded(CardContainer[] c) {
		discarded.removeAll();
		for (CardContainer cardContainer : c) {
			discarded.add(new CardView(cardContainer));
		}
		discarded.revalidate();
		discarded.repaint();
	}
}
