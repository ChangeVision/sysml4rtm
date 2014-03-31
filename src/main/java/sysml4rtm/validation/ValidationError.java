package sysml4rtm.validation;

import com.change_vision.jude.api.inf.model.INamedElement;

public class ValidationError {

    private String message;
    private INamedElement target;

    public ValidationError(String message, INamedElement target) {
        this.message = message;
        this.target = target;
    }

    public String getMessage() {
        return this.message;
    }

    public INamedElement getTarget() {
        return this.target;
    }

    public String toString() {
        return this.message;
    }

}
