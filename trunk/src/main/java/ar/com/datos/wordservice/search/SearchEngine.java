package ar.com.datos.wordservice.search;

import java.util.List;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.util.Tuple;

/**
 * Encargado de la consulta de documentos dentro del FTRS.
 * Busca a partir de una query un número de resultados ordenados por relevancia. 
 * 
 * @see QueryResolver
 * 
 * @author fvalido
 */
public interface SearchEngine {
	/**
	 * Realiza la búsqueda contenida en query, devolviendo los primeros maxResults
	 * ordenados por relevancia. Para resolver la query y el orden de relevancia
	 * utiliza el {@link QueryResolver} por defecto para la implementación (clase
	 * u objeto) actual.
	 * 
	 * @param query
	 * Documento cuyo contenido es el query buscado.
	 * @param maxResults
	 * Máxima cantidad de resultados a obtener.
	 * @return
	 * Lista ordenada de documentos por orden de ranking (superior ranking primero).
	 */
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults);
	/**
	 * Realiza la búsqueda contenida en query, devolviendo los primeros maxResults
	 * ordenados por relevancia. Para resolver la query y el orden de relevancia
	 * utiliza el {@link QueryResolver} recibido.
	 * 
	 * @param query
	 * Documento cuyo contenido es el query buscado.
	 * @param maxResults
	 * Máxima cantidad de resultados a obtener.
	 * @param queryResolver
	 * {@link QueryResolver} a utilizar para resolver la consulta y el orden de relevancia.
	 * @return
	 * Lista ordenada de documentos por orden de ranking (superior ranking primero).
	 */
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults, QueryResolver queryResolver);
}
