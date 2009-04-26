package ar.com.datos.indexer;

public class UncomparableException extends RuntimeException {

	private static final long serialVersionUID = -7946079927885590359L;

	public UncomparableException() {
	}

	public UncomparableException(String message) {
		super(message);
	}

	public UncomparableException(Throwable cause) {
		super(cause);
	}

	public UncomparableException(String message, Throwable cause) {
		super(message, cause);
	}

}
