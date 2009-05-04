package ar.com.datos.wordservice.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.DocumentLibrary;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.Indexer;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.KeyCount;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;

/**
 * Implementación de {@link SearchEngine}.
 * 
 * @author fvalido
 */
public class SeachEngineImpl implements SearchEngine {
	private QueryResolver queryResolver;
	private Indexer indexer;
	private DocumentLibrary documentLibrary;
	
	/**
	 * Crea una instancia que usará como {@link QueryResolver} a {@link SimpleQueryResolver}.
	 */
	public SeachEngineImpl(Indexer<OffsetAddress> indexer, DocumentLibrary documentLibrary, StopWordsDiscriminator discriminator) {
		this.indexer = indexer;
		this.documentLibrary = documentLibrary;
		this.queryResolver = new SimpleQueryResolver(discriminator);
	}
	
	/**
	 * Crea una instancia que usará como el queryResolverPasado.
	 */
	public SeachEngineImpl(Indexer<OffsetAddress> indexer, DocumentLibrary documentLibrary, QueryResolver queryResolver) {
		this.indexer = indexer;
		this.documentLibrary = documentLibrary;
		this.queryResolver = queryResolver;
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
		Map<String, Tuple<Integer, List<KeyCount<OffsetAddress>>>> termsData = new HashMap<String, Tuple<Integer,List<KeyCount<OffsetAddress>>>>();

		Iterator<String> itTerms = terms.iterator();
		String term;
		Tuple<Integer, List<KeyCount<OffsetAddress>>> termData;
		while (itTerms.hasNext()) {
			term = itTerms.next();
			
			// FIXME: Cuando Juan lo haya hecho. termData = indexer.findTerm(term);
			termData = null;
			termsData.put(term, termData);
		}

		List<Tuple<Double, Document>> documents = new LinkedList<Tuple<Double,Document>>();
		Iterator<Tuple<Double, OffsetAddress>> itDocs = queryResolver.resolveQuery(termsData, query, maxResults).iterator();
		Tuple<Double, OffsetAddress> docsSimilarity;
		while (itDocs.hasNext()) {
			docsSimilarity = itDocs.next();
			documents.add(new Tuple<Double, Document>(docsSimilarity.getFirst(), this.documentLibrary.get(docsSimilarity.getSecond())));
		}
		
		return documents;
	}

}
