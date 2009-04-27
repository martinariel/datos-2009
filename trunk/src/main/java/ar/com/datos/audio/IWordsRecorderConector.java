package ar.com.datos.audio;

/**
 * Interfaz conectora entre interfaz de usuario y un GrabadorPalabras
 * @author mfernandez
 *
 */
public interface IWordsRecorderConector {
    /**
     * Notifica la grabacion de una palabra
     * @param palabra
     */
    void notifyNextWord(String palabra);

    /**
     * Determina si puede empezar a grabar
     * @return
     */
    boolean canStartRecording();

    /**
     * Determina si la grabacion es correcta
     * @return
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
     * @param message
     */
    void sendMessage(String message);

    /**
     * Recibe el stopper de audio
     * @param stopper
     */
    void sendStopper(AudioStopper stopper);
}
