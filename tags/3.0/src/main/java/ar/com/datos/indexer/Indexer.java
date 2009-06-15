package ar.com.datos.indexer;

/**
 * Interfaz que deben cumplir los indexadores de términos.
 * Estos son los encargados de relacionar un objeto de tipo T contra
 * una lista de términos. Luego se pueden recuperar, dado un término la lista
 * de objetos junto con la cantidad de veces que está relacionado ese objeto
 * con el término
 * @author jbarreneche
 *
 * @param <T>
 */
public interface Indexer<T> {

	/**
	 * Relaciona los términos recibidos por parámetro al objeto T
	 */
	public void addTerms(T dato, String... terms);

	/**
	 * Recupera, para el término <code>term</code>, la lista completa de objetos que
	 * lo tienen relacionado junto con la cantidad de veces que existe la relación entre
	 * dicho objeto y el término.
	 */
	public IndexedTerm<T> findTerm(String term);

	/**
	 * @return cantidad de terminos diferentes que se ingresaron al indexador
	 */
	public Long getNumberOfIndexedTerms();
}