package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;

/**
 * Agrupación de una clave y una referencia a un nodo.
 *
 * @author fvalido
 */
public class KeyNodeReference<E extends Element<K>, K extends Key> {
	/** Clave de este {@link KeyNodeReference} */
	private K key;

	/** Referencia a un {@link Node} de este {@link KeyNodeReference} */
	private NodeReference<E, K> nodeReference;

	/**
	 * Permite construir un {@link KeyNodeReference}.
	 */
	public KeyNodeReference(K key, NodeReference<E, K> nodeReference) {
		this.key = key;
		this.nodeReference = nodeReference;
	}

	/**
	 * Permite obtener la clave de este {@link KeyNodeReference}.
	 */
	public final K getKey() {
		return this.key;
	}

	/**
	 * Permite obtener la referencia a un {@link Node} de este {@link KeyNodeReference}
	 */
	public final NodeReference<E, K> getNodeReference() {
		return this.nodeReference;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.key.toString() + "||" + this.nodeReference.toString();
	}
}
