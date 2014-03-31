package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.utils.ModelUtils;
import sysml4rtm.validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class NotExistInOutFlowPropertyRule extends DefaultValidationRule{


	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		return validateInoutFlowpropertyNotSupport((IAttribute) target);
	}
	
	
	private boolean validateInoutFlowpropertyNotSupport(IAttribute attr) {
		IBlock block = (IBlock) attr.getType();
		for (IPort port : block.getPorts()) {
			IBlock portType = (IBlock) port.getType();
			if (portType == null)
				continue;
			
			IFlowProperty[] flowProperties = portType.getFlowProperties();
			if (flowProperties == null || flowProperties.length == 0)
				continue;
			
			DataPortType direction = ModelUtils.getDirection(flowProperties);
			if (direction.equals(DataPortType.INOUT)) {
				setResult(new ValidationError(Messages.getMessage("error.inout_flowproperty_not_support",ModelUtils.getPartName(attr),
						ModelUtils.getPortName(port)), attr));
				return false;
			}
		}
		
		return true;
	}
	
}
