package ar.com.datos.wordservice.search;

import java.util.List;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.util.Tuple;

/**
 * Encargado de la consulta de documentos dentro del FTRS.
 * Busca a partir de una query un n�mero de resultados ordenados por similitud. 
 * 
 * @see QueryResolver
 * 
 * @author fvalido
 */
public interface SearchEngine {
	/**
	 * Realiza la b�squeda contenida en query, devolviendo los primeros maxResults
	 * ordenados por similitud. Para resolver la query y el orden de similitud
	 * utiliza el {@link QueryResolver} por defecto para la implementaci�n (clase
	 * u objeto) actual.
	 * 
	 * @param query
	 * Documento cuyo contenido es el query buscado.
	 * @param maxResults
	 * M�xima cantidad de resultados a obtener.
	 * @return
	 * Lista de tuplas formadas por la similitud con la consulta y el documento ordenada 
	 * por la similitud.
	 */
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults);
	/**
	 * Realiza la b�squeda contenida en query, devolviendo los primeros maxResults
	 * ordenados por similitud. Para resolver la query y el orden de similitud
	 * utiliza el {@link QueryResolver} recibido.
	 * 
	 * @param query
	 * Documento cuyo contenido es el query buscado.
	 * @param maxResults
	 * M�xima cantidad de resultados a obtener.
	 * @param queryResolver
	 * {@link QueryResolver} a utilizar para resolver la consulta y el orden de similitud.
	 * @return
	 * Lista de tuplas formadas por la similitud con la consulta y el documento ordenada 
	 * por la similitud.
	 */
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults, QueryResolver queryResolver);
}
