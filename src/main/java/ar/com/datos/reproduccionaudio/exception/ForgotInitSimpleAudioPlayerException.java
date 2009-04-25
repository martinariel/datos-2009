package ar.com.datos.reproduccionaudio.exception;

public class ForgotInitSimpleAudioPlayerException extends SimpleAudioPlayerException{
	private static final long serialVersionUID = -6384449479766586200L;

	public ForgotInitSimpleAudioPlayerException(){
		super("ERROR- Forgot to call SimpleAudioPlayer->init()");
	}
}
