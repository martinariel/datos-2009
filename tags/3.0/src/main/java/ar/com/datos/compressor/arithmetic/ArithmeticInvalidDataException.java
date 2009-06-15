package ar.com.datos.compressor.arithmetic;

import ar.com.datos.compressor.CompressorException;

public class ArithmeticInvalidDataException extends CompressorException {

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
