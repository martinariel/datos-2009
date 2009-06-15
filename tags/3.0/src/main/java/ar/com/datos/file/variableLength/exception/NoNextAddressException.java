package ar.com.datos.file.variableLength.exception;

public class NoNextAddressException extends Exception {

	private static final long serialVersionUID = -1904136170051631329L;

	public NoNextAddressException() {
		super();
	}

	public NoNextAddressException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoNextAddressException(String message) {
		super(message);
	}

	public NoNextAddressException(Throwable cause) {
		super(cause);
	}

}
