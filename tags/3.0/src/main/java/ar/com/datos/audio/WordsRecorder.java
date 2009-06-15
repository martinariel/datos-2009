package ar.com.datos.audio;

import java.util.Collection;

public interface WordsRecorder {

	/**
	 * Detiene la grabacion
	 *
	 */
	public abstract void stopRecording();

	/**
	 * Analiza la existencia de cada una de las palabras de la coleccion,
	 * si una de ellas no existe se solicitara su grabacion.
	 *
	 * @param palabras
	 */
	public abstract void recordWords(Collection<String> palabras);

}