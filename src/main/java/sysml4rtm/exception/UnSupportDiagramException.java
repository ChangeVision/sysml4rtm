package sysml4rtm.exception;

public class UnSupportDiagramException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnSupportDiagramException() {
	}

	public UnSupportDiagramException(String message) {
		super(message);
	}

	public UnSupportDiagramException(Throwable cause) {
		super(cause);
	}

	public UnSupportDiagramException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnSupportDiagramException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
