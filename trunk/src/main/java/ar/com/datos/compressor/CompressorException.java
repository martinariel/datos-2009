package ar.com.datos.compressor;

import java.util.ArrayList;
import java.util.List;

public class CompressorException extends RuntimeException {
	private static final long serialVersionUID = 4889345573773438898L;
	private List<Throwable> causes;

	/**
	 *
	 */
	public CompressorException() {
		super();
	}
	/**
	 * @param message
	 */
	public CompressorException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public CompressorException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public CompressorException(Throwable cause) {
		super(cause);
	}

	public CompressorException(String message, List<Throwable> causes) {
		super(message);
		this.causes = causes;
	}

	public List<Throwable> getCauses() {
		if(causes != null && !causes.isEmpty()) {
			return causes;
		} else {
			List<Throwable> newListOfCauses = new ArrayList<Throwable>();
			newListOfCauses.add(this);
			return newListOfCauses;
		}
	}
}
