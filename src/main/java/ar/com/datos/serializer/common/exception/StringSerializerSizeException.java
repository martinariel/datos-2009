package ar.com.datos.serializer.common.exception;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class StringSerializerSizeException extends RuntimeException {
	private List<Throwable> causes;

	public StringSerializerSizeException() {
		super();
	}

	public StringSerializerSizeException(String message) {
		super(message);
	}

	public StringSerializerSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	public StringSerializerSizeException(Throwable cause) {
		super(cause);
	}

	public StringSerializerSizeException(String message, List<Throwable> causes) {
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
