package ar.com.datos.indexer;

import java.util.Collection;

import ar.com.datos.indexer.keywordIndexer.KeyCount;

public interface Indexer<T> {

	public void addTerms(T dato, String... terms);

	public Collection<KeyCount<T>> findTerm(String string);

}