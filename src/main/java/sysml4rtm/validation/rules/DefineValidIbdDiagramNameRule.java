package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import validation.DefaultValidationRule;
import validation.ValidationError;

import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class DefineValidIbdDiagramNameRule extends DefaultValidationRule {

	@Override
	public boolean isTargetModel(INamedElement target) {
		return target instanceof IInternalBlockDiagram;
	}

	@Override
	public boolean validateRule(INamedElement target) throws Exception {
		IInternalBlockDiagram diagram = (IInternalBlockDiagram) target;
		if (!isValidDiagramName(diagram.getName())) {
			setResult(new ValidationError(Constants.VALIDATION_ERROR_CATEGORY,Messages.getMessage("error.invalid_diagram_name"),
					target, this));
			return false;
		}
		return true;
	}

	boolean isValidDiagramName(String name) {
		if (!name.matches("[\\p{Alpha}\\p{Digit}_]*")) {
			return false;
		}
		return true;
	}
}
