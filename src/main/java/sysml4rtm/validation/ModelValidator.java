package sysml4rtm.validation;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.INamedElement;

public class ModelValidator {

	
    public List<ValidationError> validate(List<? extends INamedElement> parts) {
        ValidationRuleManager manager = ValidationRuleManager.getInstance();
        return validate(manager.getValidationRule(), parts);
    }

    private List<ValidationError> validate(List<ValidationRule> rules, List<? extends INamedElement> targetModels) {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        for (INamedElement namedElement : targetModels) {
            for(ValidationRule rule : rules){
                if(rule.isTargetModel(namedElement)){
                    if(!rule.validate(namedElement)) {
                        errors.addAll(rule.getResults());
                    }
                }
            }
        }

        return errors;
    }

}
