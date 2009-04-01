package ar.com.datos.audio;

/**
 * Interfaz conectora entre interfaz de usuario y un GrabadorPalabras
 * @author mfernandez
 *
 */
public interface IConectorUsuarioGrabadorPalabras {
    /**
     * Notifica la grabacion de una palabra
     * @param palabra
     */
    void notificarPalabra(String palabra);

    /**
     * Determina si puede empezar a grabar
     * @return
     */
    boolean iniciarGrabacion();

    /**
     * Determina si la grabacion es correcta
     * @return
     */
    boolean palabraGrabadaCorrectamente();

    /**
     * Notifica un error de grabacion
     *
     */
    void notificarErrorGrabacion();

    /**
     * Notifica que se inicio la grabacion de audio
     *
     */
    void grabacionIniciada();
}
