package sysml4rtm.rtc.export.profilebuilder;

import org.openrtp.namespaces.rtc.ActionStatus;
import org.openrtp.namespaces.rtc.Actions;
import org.openrtp.namespaces.rtc.BasicInfo;

import sysml4rtm.constants.Constants;

public class ActionBuilder {

	public Actions build(BasicInfo basicinfo) {
		Actions actions = new Actions();

		actions.setOnInitialize(getActionStatus(true));

		if (basicinfo.getComponentKind().equals(Constants.ComponentKind.DFC.toString())) {
			actions.setOnExecute(getActionStatus(true));
		}

		return actions;
	}

	private ActionStatus getActionStatus(boolean isImplemented) {
		ActionStatus actionStatus = new ActionStatus();
		actionStatus.setImplemented(isImplemented);
		return actionStatus;
	}

}
