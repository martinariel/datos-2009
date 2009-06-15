package ar.com.datos.audio;

/**
 * Interfaz conectora entre interfaz de usuario y un GrabadorPalabras
 * @author mfernandez
 *
 */
public interface IWordsRecorderConector {
    /**
     * Notifica la grabacion de una palabra
     */
    void notifyNextWord(String palabra);

    /**
     * Determina si puede empezar a grabar
     */
    boolean canStartRecording();

    /**
     * Determina si la grabacion es correcta
     */
    boolean recordingWordOK();

    /**
     * Notifica un error de grabacion
     *
     */
    void recordingWordError();

    /**
     * Notifica que se inicio la grabacion de audio
     *
     */
    void recordingWordStarted();

    /**
     * Notifica la finalizacion de grabacion de todas las palabras
     */
    void recordingAllWordsEnd();

    /**
     * Recibe un mensaje.
     */
    void sendMessageLn(String message);

    /**
     * Recibe un mensaje.
     */
    void sendMessage(String message);
    
    /**
     * Recibe el stopper de audio
     */
    void sendStopper(AudioStopper stopper);
}
