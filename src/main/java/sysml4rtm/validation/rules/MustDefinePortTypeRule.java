package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class MustDefinePortTypeRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		IAttribute part = (IAttribute) target;
		IBlock block = (IBlock) part.getType();
		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port)) {
				continue;
			}
			if (hasItemFlow(port)) {
				if (ModelUtils.getConveyDataType(port.getItemFlows()[0]) == null) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.port_type_not_define",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port, this));
					return false;
				}

				if (!ModelUtils.hasItemPropertiesHaveSameType(port.getItemFlows())) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.type_must_same",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port, this));
					return false;
				}
			} else {
				if (!hasFlowProperties(port)) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.port_type_not_define",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port, this));
					return false;
				}

				IBlock type = (IBlock) port.getType();
				if (!ModelUtils.hasFlowPropertieshaveSameType(type.getFlowProperties())) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.type_must_same",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port, this));
					return false;
				}
			}

		}
		return true;
	}

	private boolean hasFlowProperties(IPort port) {
		IBlock type = (IBlock) port.getType();
		if (type == null)
			return false;

		IFlowProperty[] flowProperties = type.getFlowProperties();
		return flowProperties != null && flowProperties.length > 0;
	}

	private boolean hasItemFlow(IPort port) {
		IItemFlow[] itemFlows = port.getItemFlows();
		return itemFlows != null && itemFlows.length > 0;
	}

}
