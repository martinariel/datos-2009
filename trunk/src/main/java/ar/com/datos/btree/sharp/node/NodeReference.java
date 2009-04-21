package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;

/**
 * Referencia a un Nodo.
 *
 * @author fvalido
 */
public interface NodeReference<E extends Element<K>, K extends Key> {
	/**
	 * Permite obtener el nodo referenciado.
	 */
	public Node<E, K> getNode() throws BTreeException;
}
