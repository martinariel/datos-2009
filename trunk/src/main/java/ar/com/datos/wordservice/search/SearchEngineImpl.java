package ar.com.datos.wordservice.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentFromDocumentLibrary;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.IndexedTerm;
import ar.com.datos.indexer.Indexer;
import ar.com.datos.util.Tuple;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;

/**
 * Implementación de {@link SearchEngine}.
 * 
 * @author fvalido
 */
public class SearchEngineImpl implements SearchEngine {
	private QueryResolver queryResolver;
	private Indexer<OffsetAddress> indexer;
	private DocumentLibrary documentLibrary;
	
	/**
	 * Crea una instancia que usará como {@link QueryResolver} a {@link SimpleQueryResolver}.
	 */
	public SearchEngineImpl(Indexer<OffsetAddress> indexer, DocumentLibrary documentLibrary, StopWordsDiscriminator discriminator) {
		this.indexer = indexer;
		this.queryResolver = new SimpleQueryResolver(discriminator);
		this.documentLibrary = documentLibrary;
	}
	
	/**
	 * Crea una instancia que usará como el queryResolverPasado.
	 */
	public SearchEngineImpl(Indexer<OffsetAddress> indexer, DocumentLibrary documentLibrary, QueryResolver queryResolver) {
		this.indexer = indexer;
		this.queryResolver = queryResolver;
		this.documentLibrary = documentLibrary;
	}	
	
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.wordservice.search.SearchEngine#lookUp(ar.com.datos.documentlibrary.Document, int)
	 */
	@Override
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults) {
		return lookUp(query, maxResults, this.queryResolver);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.wordservice.search.SearchEngine#lookUp(ar.com.datos.documentlibrary.Document, int, ar.com.datos.wordservice.search.QueryResolver)
	 */
	@Override
	public List<Tuple<Double, Document>> lookUp(Document query, int maxResults, QueryResolver queryResolver) {
		Set<String> terms = queryResolver.getQueryTerms(query);
		Map<String, IndexedTerm<OffsetAddress>> termsData = new HashMap<String, IndexedTerm<OffsetAddress>>();

		// Mediante el indexer busco cada término obteniendo una representación que posee las listas, etc.
		Iterator<String> itTerms = terms.iterator();
		String term;
		IndexedTerm<OffsetAddress> termData;
		while (itTerms.hasNext()) {
			term = itTerms.next();
			
			termData = this.indexer.findTerm(term);
			if (termData != null) {
				termsData.put(term, termData);
			}
		}

		// Resulvo la consulta delegando al queryResolver.
		long documentCount = this.indexer.getNumberOfIndexedTerms(); // FIXME: Es el total de documentos !!
		Iterator<Tuple<Double, OffsetAddress>> itSimilarityOffset = queryResolver.resolveQuery(termsData, documentCount, query, maxResults).iterator();

		List<Tuple<Double, Document>> returnValue = new LinkedList<Tuple<Double,Document>>();
		Tuple<Double, OffsetAddress> current;
		while (itSimilarityOffset.hasNext()) {
			current = itSimilarityOffset.next();
			returnValue.add(new Tuple<Double, Document>(current.getFirst(), new DocumentFromDocumentLibrary(this.documentLibrary, current.getSecond())));
		}
		
		return returnValue;
	}

}
