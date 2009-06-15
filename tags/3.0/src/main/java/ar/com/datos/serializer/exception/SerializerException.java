package ar.com.datos.serializer.exception;

import java.util.ArrayList;
import java.util.List;

public class SerializerException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private List<Throwable> causes;

	/**
	 *
	 */
	public SerializerException() {
		super();
	}
	/**
	 * @param message
	 */
	public SerializerException(String message) {
		super(message);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public SerializerException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param cause
	 */
	public SerializerException(Throwable cause) {
		super(cause);
	}

	public SerializerException(String message, List<Throwable> causes) {
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
