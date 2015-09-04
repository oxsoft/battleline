package com.oxsoft.battleline.network;

import java.awt.Font;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ServerView {
	private Box status;

	public ServerView(WindowListener l) {
		final JFrame frame = new JFrame("Battle Line Server");
		frame.setBounds(160, 90, 640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(l);
		status = Box.createVerticalBox();
		frame.add(status);
		frame.setVisible(true);
	}

	public void print(String text) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("", Font.PLAIN, 20));
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setVerticalAlignment(JLabel.TOP);
		status.add(label);
		if (status.getComponentCount() > 10) status.remove(0);
		status.revalidate();
		status.repaint();
	}
}
