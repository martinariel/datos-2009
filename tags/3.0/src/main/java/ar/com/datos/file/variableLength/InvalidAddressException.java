package ar.com.datos.file.variableLength;

import ar.com.datos.file.exception.OutOfBoundsException;

public class InvalidAddressException extends OutOfBoundsException {

	private static final long serialVersionUID = 3935169674679109306L;

	public InvalidAddressException() {
	}

	public InvalidAddressException(String message) {
		super(message);
	}

	public InvalidAddressException(Throwable cause) {
		super(cause);
	}

	public InvalidAddressException(String message, Throwable cause) {
		super(message, cause);
	}

}
