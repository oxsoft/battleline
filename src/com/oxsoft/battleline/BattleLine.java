package com.oxsoft.battleline;

import java.io.IOException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.oxsoft.battleline.ai.AIManager;
import com.oxsoft.battleline.network.Client;
import com.oxsoft.battleline.network.Server;

public class BattleLine {
	private static final int DEFAULT_PORT = 50009;

	public static void main(String[] args) {
		showLaunchMenu();
		// testNetwork();
	}

	private static void showLaunchMenu() {
		JRadioButton[] radio = new JRadioButton[4];
		ButtonGroup group = new ButtonGroup();
		Box verticalBox = Box.createVerticalBox();
		radio[0] = new JRadioButton("VS computer");
		radio[1] = new JRadioButton("Launch server");
		radio[2] = new JRadioButton("Launch client");
		radio[3] = new JRadioButton("AI vs AI");
		for (int i = 0; i < radio.length; i++) {
			group.add(radio[i]);
			verticalBox.add(radio[i]);
		}
		radio[0].setSelected(true);
		int status = JOptionPane.showOptionDialog(null, verticalBox, "Launch Menu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			for (int i = 0; i < radio.length; i++) {
				if (radio[i].isSelected()) {
					switch (i) {
					case 0:
						AIManager.showVsComputerMenu();
						return;
					case 1:
						launchServer();
						return;
					case 2:
						launchClient();
						return;
					case 3:
						AIManager.showAIvsAIMenu();
						return;
					}
				}
			}
		}
	}

	private static void launchServer() {
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Port"));
		final JTextField port = new JTextField(String.valueOf(DEFAULT_PORT));
		verticalBox.add(port);
		int status = JOptionPane.showOptionDialog(null, verticalBox, "Launch Menu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			try {
				new Server(Integer.parseInt(port.getText()));
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port number");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to launch server");
			}
		}
	}

	private static void launchClient() {
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Host"));
		final JTextField host = new JTextField("localhost");
		verticalBox.add(host);
		verticalBox.add(new JLabel("Port"));
		final JTextField port = new JTextField(String.valueOf(DEFAULT_PORT));
		verticalBox.add(port);
		int status = JOptionPane.showOptionDialog(null, verticalBox, "Launch Menu", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (status == JOptionPane.OK_OPTION) {
			try {
				new Client(host.getText(), Integer.parseInt(port.getText()));
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "Invalid port number");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to connect to server");
			}
		}
	}

	@SuppressWarnings("unused")
	private static void testNetwork() {
		new Thread() {
			@Override
			public void run() {
				try {
					new Server(DEFAULT_PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					new Client("localhost", DEFAULT_PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					new Client("localhost", DEFAULT_PORT);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
