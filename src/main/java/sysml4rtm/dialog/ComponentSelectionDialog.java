package sysml4rtm.dialog;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import sysml4rtm.Activator;
import sysml4rtm.Messages;
import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.rtc.export.RtcMarshaller;
import sysml4rtm.utils.ConfigUtil;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;

public class ComponentSelectionDialog extends JDialog {

	interface GenerateResultHandler {
		public void handle(List<String> merged);
	}

	private static final long serialVersionUID = -4407702420551211005L;

	private static int WIDTH = 400;
	private static int HEIGHT = 300;
	protected static final int GAP = 1;


	private IMessageDialogHandler dialogHandler = Activator.getMessageHandler();

	private IModel currentProject;

	private OutputDirectoryPanel outputDirectoryPanel;

	private ComponentSelectionPanel componentSelectionPanel;

	private AllOrPartSelectionPanel allOrPartSelectionPanel;

	private GenerateButton generateButton;

	public ComponentSelectionDialog(JFrame window, IModel model) {
		super(window,true);
		setName("ComponentSelectionDialog");
		this.currentProject = model;
		try {
			createContents();
		} catch (Exception e) {
			e.printStackTrace();
			dialogHandler.showErrorMessage(ComponentSelectionDialog.this, e.getLocalizedMessage());
		}
		setSize(WIDTH, HEIGHT);
		pack();
		setLocationRelativeTo(window.getParent());
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
		outputDirectoryPanel = new OutputDirectoryPanel();
		mainContentPanel.add(outputDirectoryPanel, gbc);
		gbc.gridy = 1;
		allOrPartSelectionPanel = new AllOrPartSelectionPanel(this);
		mainContentPanel.add(allOrPartSelectionPanel, gbc);

		componentSelectionPanel = new ComponentSelectionPanel(currentProject);
		allOrPartSelectionPanel.setTarget(componentSelectionPanel);
		getContentPane().add(mainContentPanel);
	}

	private void createSouthContent() {
		JPanel sourthContentPanel = new JPanel(new GridLayout(1, 2, GAP, GAP));
		generateButton = new GenerateButton(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
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

	@SuppressWarnings("serial")
	class GenerateButton extends JButton {
		static final String NAME = "generate";

		private GenerateButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}

	@SuppressWarnings("serial")
	class CancelButton extends JButton {
		static final String NAME = "cancel";

		private CancelButton(ActionListener listener) {
			setName(NAME);
			setText(Messages.getMessage(NAME));
			addActionListener(listener);
		}
	}

	private void execute() {
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		this.setCursor(waitCursor);

		try {
			String outputPath = tildeToHome(outputDirectoryPanel.getOutputPath());
			ConfigUtil.saveCodeOutputPath(outputPath);

			RtcMarshaller marshaller = new RtcMarshaller();
			
			boolean all = allOrPartSelectionPanel.isAllSelected();
			if(all){
				marshaller.marshal(outputPath);
			}else{
				INamedElement[] choosedComponents = componentSelectionPanel.getChoosedComponents();
				marshaller.marshal(choosedComponents, outputPath);
			}

			dialogHandler.showInformationMessage(ComponentSelectionDialog.this, Messages.getMessage("success.code.generation"));

			openGeneratedFolderAutomatically();

		} catch  (ApplicationException ex){
			String showErrorMessage = ex.getMessage();
			String errorDetailMessage = ex.getMessage();
			errorDetailMessage = showErrorMessage.replaceAll("\n", "<br />") + "<br /><br />" + errorDetailMessage;
			ErrorInfo errInfo = new ErrorInfo(
			        "Alert",
			        showErrorMessage,
			        errorDetailMessage,
			        null,
			        null,
			        org.jdesktop.swingx.error.ErrorLevel.WARNING,
			        null);
			JXErrorPane.showDialog(ComponentSelectionDialog.this, errInfo);
		} catch (Throwable ex){
			dialogHandler.showWarningMessage(ComponentSelectionDialog.this, ex.getLocalizedMessage());
		}finally{
			Cursor defaultCursor = Cursor.getDefaultCursor();
			this.setCursor(defaultCursor);
			close();
		}
	}

	private void openGeneratedFolderAutomatically() {
		if (outputDirectoryPanel.getAutoOpenBtn()) {
			String url = outputDirectoryPanel.getOutputPath();
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
				dialogHandler.showWarningMessage(ComponentSelectionDialog.this, "Open generated folder automatically function does not support your os.");
			}
			
			try{
				new ProcessBuilder(command,url).start();
			}catch(IOException e){
				dialogHandler.showWarningMessage(ComponentSelectionDialog.this, "Open generated folder automatically function does not support your environment.");				
			}
		}
	}


	private void close() {
		ComponentSelectionDialog.this.setVisible(false);
	}

	private String tildeToHome(String path) {
		return SystemUtils.IS_OS_WINDOWS? path:
			"~".equals(path)? SystemUtils.USER_HOME:
			path.startsWith("~" + SystemUtils.FILE_SEPARATOR)? SystemUtils.USER_HOME + path.substring(1): path;
	}
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame(ComponentSelectionDialog.class.getName());
		ProjectAccessorFacade.openProject("/home/iwanaga/git/sysml4rtm/src/test/resources/sysml4rtm/rtc/export/marshal_dataports.asml");
		ComponentSelectionDialog p = new ComponentSelectionDialog(frame,AstahAPI.getAstahAPI().getProjectAccessor().getProject());
		p.setSize(400, 300);
		p.pack();
		p.setVisible(true);

	}
}
