package com.oxsoft.battleline.ai;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class SpectatorView {
	private final String[] name;
	private final JPanel[] aiPanel;
	private final JLabel[] winLabel;
	private final JLabel[] disqualifiedLabel;

	public SpectatorView(String ai0, String ai1) {
		name = new String[2];
		name[0] = ai0;
		name[1] = ai1;
		JFrame frame = new JFrame("Battle Line - AI vs AI");
		frame.setBounds(0, 0, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aiPanel = new JPanel[2];
		winLabel = new JLabel[2];
		disqualifiedLabel = new JLabel[2];
		for (int i = 0; i < winLabel.length; i++) {
			winLabel[i] = new JLabel("0");
			winLabel[i].setFont(new Font("", Font.PLAIN, 50));
			winLabel[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			disqualifiedLabel[i] = new JLabel("0");
			disqualifiedLabel[i].setFont(new Font("", Font.PLAIN, 30));
			disqualifiedLabel[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			JLabel nameLabel = new JLabel(name[i]);
			nameLabel.setFont(new Font("", Font.PLAIN, 50));
			nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			JLabel winTitleLabel = new JLabel("Win");
			winTitleLabel.setFont(new Font("", Font.PLAIN, 30));
			winTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			JLabel disqualifiedTitleLabel = new JLabel("Disqualified");
			disqualifiedTitleLabel.setFont(new Font("", Font.PLAIN, 20));
			disqualifiedTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			JPanel padding = new JPanel();
			padding.setBorder(new EmptyBorder(30, 30, 30, 30));
			padding.setLayout(new BoxLayout(padding, BoxLayout.PAGE_AXIS));
			padding.add(nameLabel);
			padding.add(winTitleLabel);
			padding.add(winLabel[i]);
			padding.add(disqualifiedTitleLabel);
			padding.add(disqualifiedLabel[i]);
			aiPanel[i] = new JPanel();
			aiPanel[i].setBorder(new LineBorder(Color.GRAY, 3));
			aiPanel[i].setLayout(new BoxLayout(aiPanel[i], BoxLayout.PAGE_AXIS));
			aiPanel[i].add(padding);
		}
		JLabel vsLabel = new JLabel("VS");
		vsLabel.setFont(new Font("", Font.PLAIN, 30));
		vsLabel.setBorder(new EmptyBorder(30, 30, 30, 30));
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
		container.add(new JPanel());
		container.add(aiPanel[0]);
		container.add(vsLabel);
		container.add(aiPanel[1]);
		container.add(new JPanel());
		frame.add(container);
		frame.setVisible(true);
	}

	public void update(int ai, int win, int disqualified) {
		winLabel[ai].setText(String.valueOf(win));
		disqualifiedLabel[ai].setText(String.valueOf(disqualified));
		aiPanel[ai].revalidate();
		aiPanel[ai].repaint();
	}

	public void updateWinner(int winner) {
		aiPanel[winner].setBorder(new LineBorder(Color.RED, 3));
		aiPanel[winner].revalidate();
		aiPanel[winner].repaint();
	}
}
