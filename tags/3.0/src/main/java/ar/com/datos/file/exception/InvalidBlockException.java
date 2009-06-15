package ar.com.datos.file.exception;

public class InvalidBlockException extends RuntimeException {

	private static final long serialVersionUID = 3004080031307581091L;

	public InvalidBlockException() {
		super();
	}

	public InvalidBlockException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidBlockException(String message) {
		super(message);
	}

	public InvalidBlockException(Throwable cause) {
		super(cause);
	}

}
