package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;

/**
 * Nodo raiz especial en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class EspecialRootNodeMemory<E extends Element<K>, K extends Key> extends AbstractEspecialRootNode<E, K> {
	/**
	 * Permite construir un nodo raiz especial en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public EspecialRootNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		super(bTreeSharpConfiguration, btree);
		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected int calculateNodeSize() {
		return this.elements.size();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		// Nada para hacer.
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractEspecialRootNode#getParts()
	 */
	@Override
	protected List<List<E>> getParts() {
		int partSize = calculateNodeSize() / 3;
		
		List<E> part = new LinkedList<E>();;
		List<List<E>> returnValue = new LinkedList<List<E>>();
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<E>();
			for (int j = 0; j < partSize && this.elements.size() > 0; j++) {
				part.add(this.elements.remove(0));
			}
			returnValue.add(part);
		}
		
		return returnValue;
	}
}
