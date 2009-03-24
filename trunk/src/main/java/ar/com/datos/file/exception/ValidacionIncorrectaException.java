package ar.com.datos.file.exception;

public class ValidacionIncorrectaException extends RuntimeException {

	private static final long serialVersionUID = 6571127945064203139L;

	public ValidacionIncorrectaException() {
		super();
	}

	public ValidacionIncorrectaException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidacionIncorrectaException(String message) {
		super(message);
	}

	public ValidacionIncorrectaException(Throwable cause) {
		super(cause);
	}

}
