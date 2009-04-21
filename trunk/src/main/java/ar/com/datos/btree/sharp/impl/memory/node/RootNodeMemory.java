package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;

/**
 * Nodo raiz en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class RootNodeMemory<E extends Element<K>, K extends Key> extends AbstractRootNode<E, K> {
	/**
	 * Permite construir un nodo raiz en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public RootNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
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
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		// Nada para hacer.
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractRootNode#getParts()
	 */
	@Override
	protected List<List<KeyNodeReference<E, K>>> getParts() {
		int partSize = calculateNodeSize() / 3;
		
		List<KeyNodeReference<E, K>> part = new LinkedList<KeyNodeReference<E,K>>();;
		part.add(new KeyNodeReference<E, K>(null, this.firstChild));
		List<List<KeyNodeReference<E, K>>> returnValue = new LinkedList<List<KeyNodeReference<E,K>>>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < partSize && this.keysNodes.size() > 0; j++) {
				part.add(this.keysNodes.remove(0));
			}
			returnValue.add(part);
			part = new LinkedList<KeyNodeReference<E,K>>();
		}
		
		return returnValue;
	}
}
