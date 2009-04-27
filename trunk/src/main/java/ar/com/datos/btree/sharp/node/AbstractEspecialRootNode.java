package ar.com.datos.btree.sharp.node;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo raiz inicial genérico. Debe ser extendido para que sea usable.
 * El nodo raiz inicial funciona distinto del nodo raiz normal.
 * Este nodo tiene vida hasta que se produce el primer split.
 * Contiene en si mismo a los elementos correspondientes a cada una de las
 * claves que contiene (tal como si fuera una hoja), pero simula ser un nodo 
 * raiz común. 
 *
 * @author fvalido
 */
public abstract class AbstractEspecialRootNode<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> {
	/** Referencia al árbol b# que contiene esta raiz. */
	private BTreeSharp<E, K> btree;
	
	/**
	 * Permite crear un nodo raiz inicial.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 * 
	 * @param btree
	 * Árbol que contiene a esta raiz.
	 */
	public AbstractEspecialRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		super(bTreeSharpConfiguration, null, null);
		this.btree = btree;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getNodeType()
	 */
	@Override
	public NodeType getNodeType() {
		return NodeType.ESPECIALROOT;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#overflow(ar.com.datos.btree.sharp.node.AbstractLeafNode, boolean, ar.com.datos.util.WrappedParam)
	 */
	@Override
	protected KeyNodeReference<E, K> overflow(AbstractLeafNode<E, K> brother, boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Aca no hay hermano, ni clave del padre... Solo debo crear 3 hijos, poner
		// 1/3 del nodo (2/3 de un nodo normal) en cada uno de esos hijos y reescribir
		// esta raiz.
	
		// Trabajo a los nodos como left, center y right.
		AbstractLeafNode<E, K> left = this.bTreeSharpConfiguration.getBTreeSharpFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);
		AbstractLeafNode<E, K> right = this.bTreeSharpConfiguration.getBTreeSharpFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);
		AbstractLeafNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);

		// Extraigo los tercios 
		List<List<E>> listParts = getParts(null); // Método template.
		List<E> leftPart = listParts.get(0);
		List<E> centerPart = listParts.get(1);
		List<E> rightPart = listParts.get(2);
		
		// Los meto en los nodos. 
		left.elements.addAll(leftPart);
		center.elements.addAll(centerPart);
		right.elements.addAll(rightPart);
		
		// Método template
		left.postAddElement();
		center.postAddElement();
		right.postAddElement();

		// Apunto los ChainedNodes
		left.next = center.myNodeReference;
		center.previous = left.myNodeReference;
		center.next = right.myNodeReference;
		right.previous = center.myNodeReference;
		
		// Creo una raiz definitiva para que apunte a los nuevos nodos.
		AbstractRootNode<E, K> definitiveRootNode = this.bTreeSharpConfiguration.getBTreeSharpFactory().createDefinitiveRootNode(this.bTreeSharpConfiguration);
		definitiveRootNode.firstChild = left.myNodeReference;
		definitiveRootNode.keysNodes.add(new KeyNodeReference<E, K>(center.elements.get(0).getKey(), center.myNodeReference));
		definitiveRootNode.keysNodes.add(new KeyNodeReference<E, K>(right.elements.get(0).getKey(), right.myNodeReference));
		
		// Método template.
		definitiveRootNode.myNodeReference = this.myNodeReference.getSameNodeReference(definitiveRootNode, NodeType.ROOT);
		definitiveRootNode.postAddElement();
		
		// Establezco el definitiveRootNode en el arbol.
		this.btree.setRootNode(definitiveRootNode);
		
		return null;
	}
	
// FIXME: Esto no va más
//	/**
//	 * Permite dividir el nodo en 3 partes.
//	 * 
//	 * Es indistinto el estado en que se deja el nodo original (este).
//	 * 
//	 * Patrón de diseño Template.
//	 */
//	protected abstract List<List<E>> getParts(); 
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getNodeMaxCapacity()
	 */
	@Override
	protected int getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityRootNode();
	}

//  FIXME: Esto no va más
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected final List<E> getThirdPart(boolean left) {
//		// No se usa.
//		return null;
//	}
}
