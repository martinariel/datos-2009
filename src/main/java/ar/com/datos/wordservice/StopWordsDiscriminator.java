package ar.com.datos.wordservice;

import java.util.List;

/**
 * Un StopWordsDiscriminator se encarga de discriminar que palabras son o no
 * stopwords. 
 * 
 * Las stopwords son palabras que aparecen muchas veces en un documento y que
 * suelen no ser irrelevantes para el mismo. Estas palabras no deben indexarse
 * con lo cual es necesario discriminarlas.
 * 
 * Tambi�n hay palabras que com�nmente aparecen en locuciones, como en el caso 
 * de algunos conectores de coordinaci�n (p.e. "en consecuencia", "de cualquier 
 * manera", "en caso de que"), que en ese contexto no tienen significado 
 * relevante aunque individualmente fuera de ese contexto s�, por lo que debe 
 * considerarse la detecci�n no s�lo de palabras individuales (stop words) sino 
 * de secuencias de palabras (que llamaremos stop phrases).
 *  
 * @author Marcos J. Medrano
 */
public interface StopWordsDiscriminator{
	
	/**
	 * Procesa una frase identificando stop words o stop phrases.
	 * @param phrase una List<String> con la frase a analizar
	 * @return una List<String> de las palabras que NO son stop words ni stop phrases.
	 */
	public List<String> processPhrase(List<String> phrase);

}
