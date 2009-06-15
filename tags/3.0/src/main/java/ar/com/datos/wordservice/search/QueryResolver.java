package ar.com.datos.wordservice.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.IndexedTerm;
import ar.com.datos.util.Tuple;

/**
 * Permite discriminar los t�rminos de una consulta, y de acuerdo a los datos almacenados por el FTRS para
 * ese t�rmino resolver los documentos que matchean esa consulta y ordenarlos por similitud a la
 * consulta.
 * 
 * @author fvalido
 */
public interface QueryResolver {
	/**
	 * Obtiene los t�rminos relevantes para la consulta de manera normalizada.
	 */
	public Set<String> getQueryTerms(Document query);

	/**
	 * Resuelve la consulta (resultados que matchean) y la ordena devolviendo los maxResults resultados m�s
	 * relevantes.
	 * 
	 * @param termsData
	 * Mapa cuya clave es un t�rmino, y cuyo valor es un {@link IndexedTerm}.
	 * Los t�rminos que debe contener el mapa son los devueltos por {@link #getQueryTerms(Document)}
	 * cuando recibe como par�metro a query. 
	 * @param documentCount
	 * Cantidad de documentos totales en el sistema. Utilizado para calcular al similitud.
	 * @param query
	 * Consulta a resolver.
	 * @param maxResults
	 * M�xima cantidad de documentos a devolver.
	 * @return
	 * Lista de tuplas formadas por la similitud con la consulta y el offset del
	 * documento en {@link DocumentLibrary}.
	 */
	public List<Tuple<Double, OffsetAddress>> resolveQuery(Map<String, IndexedTerm<OffsetAddress>> termsData,
										long documentCount, Document query, int maxResults); 
}
