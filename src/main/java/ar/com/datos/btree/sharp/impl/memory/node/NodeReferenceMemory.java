package ar.com.datos.btree.sharp.impl.memory.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Implementaci�n en Memoria de {@link NodeReference}. No existe persistencia
 * de esta implementaci�n.
 *
 * @author fvalido
 */
public class NodeReferenceMemory<E extends Element<K>, K extends Key> implements NodeReference<E, K> {
	/** Nodo al que apunta este {@link NodeReference} */
	private Node<E, K> node;

	/** 
	 * Permite crear un {@link NodeReferenceMemory}
	 *
	 * @param node
	 * Nodo al que apuntar� la referencia.
	 */
	public NodeReferenceMemory(Node<E, K> node) {
		this.node = node;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.NodeReference#getNode()
	 */
	@Override
	public final Node<E, K> getNode() {
		return this.node;
	}

	@Override
	public String toString() {
		return "*" + this.node.toString() + "*";
	}
}
