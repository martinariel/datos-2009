package ar.com.datos.capturaaudio.exception;

public class ForgotInitSimpleAudioRecorderException extends SimpleAudioRecorderException {

	private static final long serialVersionUID = -4694156368983021399L;

	public ForgotInitSimpleAudioRecorderException(){
		super("ERROR- Forgot to call SimpleAudioRecorder->init()");
	}
}
