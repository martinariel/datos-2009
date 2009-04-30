package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;

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
	 * 
	 * @param btree
	 * Árbol que contiene a esta raiz.
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
	protected long calculateNodeSize() {
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
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getParts(java.util.List, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode)
	 */
	@Override
	protected void getParts(List<E> rightNodeElements, AbstractLeafNode<E, K> leftNode, AbstractLeafNode<E, K> centerNode, AbstractLeafNode<E, K> rightNode) {
		// Extraigo las listas separadas.
		List<List<E>> parts = ThirdPartHelper.divideInThreeParts(this.elements);
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
