package sysml4rtm.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import sysml4rtm.Messages;

@SuppressWarnings("serial")
public class GenerateResultDialog extends JDialog {

	final class OKButton extends JButton {
		static final String NAME = "OK";

		{
			setName(NAME);
			setText(Messages.getMessage("ok"));
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
	}

	private JTextArea contents;
	private JScrollPane scroll;

	public GenerateResultDialog(JDialog owner) {
		super(owner, true);
		setTitle(Messages.getMessage("GenerateResultDialog.title"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createContentPanel() , BorderLayout.CENTER);
        contentPane.add(createButtonPanel() , BorderLayout.SOUTH);
        setPreferredSize(new Dimension(400, 500));
        pack();
        setLocationRelativeTo(owner);
	}

	private Component createButtonPanel() {
        JPanel basePanel = new JPanel(new BorderLayout());
        JButton btn = new OKButton();
        basePanel.add(btn);
		return basePanel;
	}

	private Component createContentPanel() {
        JPanel basePanel = new JPanel(new BorderLayout());
        contents = new JTextArea();
		scroll = new JScrollPane(contents);
		basePanel.add(BorderLayout.CENTER, scroll);
		return basePanel;
	}
	
	public void setContentText(String text) {
		contents.setText(text);
		scrollToTop();
	}

	private void scrollToTop() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				scrollTo(scroll, 0);
			}
		});
	}

	private void scrollTo(JScrollPane scrollPane, int line) {
		JTextArea textArea = (JTextArea)scrollPane.getViewport().getView();
		Document doc = textArea.getDocument();
		Element root = doc.getDefaultRootElement();
		Element elem = root.getElement(line);
		try {
			Rectangle rect = textArea.modelToView(elem.getStartOffset());
			if (rect != null) {
				Rectangle vr = scrollPane.getViewport().getViewRect();
				rect.setSize(10, vr.height);
				textArea.scrollRectToVisible(rect);
			}
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
	}
}
