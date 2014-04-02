package sysml4rtm.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import sysml4rtm.Messages;
import sysml4rtm.utils.ConfigUtil;

public class OutputDirectoryPanel extends JPanel {
	private static final long serialVersionUID = 6227307820865854523L;
	static final String NAME = "output_directory_panel";
	private OutputDirectoryText outputDirectoryText;
	private AutoOpenBtn autoOpenBtn;

	public OutputDirectoryPanel() {
		setName(NAME);
		setLayout(new GridBagLayout());
		setAlignmentX(Component.LEFT_ALIGNMENT);
		createContents();
		setVisible(true);
	}

	private void createContents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(2, 10, 0, 10);
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(new ExtractLabel(), gbc);
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;

		gbc.gridy = 2;
		gbc.gridx = 0;
		add(new Empty(), gbc);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 1;
		add(new OutputLabel(), gbc);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		add(outputDirectoryText = new OutputDirectoryText(), gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridx = 3;
		add(new ReferenceButton(), gbc);

		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(autoOpenBtn = new AutoOpenBtn(), gbc);

	}

	class ExtractLabel extends JLabel {
		static final String NAME = "explain_output_directory";
		private static final long serialVersionUID = -6217535974483549058L;

		public ExtractLabel() {
			setText(Messages.getMessage(NAME));
			setName(NAME);
		}
	}

	class OutputLabel extends JLabel {
		private static final long serialVersionUID = -8910390913301028735L;
		static final String NAME = "output_directory";

		public OutputLabel() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
		}
	}

	class OutputDirectoryText extends JTextField {

		private static final int WIDTH = 300;
		private static final int HEIGHT = 14;
		private static final long serialVersionUID = -5561324124348079144L;
		static final String NAME = "output_directory";

		public OutputDirectoryText() {
			setName(NAME);
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setMinimumSize(new Dimension(WIDTH, HEIGHT));
			setText(ConfigUtil.getDefaultCodeOutputDirectoryPath());
		}
	}

	class ReferenceButton extends JButton {

		private static final long serialVersionUID = 4246421697607707364L;
		static final String NAME = "reference_button";

		public ReferenceButton() {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle(Messages.getMessage("explain_output_directory"));
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setCurrentDirectory(new File(ConfigUtil
							.getDefaultCodeOutputDirectoryPath()));
					int option = chooser.showOpenDialog(OutputDirectoryPanel.this);
					if (JFileChooser.APPROVE_OPTION == option) {
						File file = chooser.getSelectedFile();
						outputDirectoryText.setText(file.getAbsolutePath());
					}
				}
			});
		}
	}

	class AutoOpenBtn extends JCheckBox {

		private static final long serialVersionUID = 2343757561722416100L;
		static final String NAME = "auto_open";

		public AutoOpenBtn() {
			setText(Messages.getMessage(NAME));
			setName(NAME);
			setSelected(ConfigUtil.isOpenGeneratedFolderAutomatically());
			addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ConfigUtil.saveOpenGeneratedFolderAutomaticcaly(AutoOpenBtn.this.isSelected());
				}
			});
		}
		
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("OutputDirectoryPanelTest");
		JDialog dialog = new JDialog(frame);
		OutputDirectoryPanel p = new OutputDirectoryPanel();
		dialog.add(p);
		frame.setSize(400, 300);
		dialog.pack();
		dialog.setVisible(true);
		
		String errorDetailMessage = "test\ntest2".replaceAll("\n", "<br />");
		ErrorInfo errInfo = new ErrorInfo(
		        "Warning",
		        Messages.getMessage("validationerror.occur"),
		        errorDetailMessage,
		        null,
		        null,
		        org.jdesktop.swingx.error.ErrorLevel.WARNING,
		        null);
		JXErrorPane.showDialog(frame, errInfo);
	}

	public String getOutputPath() {
		return outputDirectoryText.getText();
	}

	OutputDirectoryText getOutputDirectoryText() {
		return outputDirectoryText;
	}

	public boolean getAutoOpenBtn() {
		return autoOpenBtn.isSelected();
	}

}
