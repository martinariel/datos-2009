package ar.com.datos.btree.sharp.impl.disk.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;

/**
 * Interface a ser implementada por los nodos en disco.
 *
 * @author fvalido
 */
public interface DiskNode<E extends Element<K>, K extends Key> {
	/**
	 * Permite establecer una referencia a este nodo.
	 */
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference);
}
