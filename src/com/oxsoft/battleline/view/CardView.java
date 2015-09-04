package com.oxsoft.battleline.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.oxsoft.battleline.container.CardContainer;
import com.oxsoft.battleline.model.card.Card;
import com.oxsoft.battleline.model.card.CompanionCavalry;
import com.oxsoft.battleline.model.card.ShieldBearers;
import com.oxsoft.battleline.model.card.Troop;

@SuppressWarnings("serial")
public class CardView extends JPanel {

	public CardView(Card card) {
		this(new CardContainer(card, false));
	}

	public CardView(CardContainer cardContainer) {
		if ((cardContainer == null) || (cardContainer.type == null)) {
			setPreferredSize(new Dimension(100, 70));
			setBackground(new Color(0, 0, 0, 0));
			return;
		}
		setPreferredSize(new Dimension(100, 70));
		JLabel label = new JLabel();
		if (cardContainer.type.equals(Troop.class.getSimpleName())) {
			label.setFont(new Font("", Font.PLAIN, 40));
			label.setForeground(getFgColor(cardContainer.color));
			setBackground(getBgColor(cardContainer.color));
			label.setText(String.valueOf(cardContainer.value + 1));
		} else {
			if (cardContainer.type.equals(CompanionCavalry.class.getSimpleName())) {
				label.setFont(new Font("", Font.PLAIN, 40));
				label.setText("8");
			} else if (cardContainer.type.equals(ShieldBearers.class.getSimpleName())) {
				label.setFont(new Font("", Font.PLAIN, 40));
				label.setText("123");
			} else {
				label.setFont(new Font("", Font.PLAIN, 20));
				label.setText(cardContainer.type);
			}
			setBackground(Color.BLACK);
			label.setForeground(Color.WHITE);
		}
		add(label, BorderLayout.CENTER);
	}

	private Color getFgColor(com.oxsoft.battleline.model.card.Color color) {
		switch (color) {
		case BLUE:
		case VIOLET:
			return Color.WHITE;
		case GREEN:
		case ORANGE:
		case RED:
		case YELLOW:
			return Color.BLACK;
		default:
			return null;
		}
	}

	private Color getBgColor(com.oxsoft.battleline.model.card.Color color) {
		switch (color) {
		case BLUE:
			return new Color(6, 132, 216);
		case GREEN:
			return new Color(105, 195, 54);
		case ORANGE:
			return new Color(244, 175, 0);
		case RED:
			return new Color(255, 45, 28);
		case VIOLET:
			return new Color(148, 33, 132);
		case YELLOW:
			return new Color(255, 255, 0);
		default:
			return null;
		}
	}
}
