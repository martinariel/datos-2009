package ar.com.datos.wordservice.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.KeyCount;

/**
 * Permite discriminar los términos de una consulta, y de acuerdo a los datos almacenados por el FTRS para
 * ese término resolver los documentos que matchean esa consulta y ordenarlos por relevancia.
 * 
 * @author fvalido
 */
public interface QueryResolver {
	/**
	 * Obtiene los términos relevantes para la consulta de manera normalizada.
	 */
	public Set<String> getQueryTerms(Document query);

	/**
	 * Resuelve la consulta (resultados que matchean) y la ordena devolviendo los maxResults resultados más
	 * relevantes.
	 * 
	 * @param termsData
	 * Mapa cuya clave es un término, y cuyo valor es la unión de la cantidad de documentos en que aparece
	 * ese término y un listado de cantidad de apariciones por documento (documento es el OffsetAddress del
	 * documento dentro de {@link DocumentLibrary}).
	 * Los términos que debe contener el mapa son los devueltos por {@link #getQueryTerms(Document)}
	 * cuando recibe como parámetro a query. 
	 * @param query
	 * Consulta a resolver.
	 * @param maxResults
	 * Máxima cantidad de documentos a devolver.
	 * @return
	 * Lista de tuplas formadas por la relevancia y el offset del documento en {@link DocumentLibrary}.
	 */
	public List<Tuple<Double, OffsetAddress>> resolveQuery(Map<String, Tuple<Integer, List<KeyCount<OffsetAddress>>>> termsData,
										Document query, int maxResults); 
}
