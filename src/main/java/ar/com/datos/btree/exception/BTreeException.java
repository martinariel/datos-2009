package ar.com.datos.btree.exception;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BTreeException extends RuntimeException {
	private List<Throwable> causes;

	/**
	 *
	 */
	public BTreeException() {
		super();
	}
	/**
	 * @param message
	 */
	public BTreeException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public BTreeException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public BTreeException(Throwable cause) {
		super(cause);
	}

	public BTreeException(String message, List<Throwable> causes) {
		super(message);
		this.causes = causes;
	}

	public List getCauses() {
		if(causes != null && !causes.isEmpty()) {
			return causes;
		} else {
			List<Throwable> newListOfCauses = new ArrayList<Throwable>();
			newListOfCauses.add(this);
			return newListOfCauses;
		}
	}
}
