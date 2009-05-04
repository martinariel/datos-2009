package ar.com.datos.wordservice.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.parser.Parser;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.KeyCount;
import ar.com.datos.wordservice.stopwords.StopWordsDiscriminator;

/**
 * Implementación básica de {@link QueryResolver}.
 * No distingue términos especiales en la consulta. Ordena los resultados poniendo primero los
 * de mayor peso global.
 * 
 * @author fvalido
 */
public class SimpleQueryResolver implements QueryResolver {
	private StopWordsDiscriminator stopWordsDiscriminator;
	private Comparator<Tuple<Integer, List<KeyCount<OffsetAddress>>>> comparator;
	
	/**
	 * Construye una instancia.
	 */
	public SimpleQueryResolver(StopWordsDiscriminator stopWordsDiscriminator) {
		this.stopWordsDiscriminator = stopWordsDiscriminator;
		this.comparator = new Tuple.FirstComparator<Tuple<Integer, List<KeyCount<OffsetAddress>>>, Integer>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.wordservice.search.QueryResolver#getQueryTerms(ar.com.datos.documentlibrary.Document)
	 */
	@Override
	public Set<String> getQueryTerms(Document query) {
		Set<String> terms = new HashSet<String>();
		
		// inicializo el parser con este documento
		Parser parser = new Parser(query);
					
		// recorro las frases (ya normalizadas) del documento 
		Iterator<List<String>> itPhrases = parser.iterator();
		List<String> phraseTerms;
		Iterator<String> itTerms;
		while (itPhrases.hasNext()) {
			phraseTerms = itPhrases.next();
			// Saco las stopwords.
			phraseTerms = this.stopWordsDiscriminator.processPhrase(phraseTerms);
			itTerms = phraseTerms.iterator();
			while (itTerms.hasNext()) {
				terms.add(itTerms.next());
			}
		}
		
		return terms;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.wordservice.search.QueryResolver#resolveQuery(java.util.Map, ar.com.datos.documentlibrary.Document, int)
	 */
	@Override
	public List<Tuple<Double, OffsetAddress>> resolveQuery(Map<String, Tuple<Integer, List<KeyCount<OffsetAddress>>>> termsData, Document query, int maxResults) {
		List<Tuple<Double, OffsetAddress>> returnValue = new LinkedList<Tuple<Double,OffsetAddress>>();
		
		// En esta implementación la query no me interesa usarla, porque todos los términos deben ser buscados
		// sin necesidad de operaciones de conjuntos, etc. Así que para saber cuales son los términos uso directamente
		// termData (puesto que por contrato contiene todos los términos devueltos por getQueryTerms()).
		List<Tuple<Integer, List<KeyCount<OffsetAddress>>>> termsByNumberOfDocument = new LinkedList<Tuple<Integer,List<KeyCount<OffsetAddress>>>>(termsData.values());
		
		// Ordeno por peso global.
		// Lo ordeno de menor a mayor y no necesito calcular el peso global ! (Puesto que
		// PesoGlobal = log(CantDocsTotal/CantDocsEnQueAparece), y cuanto menor sea
		// CantDocsEnQueAparece, mayor será el peso global.
		Collections.sort(termsByNumberOfDocument, this.comparator);
		
		Iterator<Tuple<Integer, List<KeyCount<OffsetAddress>>>> itTerms = termsByNumberOfDocument.iterator();
		Iterator<KeyCount<OffsetAddress>> itDocuments;
		OffsetAddress address;
		double similarity;
		while (itTerms.hasNext()) {
			// Aca recién tiene que traerse el listado (lazy!)
			itDocuments = itTerms.next().getSecond().iterator();
			while (itDocuments.hasNext()) {
				address = itDocuments.next().getKey();
				
				// TODO: acá tendría que calcular la similaridad.
				similarity = 0D;
				
				returnValue.add(new Tuple<Double, OffsetAddress>(similarity, address));
			}
		}
		
		return returnValue;
	}
}
