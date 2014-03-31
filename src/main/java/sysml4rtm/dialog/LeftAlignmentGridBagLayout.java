package sysml4rtm.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;

final class LeftAlignmentGridBagLayout extends GridBagLayout {
	private static final long serialVersionUID = 5568618157020959813L;

	@Override
	public float getLayoutAlignmentX(Container parent) {
		return Component.LEFT_ALIGNMENT;
	}

	@Override
	public float getLayoutAlignmentY(Container parent) {
		return Component.TOP_ALIGNMENT;
	}
}