package ar.com.datos.indexer;


import ar.com.datos.wordservice.SessionHandler;
/**
 * Interfaz para los indexadores que trabajan por sesiones.
 * @author jbarreneche
 *
 * @param <T>
 */
public interface SessionIndexer<T> extends SessionHandler, Indexer<T> {
	/**
	 * Mantiene la funcionalidad de {@link Indexer#addTerms(Object, String...)}
	 * Pero con el requerimiento de que se cumpla {@code SessionIndexer#isActive()}
	 */
	@Override
	public void addTerms(T dato, String...terms);
}