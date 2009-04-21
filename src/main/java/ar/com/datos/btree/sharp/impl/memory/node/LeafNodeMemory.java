package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Nodo hoja en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class LeafNodeMemory<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> {
	/**
	 * Permite construir un nodo hoja en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuracion del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 */
	public LeafNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		super(bTreeSharpConfiguration, previous, next);

		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() {
		// Nada para hacer
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
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getThirdPart(boolean)
	 */
	@Override
	protected List<E> getThirdPart(boolean left) {
		List<E> thirdPart = new LinkedList<E>();
		
		int cantRemove = this.elements.size() / 3;
		int removePosition = (left) ? 0 : cantRemove * 3;
		int elementsInitialSize = this.elements.size();
		for (int i = cantRemove * 3; i < elementsInitialSize; i++) {
			thirdPart.add(this.elements.remove(removePosition));
		}

		return thirdPart;
	}
}
