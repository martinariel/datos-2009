package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;

/**
 * Nodo interno en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class InternalNodeMemory<E extends Element<K>, K extends Key> extends AbstractInternalNode<E, K> {
	/**
	 * Permite construir un nodo interno en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public InternalNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
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
		return this.keysNodes.size();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
	 */
	@Override
	protected List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
		List<KeyNodeReference<E, K>> thirdPart = new LinkedList<KeyNodeReference<E, K>>();
		
		int cantRemove = this.keysNodes.size() / 3;

		if (left) {
			thirdPart.add(new KeyNodeReference<E, K>(null, this.firstChild));
		}
		int keysNodesInitialSize = this.keysNodes.size();
		int removePosition = (left) ? 0 : cantRemove * 3;
		for (int i = cantRemove * 3; i < keysNodesInitialSize; i++) {
			thirdPart.add(this.keysNodes.remove(removePosition));
		}
		if (left) {
			KeyNodeReference<E, K> tempKeyNodeReference = this.keysNodes.remove(0);
			this.firstChild = tempKeyNodeReference.getNodeReference();
			thirdPart.add(new KeyNodeReference<E, K>(tempKeyNodeReference.getKey(), null));
		}
		
		return thirdPart;
	}
}
