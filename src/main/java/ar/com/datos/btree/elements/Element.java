package ar.com.datos.btree.elements;

/**
 * Elemento a guardar por las hojas.
 * Contiene una {@link Key} que puede ser obtenida.
 *
 * @author fvalido
 */
public interface Element<K extends Key> {
	/**
	 * Permite obtener la Key correspondiente a este elemento.
	 */
	public K getKey();

	/**
	 * Permite actualizar este elemento con el elemento pasado.
	 * PRE: La {@link Key} del elemento pasado debe ser igual a la {@link Key} de este elemento.
	 *
	 * @return
	 * true si el elemento sufrió una modificación que pudo haber modificado
	 * al nodo; false en caso contrario.
	 * Esto queda a criterio del implementador.
	 */
	public boolean updateElement(Element<K> element);
}
