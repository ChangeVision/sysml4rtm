package sysml4rtm.exception;

public class AstahApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AstahApiException() {
	}

	public AstahApiException(String message) {
		super(message);
	}

	public AstahApiException(Throwable cause) {
		super(cause);
	}

	public AstahApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public AstahApiException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
