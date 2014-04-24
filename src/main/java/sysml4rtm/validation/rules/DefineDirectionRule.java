package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.utils.ModelUtils;
import validation.DefaultValidationRule;
import validation.ValidationError;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class DefineDirectionRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws Exception {
		IAttribute part = (IAttribute) target;
		IBlock block = (IBlock) part.getType();
		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port)) {
				continue;
			}
			IItemFlow[] itemFlows = port.getItemFlows();
			IBlock portType = (IBlock) port.getType();
			if (portType == null) {
				if (itemFlows.length == 0) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.direction_not_define",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port,
							this));
					return false;
				}
			} else {
				IFlowProperty[] flowProperties = portType.getFlowProperties();
				if (itemFlows.length == 0 && flowProperties.length == 0) {
					setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.direction_not_define",
							ModelUtils.getPartName(part), ModelUtils.getPortName(port)), port,
							this));
					return false;
				}
			}
		}
		return true;
	}

}
