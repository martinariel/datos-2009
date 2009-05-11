package ar.com.datos.file.variableLength.exception;

public class InvalidUpdateException extends RuntimeException {

	private static final long serialVersionUID = -4453417551234815782L;

	public InvalidUpdateException() {
		super();
	}

	public InvalidUpdateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidUpdateException(String arg0) {
		super(arg0);
	}

	public InvalidUpdateException(Throwable arg0) {
		super(arg0);
	}

}
