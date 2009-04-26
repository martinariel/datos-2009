package ar.com.datos.indexer;


import ar.com.datos.wordservice.SessionHandler;

public interface SessionIndexer<T> extends SessionHandler, Indexer<T> {

}