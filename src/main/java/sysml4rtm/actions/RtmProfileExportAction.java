package sysml4rtm.actions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import sysml4rtm.dialog.GenerateRtmProfileDialog;

import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;

public class RtmProfileExportAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {
		try {
			JFrame frame = (JFrame) window.getParent();
			GenerateRtmProfileDialog dialog = new GenerateRtmProfileDialog(frame);
			dialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(), "Unexpected error has occurred.",
					"Alert", JOptionPane.ERROR_MESSAGE);
			throw new UnExpectedException();
		}
		return null;
	}

}
