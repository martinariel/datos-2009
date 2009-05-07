package ar.com.datos.wordservice.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ar.com.datos.documentlibrary.Document;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.IndexedTerm;
import ar.com.datos.parser.Parser;
import ar.com.datos.util.MapEntryComparator;
import ar.com.datos.util.Tuple;
import ar.com.datos.util.UpsideDownComparator;
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
	private Comparator<Tuple<Double, IndexedTerm<OffsetAddress>>> comparator1;
	private Comparator<Map.Entry<OffsetAddress, Double>> comparator2;
	
	/**
	 * Construye una instancia.
	 */
	public SimpleQueryResolver(StopWordsDiscriminator stopWordsDiscriminator) {
		this.stopWordsDiscriminator = stopWordsDiscriminator;
		this.comparator1 = new Tuple.FirstComparator<Tuple<Double, IndexedTerm<OffsetAddress>>, Double>();
		this.comparator2 = new UpsideDownComparator<Map.Entry<OffsetAddress,Double>>(new MapEntryComparator.ByValue<Map.Entry<OffsetAddress,Double>>());
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
	 * @see ar.com.datos.wordservice.search.QueryResolver#resolveQuery(java.util.Map, long, ar.com.datos.documentlibrary.Document, int)
	 */
	public List<Tuple<Double, OffsetAddress>> resolveQuery(Map<String, IndexedTerm<OffsetAddress>> termsData, long documentCount, Document query, int maxResults) {
		// En esta implementación la query no me interesa usarla, porque todos los términos deben ser buscados
		// sin necesidad de operaciones de conjuntos, etc. Así que para saber cuales son los términos uso directamente
		// termData (puesto que por contrato contiene todos los términos devueltos por getQueryTerms()).
		List<IndexedTerm<OffsetAddress>> terms = new LinkedList<IndexedTerm<OffsetAddress>>(termsData.values());
		
		// Obtengo el peso global de cada término
		double globalWeight;
		List<Tuple<Double, IndexedTerm<OffsetAddress>>> globalWeightTerm = new LinkedList<Tuple<Double,IndexedTerm<OffsetAddress>>>();
		Iterator<IndexedTerm<OffsetAddress>> itTerm = terms.iterator();
		IndexedTerm<OffsetAddress> term;
		while (itTerm.hasNext()) {
			term = itTerm.next();
			globalWeight = Math.log10(documentCount / term.getNumberOfAssociatedData());
			globalWeightTerm.add(new Tuple<Double, IndexedTerm<OffsetAddress>>(globalWeight, term));
		}
		
		// Lo ordeno por peso global.
		Collections.sort(globalWeightTerm, this.comparator1);
		
		// Creo un hashMap donde ire guardando la similitud con cada uno de los documentos.
		HashMap<OffsetAddress, Double> offsetSimilarity = new HashMap<OffsetAddress, Double>();
		double similarityPortion;
		Double similarity;
		Tuple<Double, IndexedTerm<OffsetAddress>> currentGlobalWeightTerm;
		Iterator<Tuple<Double, IndexedTerm<OffsetAddress>>> itGlobalWeightTerm = globalWeightTerm.iterator();
		Iterator<KeyCount<OffsetAddress>> itKeyCountOffset;
		KeyCount<OffsetAddress> keyCountOffset;
		while (itGlobalWeightTerm.hasNext()) {
			currentGlobalWeightTerm = itGlobalWeightTerm.next();
			// Sim(d,c) ~= Sumatoria(peso(t,d) * pesoGlobal) = Sumatoria(cantOcurrencias * pesoGlobal^2)
			// con t perteneciente a c. Se ignora la normalización del peso.
			globalWeight = currentGlobalWeightTerm.getFirst();
			// Para cada elemento de la lista de documentos...
			itKeyCountOffset = currentGlobalWeightTerm.getSecond().getAssociatedData().iterator();
			while (itKeyCountOffset.hasNext()) {
				keyCountOffset = itKeyCountOffset.next();
				similarityPortion = keyCountOffset.getCount() * globalWeight * globalWeight;
				// Agrego al mapa el par similitud-offset
				similarity = offsetSimilarity.get(keyCountOffset.getKey());
				similarity = (similarity == null) ? similarityPortion : similarity + similarityPortion;
				offsetSimilarity.put(keyCountOffset.getKey(), similarity);
			}
		}
		
		// Tengo el par similitud-offset para todos los documentos. Extraigo los maxResults mejores
		// y devuelvo eso.
		List<Tuple<Double, OffsetAddress>> returnValue = new LinkedList<Tuple<Double,OffsetAddress>>();
		List<Map.Entry<OffsetAddress, Double>> offsetSimilarityList = new LinkedList<Map.Entry<OffsetAddress, Double>>(offsetSimilarity.entrySet());
		Collections.sort(offsetSimilarityList, this.comparator2); // la ordeno
		Map.Entry<OffsetAddress, Double> offsetSimilarityEntry;
		Iterator<Map.Entry<OffsetAddress, Double>> itOffsetSimilarity = offsetSimilarityList.iterator();
		int count = 0;
		while (itOffsetSimilarity.hasNext() && count < maxResults) {
			offsetSimilarityEntry = itOffsetSimilarity.next();
			returnValue.add(new Tuple<Double, OffsetAddress>(offsetSimilarityEntry.getValue(), offsetSimilarityEntry.getKey()));
		}
		
		return returnValue;
	}
}
