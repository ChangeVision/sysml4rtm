package sysml4rtm.exceptions;

import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import validation.ValidationError;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private List<ValidationError> errors;

	public ValidationException(List<ValidationError> errors) {
		this.errors = errors;
	}
	
	public List<ValidationError> getErrors(){
		return this.errors;
	}

	@Override
	public String getMessage() {
		StringBuilder msg = new StringBuilder();
		for(ValidationError error : errors){
			msg.append(error.getMessage()).append(SystemUtils.LINE_SEPARATOR);
		}
		return msg.toString();
	}
}
