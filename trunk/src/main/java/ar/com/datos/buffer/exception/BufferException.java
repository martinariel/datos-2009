package ar.com.datos.buffer.exception;

import java.util.ArrayList;
import java.util.List;

public class BufferException extends RuntimeException {
	private static final long serialVersionUID = 1914623651661956043L;
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
