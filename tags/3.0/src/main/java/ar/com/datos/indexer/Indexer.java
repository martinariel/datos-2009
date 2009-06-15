package ar.com.datos.indexer;

/**
 * Interfaz que deben cumplir los indexadores de t�rminos.
 * Estos son los encargados de relacionar un objeto de tipo T contra
 * una lista de t�rminos. Luego se pueden recuperar, dado un t�rmino la lista
 * de objetos junto con la cantidad de veces que est� relacionado ese objeto
 * con el t�rmino
 * @author jbarreneche
 *
 * @param <T>
 */
public interface Indexer<T> {

	/**
	 * Relaciona los t�rminos recibidos por par�metro al objeto T
	 */
	public void addTerms(T dato, String... terms);

	/**
	 * Recupera, para el t�rmino <code>term</code>, la lista completa de objetos que
	 * lo tienen relacionado junto con la cantidad de veces que existe la relaci�n entre
	 * dicho objeto y el t�rmino.
	 */
	public IndexedTerm<T> findTerm(String term);

	/**
	 * @return cantidad de terminos diferentes que se ingresaron al indexador
	 */
	public Long getNumberOfIndexedTerms();
}