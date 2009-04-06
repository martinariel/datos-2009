package ar.com.datos.buffer.exception;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class BufferException extends RuntimeException {
	private List<Throwable> causes;

	public BufferException() {
		super();
	}

	public BufferException(String message) {
		super(message);
	}

	public BufferException(String message, Throwable cause) {
		super(message, cause);
	}

	public BufferException(Throwable cause) {
		super(cause);
	}

	public BufferException(String message, List<Throwable> causes) {
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
