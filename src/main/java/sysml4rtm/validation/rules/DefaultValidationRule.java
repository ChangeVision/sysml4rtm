package sysml4rtm.validation.rules;

import java.util.ArrayList;
import java.util.List;

import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.validation.ValidationError;
import sysml4rtm.validation.ValidationRule;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.INamedElement;

public abstract class DefaultValidationRule implements ValidationRule {

    private List<ValidationError> results = new ArrayList<ValidationError>();

    public void setResult(ValidationError result) {
        results.clear();
        results.add(result);
    }

    @Override
    public List<ValidationError> getResults() {
        return results;
    }

    @Override
    public boolean validate(INamedElement target) {
    	try{
    		return validateRule(target);
    	}catch(Exception e){
    		throw new ApplicationException(e);
    	}
    }
    
    abstract public boolean validateRule(INamedElement target) throws InvalidUsingException;
    	
}
