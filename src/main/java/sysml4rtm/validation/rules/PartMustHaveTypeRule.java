package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.INamedElement;

public class PartMustHaveTypeRule extends DefaultValidationRule {


	@Override
	public boolean validateRule(INamedElement target) {
		if (target instanceof IAttribute) {
			IAttribute attr = (IAttribute) target;
			if (attr.getType() == null){
				setResult(new ValidationError(Messages.getMessage("error.include_unknowntype"), target,this));
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

}
