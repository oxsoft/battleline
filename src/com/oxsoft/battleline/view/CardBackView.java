package com.oxsoft.battleline.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oxsoft.battleline.container.CardContainer;
import com.oxsoft.battleline.model.card.Card;
import com.oxsoft.battleline.model.card.Tactics;
import com.oxsoft.battleline.model.card.Troop;

@SuppressWarnings("serial")
public class CardBackView extends JPanel {

	public CardBackView(Card card) {
		this(new CardContainer(card, true));
	}

	public CardBackView(CardContainer cardContainer) {
		setPreferredSize(new Dimension(100, 70));
		JLabel label = new JLabel();
		label.setFont(new Font("", Font.PLAIN, 20));
		if (cardContainer.type.equals(Troop.class.getSimpleName())) {
			setBackground(Color.WHITE);
			label.setText("Troop");
			label.setForeground(Color.BLACK);
		} else if (cardContainer.type.equals(Tactics.class.getSimpleName())) {
			setBackground(Color.BLACK);
			label.setText("Tactics");
			label.setForeground(Color.WHITE);
		}
		add(label, BorderLayout.CENTER);
	}
}
