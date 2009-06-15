package ar.com.datos.indexer;

import java.util.List;

import ar.com.datos.utils.sort.external.KeyCount;

public interface IndexedTerm<T> {
	/**
	 * Devuelve el string que representa al término indexado
	 */
	public String getTerm();

	/**
	 * Devuelve la cantidad de datos asociados a este término
	 */
	public Integer getNumberOfAssociatedData();

	/**
	 * Devuelve todos los datos asociados al este término con la cantidad de asociaciones existentes
	 */
	public List<KeyCount<T>> getAssociatedData();

}
