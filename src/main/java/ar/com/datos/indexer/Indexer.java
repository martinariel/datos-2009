package ar.com.datos.indexer;

import java.util.Collection;

import ar.com.datos.indexer.keywordIndexer.KeyCount;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.wordservice.SessionHandler;

public class Indexer<T> implements SessionHandler {

	public Indexer(String fileName, Serializer<T> serializadorDocumento) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void endSession() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startSession() {
		// TODO Auto-generated method stub

	}

	public void addTerms(T dato, String...terminos) {
		// TODO Auto-generated method stub
		
	}

	public Collection<KeyCount<T>> findTerm(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
