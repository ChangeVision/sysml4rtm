package sysml4rtm.dialogs;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sysml4rtm.Activator;
import sysml4rtm.Marshaller;
import sysml4rtm.Messages;
import sysml4rtm.exceptions.UnSupportDiagramException;
import sysml4rtm.exceptions.ValidationException;
import sysml4rtm.rtc.export.RtcMarshaller;
import sysml4rtm.rts.export.RtsMarshaller;
import sysml4rtm.utils.ConfigUtil;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;

@SuppressWarnings("serial")
public class GenerateRtmProfileDialog extends JDialog {
	private static final String NAME = "generate_document";
	private static int WIDTH = 510;
	private static int HEIGHT = 140;
	protected static final int GAP = 1;

	private JButton generateButton;
	private IMessageDialogHandler util = Activator.getMessageHandler();

	private OutputDirectoryPanel outputPanel;
	private static final Logger logger = LoggerFactory.getLogger(GenerateRtmProfileDialog.class);
	
	public GenerateRtmProfileDialog(JFrame frame) {
		super(frame, true);
		setName(NAME);
		setTitle(Messages.getMessage("dialog.title"));
		createContents();
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(frame);
	}

	private void createContents() {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		createMainContent();
		createSouthContent();
		getRootPane().setDefaultButton(generateButton);
	}

	private void createMainContent() {
		JPanel mainContentPanel = new JPanel();
		mainContentPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		outputPanel = new OutputDirectoryPanel();
		mainContentPanel.add(outputPanel, gbc);
		getContentPane().add(mainContentPanel);
	}

	private void createSouthContent() {
		JPanel sourthContentPanel = new JPanel(new GridLayout(1, 2, GAP, GAP));
		generateButton = new GenerateButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		outputPanel.getOutputDirectoryText().getDocument()
				.addDocumentListener(new DocumentListener() {
					public void removeUpdate(DocumentEvent e) {
						handleButton(e);
					}

					public void insertUpdate(DocumentEvent e) {
						handleButton(e);
					}

					public void changedUpdate(DocumentEvent e) {
					}

					private void handleButton(DocumentEvent e) {
						String path = outputPanel.getOutputPath();
						if (path.length() == 0) {
							generateButton.setEnabled(false);
						} else {
							generateButton.setEnabled(true);
						}
					}
				});
		sourthContentPanel.add(generateButton);
		sourthContentPanel.add(new CancelButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		}));
		getContentPane().add(sourthContentPanel);
	}

	protected void close() {
		setVisible(false);
	}

	private void execute() {
		Cursor originalCursor = this.getCursor();
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			String outputPath = outputPanel.getOutputPath();
			ConfigUtil.saveCodeOutputPath(outputPath);

			IDiagram currentDiagram = ModelUtils.getCurrentDiagram();

			Marshaller marshaller = new Marshaller();
			marshaller.marshal(currentDiagram,outputPath);
			
			util.showInformationMessage(GenerateRtmProfileDialog.this,
					Messages.getMessage("success.doc.generation"));
			
			openGeneratedFolderAutomatically();
		} catch(UnSupportDiagramException use){
			util.showWarningMessage(GenerateRtmProfileDialog.this, use.getMessage() );
		} catch (ValidationException ve) {
			String errorDetailMessage = ve.getMessage().replaceAll(SystemUtils.LINE_SEPARATOR, "<br />");
			ErrorInfo errInfo = new ErrorInfo(
			        "Warning",
			        Messages.getMessage("validationerror.occur"),
			        errorDetailMessage,
			        null,
			        null,
			        org.jdesktop.swingx.error.ErrorLevel.WARNING,
			        null);
			JXErrorPane.showDialog(GenerateRtmProfileDialog.this, errInfo);
		} catch (Exception ex) {
			logger.error(Messages.getMessage("unexpectedexception.occur"),ex);
			ErrorInfo errInfo = new ErrorInfo(
			        "Alert",
			        Messages.getMessage("unexpectedexception.occur"),
			        null,
			        null,
			        ex,
			        org.jdesktop.swingx.error.ErrorLevel.WARNING,
			        null);
			JXErrorPane.showDialog(GenerateRtmProfileDialog.this, errInfo);
		} finally {
			this.setCursor(originalCursor);
		}

		close();
	}

	public IDiagram getCurrentDiagram() throws InvalidUsingException, ClassNotFoundException {
		return AstahAPI.getAstahAPI().getProjectAccessor().getViewManager().getDiagramViewManager()
				.getCurrentDiagram();
	}
	
	private void openGeneratedFolderAutomatically() {
		if (outputPanel.getAutoOpenBtn()) {
			String url = outputPanel.getOutputPath();
			url = FilenameUtils.separatorsToSystem(url);
			String command = "";
			if(SystemUtils.IS_OS_WINDOWS){
				command = "explorer";
				url = "/root," + url;
			}else if (SystemUtils.IS_OS_MAC){
				command = "open";
			}else if (SystemUtils.IS_OS_LINUX){
				command = "xdg-open";
			}else{
				util.showWarningMessage(GenerateRtmProfileDialog.this, "Open generated folder automatically function does not support your os.");
			}
			
			try{
				new ProcessBuilder(command,url).start();
			}catch(IOException e){
				util.showWarningMessage(GenerateRtmProfileDialog.this, "Open generated folder automatically function does not support your environment.");				
			}
		}
	}
	
	class GenerateButton extends JButton {
		static final String NAME = "generate";

		private GenerateButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}

	class CancelButton extends JButton {
		static final String NAME = "cancel";

		private CancelButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame(GenerateRtmProfileDialog.class.getName());
		GenerateRtmProfileDialog p = new GenerateRtmProfileDialog(frame);
		frame.setSize(400, 300);
		p.pack();
		p.setVisible(true);
	}
}
