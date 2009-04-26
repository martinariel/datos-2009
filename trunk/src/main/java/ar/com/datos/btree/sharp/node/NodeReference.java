package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;

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
	
	/**
	 * Obtiene una {@link NodeReference} igual a esta (por ejemplo si está en disco,
	 * apuntará al mismo lugar) pero reemplazando el node por el pasado.
	 */
	public NodeReference<E, K> getSameNodeReference(Node<E, K> node, NodeType nodeType);
}
