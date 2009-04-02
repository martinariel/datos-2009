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
    void notifyWord(String palabra);

    /**
     * Determina si puede empezar a grabar
     * @return
     */
    boolean canStartRecording();

    /**
     * Determina si la grabacion es correcta
     * @return
     */
    boolean wordRecordedOk();

    /**
     * Notifica un error de grabacion
     *
     */
    void notifyRecordingError();

    /**
     * Notifica que se inicio la grabacion de audio
     *
     */
    void recordingStarted();
}
