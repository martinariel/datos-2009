package ar.com.datos.file.exception;

public class OutOfBoundsException extends RuntimeException {

	private static final long serialVersionUID = -2487632653285335966L;

	public OutOfBoundsException() {
		super();
	}

	public OutOfBoundsException(String message) {
		super(message);
	}

	public OutOfBoundsException(Throwable cause) {
		super(cause);
	}

	public OutOfBoundsException(String message, Throwable cause) {
		super(message, cause);
	}

}
