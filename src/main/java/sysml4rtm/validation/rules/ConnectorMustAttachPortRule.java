package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class ConnectorMustAttachPortRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isBlock(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		IAttribute part = (IAttribute) target;
		IBlock block = (IBlock) part.getType();

		IConnector[] partConnectors = part.getConnectors();
		if (partConnectors.length > 0) {
			setResult(new ValidationError(Messages.getMessage("error.connector_must_attach_port",
					ModelUtils.getPartName(part)), target, this));
			return false;
		}

		for (IPort port : block.getPorts()) {
			IConnector[] connectors = port.getConnectors();
			for (IConnector connector : connectors) {
				if (!(connector.getPorts().length == 2 && connector.getPorts()[0] != null && connector
						.getPorts()[1] != null)) {
					setResult(new ValidationError(Messages.getMessage(
							"error.connector_must_attach_port", ModelUtils.getPartName(part)),
							target, this));
					return false;
				}
			}
		}

		return true;
	}

}
