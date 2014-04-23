package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class NotExistInconsitencyItemFlowAndFlowPropertyRelationRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		return validateInconsistencyItemFlowAndFlowProperty((IAttribute) target);
	}

	private boolean validateInconsistencyItemFlowAndFlowProperty(IAttribute attr)
			throws InvalidUsingException {
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

			IItemFlow[] itemFlows = port.getItemFlows();
			if (itemFlows.length == 0)
				continue;

			DataPortType directionFromFlowProperty = ModelUtils.getDirection(flowProperties);
			if (port.isConjugated()) {
				directionFromFlowProperty = DataPortType
						.getConjugatedType(directionFromFlowProperty);
			}
			DataPortType directionFromItemFlow = ModelUtils.getDirection(attr, itemFlows);
			if (!directionFromFlowProperty.equals(directionFromItemFlow)) {
				setResult(new ValidationError(Messages.getMessage(
						"error.Inconsistency_flowproperty_itemflow", ModelUtils.getPartName(attr),
						ModelUtils.getPortName(port)), attr, this));
				return false;
			}
		}

		return true;
	}

}
