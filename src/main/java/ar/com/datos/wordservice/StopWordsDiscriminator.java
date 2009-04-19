package ar.com.datos.wordservice;

import java.util.Set;

import ar.com.datos.wordservice.exception.ActiveSessionException;

/**
 * Un StopWordsDiscriminator se encarga de discriminar que palabras son o no
 * stopwords. 
 * 
 * Las stopwords son palabras que aparecen muchas veces en un documento y que
 * suelen no ser irrelevantes para el mismo. Estas palabras no deben indexarse
 * con lo cual es necesario discriminarlas.
 * 
 * Las implementaciones de StopWordsDiscriminator funcionaran mediante sesiones.
 * Para cada sesion se almacenara las palabras stopwords y las nonstopwords de 
 * manera que el usuario de dicha implementacion pueda consultarlas al finalizar
 * la sesion.
 *  
 * @author Marcos J. Medrano
 */
public interface StopWordsDiscriminator extends SessionHandler {
	
	/**
	 * Procesa una palabra para ver si es o no una stopword. El resultado de
	 * cada procesamiento deberia ser almacenado en dos {@link Set}s. Uno para
	 * stopwords y otro para nonstopwords. 
	 * @param word la palabra a analizar
	 * @throws {@link ActiveSessionException} si la session no esta activa
	 */
	public void processWord(String word);
	
	/**
	 * Devuelve en un {@link Set<String>} las stopwords encontradas durante la 
	 * sesion. La sesion debe estar cerrada o se lanzara una excepcion.  
	 * @throws {@link ActiveSessionException} si la sesion esta activa
	 * @return {@link Set<String>} las stopwords procesadas en la session
	 */
	public Set<String> getStopWords();
	
	/**
	 * Devuelve en un {@link Set<String>} las stopwords encontradas durante la 
	 * sesion. La sesion debe estar cerrada o se lanzara una excepcion.  
	 * @throws {@link ActiveSessionException} si la session esta activa
	 * @return {@link Set<String>} las palabras que no son stopwords de la session
	 */
	public Set<String> getNonStopWords();
}
