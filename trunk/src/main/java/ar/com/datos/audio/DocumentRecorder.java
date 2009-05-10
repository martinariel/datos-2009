package ar.com.datos.audio;

import java.util.Collection;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.parser.Parser;

/**
 * @deprecated
 *
 * @author mfernandez
 *
 */
public class DocumentRecorder implements IWordsRecorderConector, AudioStopper {

    private WordsRecorder grabador;
    private IWordsRecorderConector vista;

    public DocumentRecorder(IWordsRecorderConector vista,SoundPersistenceService servicioPersistencia) {
        this.vista = vista;
        grabador = new WordsRecorder(this,servicioPersistencia);
    }

    /**
     * Graba un documento
     * @param documento
     */
    public void record (Document documento){
        Parser parser = new Parser(documento);

        for (Collection<String> oracion : parser){
            grabador.recordWords(oracion);
        }

        vista.recordingAllWordsEnd();
    }

    public void stop(){
        grabador.stop();
    }

    public boolean canStartRecording() {
        return vista.canStartRecording();
    }

    public void notifyNextWord(String palabra) {
        vista.notifyNextWord(palabra);

    }

    public void recordingAllWordsEnd() {
        //Nothing, se termino la grabacion de una oracion

    }

    public void recordingWordError() {
        vista.recordingWordError();

    }

    public boolean recordingWordOK() {
        return vista.recordingWordOK();
    }

    public void recordingWordStarted() {
        vista.recordingWordStarted();
    }

    public void sendMessageLn(String message){
        vista.sendMessageLn(message);
    }

    public void sendStopper(AudioStopper stopper){
        vista.sendStopper(stopper);
    }

	public void sendMessage(String message) {
		vista.sendMessage(message);
		
	}

}
