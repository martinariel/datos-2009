package ar.com.datos.bits;

import java.util.ArrayList;
import java.util.List;

public class BitsException extends RuntimeException {
	private static final long serialVersionUID = 4889345573773438898L;
	private List<Throwable> causes;

	/**
	 *
	 */
	public BitsException() {
		super();
	}
	/**
	 * @param message
	 */
	public BitsException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public BitsException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public BitsException(Throwable cause) {
		super(cause);
	}

	public BitsException(String message, List<Throwable> causes) {
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
