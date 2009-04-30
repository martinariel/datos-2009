package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.util.WrappedParam;

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
	protected long calculateNodeSize() {
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
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.util.WrappedParam, ar.com.datos.util.WrappedParam)
	 */
	@Override
	protected void getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode, AbstractInternalNode<E, K> leftNode, AbstractInternalNode<E, K> centerNode, AbstractInternalNode<E, K> rightNode, WrappedParam<K> overflowKeyCenter, WrappedParam<K> overflowKeyRight) {
		// Creo una lista de NodeReferences y otra de Keys
		List<NodeReference<E, K>> sourceNodeReferences = new LinkedList<NodeReference<E, K>>();
		List<K> sourceKeys = new LinkedList<K>();
		
		sourceNodeReferences.add(this.firstChild); // Agrego la primer referencia de este nodo.
		Iterator<KeyNodeReference<E, K>> keyNodeReferenceIt;
		KeyNodeReference<E, K> currentKeyNodeReference;
		
		// Agrego claves de este nodo y resto de las referencias.
		keyNodeReferenceIt = this.keysNodes.iterator();
		while (keyNodeReferenceIt.hasNext()) {
			currentKeyNodeReference = keyNodeReferenceIt.next();
			sourceNodeReferences.add(currentKeyNodeReference.getNodeReference());
			sourceKeys.add(currentKeyNodeReference.getKey());
		}
		
		// Divido la lista de Keys.
		List<List<K>> keyParts = ThirdPartHelper.divideInThreePartsEspecial(sourceKeys);
		
		// Recombino las listas divididas de keys con las KeyNodeReferences armando los nodos.
		ThirdPartHelper.combineKeysAndNodeReferences(sourceNodeReferences, keyParts, leftNode, centerNode, rightNode, overflowKeyCenter, overflowKeyRight);
	}
}
