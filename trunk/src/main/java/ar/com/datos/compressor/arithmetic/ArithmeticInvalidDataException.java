package ar.com.datos.compressor.arithmetic;

public class ArithmeticInvalidDataException extends RuntimeException {

	private static final long serialVersionUID = -83701653218101110L;

	public ArithmeticInvalidDataException() {
	}

	public ArithmeticInvalidDataException(String message) {
		super(message);
	}

	public ArithmeticInvalidDataException(Throwable cause) {
		super(cause);
	}

	public ArithmeticInvalidDataException(String message, Throwable cause) {
		super(message, cause);
	}

}
