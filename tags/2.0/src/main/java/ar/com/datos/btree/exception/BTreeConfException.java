package ar.com.datos.btree.exception;

import java.util.ArrayList;
import java.util.List;

public class BTreeConfException extends RuntimeException {
	private static final long serialVersionUID = 8217213998866164619L;
	private List<Throwable> causes;

	/**
	 *
	 */
	public BTreeConfException() {
		super();
	}
	/**
	 * @param message
	 */
	public BTreeConfException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public BTreeConfException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public BTreeConfException(Throwable cause) {
		super(cause);
	}

	public BTreeConfException(String message, List<Throwable> causes) {
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
