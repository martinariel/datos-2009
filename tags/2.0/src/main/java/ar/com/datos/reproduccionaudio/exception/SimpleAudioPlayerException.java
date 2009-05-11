package ar.com.datos.reproduccionaudio.exception;

public class SimpleAudioPlayerException extends Exception{

	private static final long serialVersionUID = 2559364280531446267L;

	public SimpleAudioPlayerException(String msg){
		super(msg);
	}

	public SimpleAudioPlayerException(){
		super("ERROR- SimpleAudioPlayerException");
	}

	public SimpleAudioPlayerException(Exception e){
		super(e);
	}
}
