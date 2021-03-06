package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.utils.ModelUtils;
import validation.DefaultValidationRule;
import validation.ValidationError;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class NotExistInOutFlowPropertyRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws Exception {
		return validateInoutFlowpropertyNotSupport((IAttribute) target);
	}

	private boolean validateInoutFlowpropertyNotSupport(IAttribute attr) {
		IBlock block = (IBlock) attr.getType();
		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port))
				continue;

			IBlock portType = (IBlock) port.getType();
			if (portType == null)
				continue;

			IFlowProperty[] flowProperties = portType.getFlowProperties();
			if (flowProperties == null || flowProperties.length == 0)
				continue;

			DataPortType direction = ModelUtils.getDirection(flowProperties);
			if (direction.equals(DataPortType.INOUT)) {
				setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage(
						"error.inout_flowproperty_not_support", ModelUtils.getPartName(attr),
						ModelUtils.getPortName(port)), port, this));
				return false;
			}
		}

		return true;
	}

}
