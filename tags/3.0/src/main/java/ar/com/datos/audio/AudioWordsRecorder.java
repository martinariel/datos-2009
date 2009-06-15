package ar.com.datos.audio;

import java.util.Collection;

import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;

/**
 * Grabador de palabras
 *
 * @author mfernandez
 *
 */
public class AudioWordsRecorder implements AudioStopper, WordsRecorder{
    private AudioServiceHandler servicioAudio;
    private SoundPersistenceService servicioArchivos;
    private IWordsRecorderConector interfazUsuario;
    private SoundByteArrayOutputStream audio;
    private String palabraActual;
    private boolean boostMic;

    public AudioWordsRecorder(IWordsRecorderConector view, SoundPersistenceService audioService, boolean boostMic ){
   	 servicioAudio = AudioServiceHandler.getInstance();
        this.servicioArchivos = audioService;
        this.interfazUsuario = view;
        //Le envio el stopper a la interfaz de usuario
        this.interfazUsuario.sendStopper(this);
        this.boostMic = boostMic;
   }
    
    public AudioWordsRecorder(IWordsRecorderConector interfazUsuario, SoundPersistenceService servicioArchivos) {
    	this(interfazUsuario, servicioArchivos, false);
    }

    /**
     * Graba el audio de una palabra
     *
     * @param palabra
     */
    private void grabarPalabra(){

        if (interfazUsuario.canStartRecording()){
            //Grabo en memoria!!
        	audio = new SoundByteArrayOutputStream(512 * 1024);
			servicioAudio.record(audio);
            interfazUsuario.recordingWordStarted();
        }
    }

    public void stop(){
        stopRecording();
    }

    /* (non-Javadoc)
	 * @see ar.com.datos.audio.WordsRecorder#stopRecording()
	 */
    public void stopRecording(){
        if (servicioAudio.isRecording()){
        	audio.setAcceptData(!boostMic);
        	interfazUsuario.sendMessageLn("Grabación detenida. Esperando a que el device devuelva el control... ");
        	servicioAudio.stopRecording();
        	interfazUsuario.sendMessageLn("Listo!");
        	
            //Supongo que tengo memoria suficiente!

            AnotherInputStream inputAudio = new AnotherInputStream(audio.toByteArray());
            
            inputAudio.mark(Integer.MAX_VALUE);
            Thread reproduccion = servicioAudio.play(inputAudio);

            try {
                reproduccion.join();
            }
            catch(InterruptedException e){
                System.out.println("Thread principal interrumpido");
            }

            inputAudio.reset();

            if (interfazUsuario.recordingWordOK()) {
                try {
                    servicioArchivos.addWord(palabraActual, inputAudio);
                }
                catch (WordIsAlreadyRegisteredException e) {
                    interfazUsuario.recordingWordError();
                }
            }
            else {
                grabarPalabra();
            }
        }
    }


    /* (non-Javadoc)
	 * @see ar.com.datos.audio.WordsRecorder#recordWords(java.util.Collection)
	 */
    public void recordWords(Collection<String> palabras){

        for (String palabra : palabras){

            if (!servicioArchivos.isRegistered(palabra)){
                palabraActual = palabra;
                interfazUsuario.notifyNextWord(palabraActual);
                grabarPalabra();
            }

        }

        interfazUsuario.recordingAllWordsEnd();
    }
}
