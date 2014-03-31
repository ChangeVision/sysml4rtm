package sysml4rtm.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.UIManager;

public class ArrowButton extends JButton {
	private static final long serialVersionUID = 8208928429976409894L;
	public static final int LEFT = 0;
	public static final int STRONG_LEFT = 1;
	public static final int RIGHT = 2;
	public static final int STRONG_RIGHT = 3;
	public static final int DOWN = 4;
	public static final int UP = 5;

	public static final String NAME_LEFT = "left";
	public static final String NAME_STRONG_LEFT = "strong_left";
	public static final String NAME_RIGHT = "right";
	public static final String NAME_STRONG_RIGHT = "strong_right";
	public static final String NAME_DOWN = "down";
	public static final String NAME_UP = "up";

	protected int type;

	public ArrowButton(int type) {
		setRequestFocusEnabled(false);
		this.type = type;
		switch (type) {
		case LEFT:
			setName(NAME_LEFT);
			break;
		case STRONG_LEFT:
			setName(NAME_STRONG_LEFT);
			break;
		case RIGHT:
			setName(NAME_RIGHT);
			break;
		case STRONG_RIGHT:
			setName(NAME_STRONG_RIGHT);
			break;
		case DOWN:
			setName(NAME_DOWN);
			break;
		case UP:
			setName(NAME_UP);
			break;
		}
		setBackground(UIManager.getColor("control"));
	}

	public int getType() {
		return type;
	}

	@Override
	public void paint(Graphics g) {
		Color origColor;
		int w;
		int h;
		int size;

		w = getSize().width;
		h = getSize().height;
		origColor = g.getColor();
		boolean isPressed = getModel().isPressed();
		boolean isEnabled = isEnabled();

		g.setColor(getBackground());
		g.fillRect(1, 1, w - 2, h - 2);

		// / Draw the proper Border
		if (isPressed) {
			g.setColor(UIManager.getColor("controlShadow"));
			g.drawRect(0, 0, w - 1, h - 1);
		} else {
			g.drawLine(0, 0, 0, h - 1);
			g.drawLine(1, 0, w - 2, 0);
			g.setColor(UIManager.getColor("controlLtHighlight"));
			g.drawLine(1, 1, 1, h - 3);
			g.drawLine(2, 1, w - 3, 1);
			g.setColor(UIManager.getColor("controlShadow"));
			g.drawLine(1, h - 2, w - 2, h - 2);
			g.drawLine(w - 2, 1, w - 2, h - 3);
			g.setColor(UIManager.getColor("controlDkShadow"));
			g.drawLine(0, h - 1, w - 1, h - 1);
			g.drawLine(w - 1, h - 1, w - 1, 0);
		}
		if (h < 5 || w < 5) {
			g.setColor(origColor);
			return;
		}
		if (isPressed)
			g.translate(1, 1);
		size = Math.min((h - 4) / 3, (w - 4) / 3);
		size = Math.max(size, 2);
		paintTriangle(g, (w - size) / 2, (h - size) / 2, size, type, isEnabled);
		if (isPressed) {
			g.translate(-1, -1);
		}
		g.setColor(origColor);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(16, 16);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(16, 16);
	}

	// deprecated
	// public boolean isFocusTraversable() {
	// return true;
	// }

	/**
	 * Returns whether this Component can be focused.
	 * 
	 * @return <code>true</code> if this Component is focusable;
	 *         <code>false</code> otherwise.
	 * @see #setFocusable
	 * @since 1.4
	 */
	@Override
	public boolean isFocusable() {
		return true;
	}

	public void paintTriangle(Graphics g, int x, int y, int size, int type, boolean isEnabled) {
		Color oldColor = g.getColor();
		size = Math.max(size, 2);
		int mid = size / 2;
		g.translate(x, y);

		if (isEnabled) {
			// g.setColor(UIManager.getColor("controlDkShadow"));
			g.setColor(Color.black);
		} else {
			g.setColor(UIManager.getColor("controlShadow"));
		}
		if (type == UP) {
			for (int i = 0; i < size; i++) {
				g.drawLine(mid - i, i, mid + i, i);
			}
			if (!isEnabled) {
				g.setColor(UIManager.getColor("controlLtHighlight"));
				g.drawLine(mid - size + 2, size, mid + size, size); // org
																	// size->i
			}
		} else if (type == DOWN) {
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(UIManager.getColor("controlLtHighlight"));
				int j = 0;
				for (int i = size - 1; i >= 0; i--) {
					g.drawLine(mid - i, j, mid + i, j);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(UIManager.getColor("controlShadow"));
			}
			int j = 0;
			for (int i = size - 1; i >= 0; i--) {
				g.drawLine(mid - i, j, mid + i, j);
				j++;
			}
		} else if (type == LEFT) {
			for (int i = 0; i < size; i++) {
				g.drawLine(i, mid - i, i, mid + i);
			}
			if (!isEnabled) {
				g.setColor(UIManager.getColor("controlLtHighlight"));
				g.drawLine(size, mid - size + 2, size, mid + size); // org size
																	// -> i
			}
		} else if (type == STRONG_LEFT) {
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(UIManager.getColor("controlLtHighlight"));
				g.drawLine(size, 0, 3, mid);
				g.drawLine(size, size, 3, mid);
				g.drawLine(size - 3, 0, 0, mid);
				g.drawLine(size - 3, size, 0, mid);
				g.translate(-1, -1);
				g.setColor(UIManager.getColor("controlShadow"));
			}
			g.drawLine(size, 0, 3, mid);
			g.drawLine(size, size, 3, mid);
			g.drawLine(size - 3, 0, 0, mid);
			g.drawLine(size - 3, size, 0, mid);
		} else if (type == RIGHT) {
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(UIManager.getColor("controlLtHighlight"));
				int j = 0;
				for (int i = size - 1; i >= 0; i--) {
					g.drawLine(j, mid - i, j, mid + i);
					j++;
				}
				g.translate(-1, -1);
				g.setColor(UIManager.getColor("controlShadow"));
			}
			int j = 0;
			for (int i = size - 1; i >= 0; i--) {
				g.drawLine(j, mid - i, j, mid + i);
				j++;
			}
		} else if (type == STRONG_RIGHT) {
			if (!isEnabled) {
				g.translate(1, 1);
				g.setColor(UIManager.getColor("controlLtHighlight"));
				g.drawLine(0, 0, size - 3, mid);
				g.drawLine(0, size, size - 3, mid);
				g.drawLine(3, 0, size, mid);
				g.drawLine(3, size, size, mid);
				g.translate(-1, -1);
				g.setColor(UIManager.getColor("controlShadow"));
			}
			g.drawLine(0, 0, size - 3, mid);
			g.drawLine(0, size, size - 3, mid);
			g.drawLine(3, 0, size, mid);
			g.drawLine(3, size, size, mid);
		}
		g.translate(-x, -y);
		g.setColor(oldColor);
	}
}
