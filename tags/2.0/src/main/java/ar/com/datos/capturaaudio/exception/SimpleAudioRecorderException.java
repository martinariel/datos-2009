package ar.com.datos.capturaaudio.exception;

public class SimpleAudioRecorderException extends Exception{

	private static final long serialVersionUID = -1792940801322387694L;

	public SimpleAudioRecorderException(String msg){
		super(msg);
	}

	public SimpleAudioRecorderException(){
		super("SimpleAudioRecorder Exception");
	}

	public SimpleAudioRecorderException(Exception e){
		super(e);
	}
}
