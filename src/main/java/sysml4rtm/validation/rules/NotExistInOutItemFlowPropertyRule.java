package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class NotExistInOutItemFlowPropertyRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		IAttribute part = (IAttribute) target;
		IBlock block = (IBlock) part.getType();
		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port))
				continue;

			IItemFlow[] itemFlows = port.getItemFlows();
			if (itemFlows.length == 0)
				continue;

			DataPortType direction = ModelUtils.getDirection(part, itemFlows);
			if (direction.equals(DataPortType.INOUT)) {
				setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage(
						"error.inout_itemproperty_not_support", ModelUtils.getPartName(part),
						ModelUtils.getPortName(port)), port, this));
				return false;
			}
		}

		return true;
	}

}
