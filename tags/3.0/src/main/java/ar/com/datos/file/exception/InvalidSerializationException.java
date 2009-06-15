package ar.com.datos.file.exception;

public class InvalidSerializationException extends RuntimeException {

	private static final long serialVersionUID = -2822711997220692685L;

	public InvalidSerializationException() {
		super();
	}

	public InvalidSerializationException(String message) {
		super(message);
	}

	public InvalidSerializationException(Throwable cause) {
		super(cause);
	}

	public InvalidSerializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
