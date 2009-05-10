package ar.com.datos.indexer;

import java.util.List;

import ar.com.datos.utils.sort.external.KeyCount;

public interface IndexedTerm<T> {
	/**
	 * Devuelve el string que representa al t�rmino indexado
	 */
	public String getTerm();

	/**
	 * Devuelve la cantidad de datos asociados a este t�rmino
	 */
	public Integer getNumberOfAssociatedData();

	/**
	 * Devuelve todos los datos asociados al este t�rmino con la cantidad de asociaciones existentes
	 */
	public List<KeyCount<T>> getAssociatedData();

}
