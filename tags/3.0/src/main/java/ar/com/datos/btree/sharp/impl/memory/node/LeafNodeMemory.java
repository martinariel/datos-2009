package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;

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
	protected long calculateNodeSize() {
		return this.elements.size();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getParts(java.util.List, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode)
	 */
	@Override
	protected void getParts(List<E> rightNodeElements, AbstractLeafNode<E, K> leftNode, AbstractLeafNode<E, K> centerNode, AbstractLeafNode<E, K> rightNode) {
		// Uno las listas
		List<E> source = new LinkedList<E>(this.elements);
		source.addAll(rightNodeElements);
		
		// Extraigo las listas separadas.
		List<List<E>> parts = ThirdPartHelper.divideInThreeParts(source);
		List<E> left = parts.remove(0);
		List<E> center = parts.remove(0);
		List<E> right = parts.remove(0);
		
		// Configuro los nodos con las partes que obtuve.
		leftNode.getElements().clear();
		leftNode.getElements().addAll(left);
		centerNode.getElements().clear();
		centerNode.getElements().addAll(center);
		rightNode.getElements().clear();
		rightNode.getElements().addAll(right);
	}
}
