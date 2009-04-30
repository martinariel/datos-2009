package ar.com.datos.wordservice.exception;

public class InactiveSessionException extends RuntimeException {

	private static final long serialVersionUID = 1273780142071401370L;

	public InactiveSessionException() {
		super();
	}

	public InactiveSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public InactiveSessionException(String message) {
		super(message);
	}

	public InactiveSessionException(Throwable cause) {
		super(cause);
	}

}
