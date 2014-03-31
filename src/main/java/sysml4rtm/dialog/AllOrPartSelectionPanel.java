package sysml4rtm.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sysml4rtm.Messages;

public class AllOrPartSelectionPanel extends JPanel {

	private static final long serialVersionUID = 2225522938156327518L;
	static final String NAME = "all_or_part_selection";
	public AllGenerateButton allGenerateButton;
	public ComponentGenerateButton componentGenerateButton;
	private Component target;
	private JDialog dialog;

	public AllOrPartSelectionPanel(JDialog dialog) {
		setName(NAME);
		GridBagLayout manager = new GridBagLayout();
		setLayout(manager);
		createContents();
		setVisible(true);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		this.dialog = dialog;
	}

	private void createContents() {
		new SelectionArea();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		add(new Number2Icon(), gbc);
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		add(new ExplainLabel(), gbc);
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(new Empty(), gbc);
		gbc.gridx = 1;
		add(allGenerateButton, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new Empty(), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		add(componentGenerateButton, gbc);
	}

	public boolean isAllSelected() {
		return allGenerateButton.isSelected();
	}

	class Number2Icon extends JLabel {
		private static final long serialVersionUID = 5133031462398236549L;
		static final String NAME = "number_2";

		public Number2Icon() {
			Icon icon = new ImageIcon(ImageLoader.getImage(this.getClass(), "ico2.png"));
			setIcon(icon);
			setName(NAME);
		}
	}

	class ExplainLabel extends JLabel {

		private static final long serialVersionUID = -2043300284211267107L;
		static final String NAME = "all_or_part_explain";

		public ExplainLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}
	}

	class SelectionArea extends ButtonGroup {

		private static final long serialVersionUID = -4630277115815049856L;

		private SelectionArea() {
			add(allGenerateButton = new AllGenerateButton());
			add(componentGenerateButton = new ComponentGenerateButton());
		}
	}

	class ComponentGenerateButton extends JRadioButton {
		private static final long serialVersionUID = 2270864414354703154L;
		static final String NAME = "component_generate";

		private ComponentGenerateButton() {
			super();
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (ComponentGenerateButton.this.isSelected()) {
						if (target != null)
							target.setEnabled(true);
					}
				}
			});
		}
	}

	class AllGenerateButton extends JRadioButton {
		private static final long serialVersionUID = 4037864631476683014L;
		static final String NAME = "all_generate";

		private AllGenerateButton() {
			super();
			setSelected(true);
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (target != null) {
						if (AllGenerateButton.this.isSelected()) {
							target.setVisible(false);
							dialog.pack();
						} else {
							target.setVisible(true);
							dialog.pack();
						}
					}
				}
			});
		}
	}

	public void setTarget(Component target) {
		this.target = target;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(target, gbc);
		if (allGenerateButton.isSelected()) {
			target.setVisible(false);
			dialog.pack();
		} else {
			target.setVisible(true);
			dialog.pack();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(AllOrPartSelectionPanel.class.getName());
		JDialog dialog = new JDialog(frame);
		AllOrPartSelectionPanel p = new AllOrPartSelectionPanel(dialog);
		JLabel testLabel = new JLabel("test");
		testLabel.setName("test");
		p.setTarget(testLabel);
		dialog.add(p);
		frame.setSize(400, 300);
		dialog.pack();
		dialog.setVisible(true);
	}

}
