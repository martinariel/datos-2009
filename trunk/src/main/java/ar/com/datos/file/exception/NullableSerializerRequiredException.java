package ar.com.datos.file.exception;

public class NullableSerializerRequiredException extends RuntimeException {

	private static final long serialVersionUID = -6283769849092108181L;

	public NullableSerializerRequiredException() {
		super();
	}

	public NullableSerializerRequiredException(String message) {
		super(message);
	}

	public NullableSerializerRequiredException(Throwable cause) {
		super(cause);
	}

	public NullableSerializerRequiredException(String message, Throwable cause) {
		super(message, cause);
	}

}
