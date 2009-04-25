package ar.com.datos.wordservice.exception;

public class ActiveSessionException extends RuntimeException {
	private static final long serialVersionUID = -2161698793196301317L;

	public ActiveSessionException(String msg){
		super(msg);
	}
}
