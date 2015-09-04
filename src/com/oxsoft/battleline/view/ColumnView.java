package com.oxsoft.battleline.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.oxsoft.battleline.container.CardContainer;
import com.oxsoft.battleline.container.ColumnContainer;
import com.oxsoft.battleline.container.FlagContainer;
import com.oxsoft.battleline.model.Column;
import com.oxsoft.battleline.model.Side;
import com.oxsoft.battleline.model.card.Fog;
import com.oxsoft.battleline.model.card.Mud;

@SuppressWarnings("serial")
public class ColumnView extends JPanel {
	public static enum NorthOrSouth {
		NORTH, SOUTH
	}

	public final JPanel northSlot;
	public final JPanel southSlot;
	public final JPanel flag;

	private final OnClickListener southSlotListener;
	private final OnSelectSlotListener onSelectSlotListener;

	public ColumnView(final OnClickListener southSlotListener, final OnClickListener flagListener, OnSelectSlotListener southSlotCardListener) {
		JPanel content = new JPanel();
		content.setPreferredSize(new Dimension(110, 650));
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		northSlot = new JPanel();
		southSlot = new JPanel();
		flag = new JPanel();
		northSlot.setPreferredSize(new Dimension(110, 305));
		southSlot.setPreferredSize(new Dimension(110, 305));
		flag.setPreferredSize(new Dimension(110, 40));
		northSlot.setBackground(new Color(255, 192, 192));
		southSlot.setBackground(new Color(192, 192, 255));
		flag.setBackground(Color.YELLOW);
		southSlot.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				southSlotListener.onClick();
			}
		});
		flag.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				flagListener.onClick();
			}
		});
		content.add(northSlot);
		content.add(flag);
		content.add(southSlot);
		this.southSlotListener = southSlotListener;
		onSelectSlotListener = southSlotCardListener;
		add(content);
	}

	public static interface OnClickListener {
		public void onClick();
	}

	public static interface OnSelectSlotListener {
		public void onSelectSlot(NorthOrSouth side, int cardIndex);
	}

	public void update(Column column, final Side north, final Side south) {
		update(new ColumnContainer(column, south));
	}

	public void update(ColumnContainer columnContainer) {
		northSlot.removeAll();
		southSlot.removeAll();
		for (int i = 0; i < 4; i++) {
			final int index = i;
			CardView northCardView = new CardView(columnContainer.opponent.length > 3 - index ? columnContainer.opponent[3 - index] : null);
			northCardView.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					onSelectSlotListener.onSelectSlot(NorthOrSouth.NORTH, 3 - index);
				}
			});
			northSlot.add(northCardView);
			CardView southCardView = new CardView(columnContainer.me.length > index ? columnContainer.me[index] : null);
			southCardView.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					southSlotListener.onClick();
					onSelectSlotListener.onSelectSlot(NorthOrSouth.SOUTH, index);
				}
			});
			southSlot.add(southCardView);
		}
		for (CardContainer environment : columnContainer.environments) {
			if (environment.type.equals(Fog.class.getSimpleName())) {
				setBackground(Color.GRAY);
			} else if (environment.type.equals(Mud.class.getSimpleName())) {
				setBorder(new LineBorder(new Color(156, 90, 60), 5));
			}
		}
		if (columnContainer.flag == FlagContainer.UNCLAIMED) {
			this.flag.setBackground(Color.YELLOW);
		} else if (columnContainer.flag == FlagContainer.OPPONENT) {
			this.flag.setBackground(Color.RED);
		} else if (columnContainer.flag == FlagContainer.ME) {
			this.flag.setBackground(Color.BLUE);
		} else {
			this.flag.setBackground(Color.YELLOW);
		}
		northSlot.revalidate();
		southSlot.revalidate();
		this.flag.revalidate();
		northSlot.repaint();
		southSlot.repaint();
		this.flag.repaint();
	}
}
