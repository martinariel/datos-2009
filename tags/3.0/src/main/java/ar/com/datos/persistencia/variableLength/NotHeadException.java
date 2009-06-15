package ar.com.datos.persistencia.variableLength;

public class NotHeadException extends RuntimeException {

	private static final long serialVersionUID = -6458749002596135057L;

	public NotHeadException() {
		super();
	}

	public NotHeadException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotHeadException(String message) {
		super(message);
	}

	public NotHeadException(Throwable cause) {
		super(cause);
	}

}
