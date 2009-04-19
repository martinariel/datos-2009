package ar.com.datos.wordservice.exception;

public class ActiveSessionException extends RuntimeException {
	public ActiveSessionException(String msg){
		super(msg);
	}
}
