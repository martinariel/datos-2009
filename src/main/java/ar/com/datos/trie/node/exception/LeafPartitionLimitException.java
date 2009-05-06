package ar.com.datos.trie.node.exception;

import java.util.ArrayList;
import java.util.List;

public class LeafPartitionLimitException extends RuntimeException {

	private static final long serialVersionUID = 8226363881863823462L;
	private List<Throwable> causes;

	public LeafPartitionLimitException() {
		super();
	}

	public LeafPartitionLimitException(String message) {
		super(message);
	}

	public LeafPartitionLimitException(String message, Throwable cause) {
		super(message, cause);
	}

	public LeafPartitionLimitException(Throwable cause) {
		super(cause);
	}

	public LeafPartitionLimitException(String message, List<Throwable> causes) {
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
