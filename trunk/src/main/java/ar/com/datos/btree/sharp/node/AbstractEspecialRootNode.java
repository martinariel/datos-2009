package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo raiz inicial gen�rico. Debe ser extendido para que sea usable.
 * El nodo raiz inicial funciona distinto del nodo raiz normal.
 * Este nodo tiene vida hasta que se produce el primer split.
 * Contiene en si mismo a los elementos correspondientes a cada una de las
 * claves que contiene (tal como si fuera una hoja), pero simula ser un nodo 
 * raiz com�n. 
 *
 * @author fvalido
 */
public abstract class AbstractEspecialRootNode<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> {
	/** Referencia al �rbol b# que contiene esta raiz. */
	private BTreeSharp<E, K> btree;
	
	/**
	 * Permite crear un nodo raiz inicial.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 * 
	 * @param btree
	 * �rbol que contiene a esta raiz.
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
		AbstractLeafNode<E, K> left = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);
		AbstractLeafNode<E, K> right = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);
		AbstractLeafNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createLeafNode(this.bTreeSharpConfiguration, null, null);

		// Extraigo los tercios 
		getParts(null, left, center, right); // M�todo template.
		
		// M�todo template (En este caso particular no me queda otra que llamarlo 2 veces para 2 de los nodos)
		left.postAddElement();
		center.postAddElement();

		left.next = center.myNodeReference;
		left.postAddElement(); // Acabo de modificar left.
		
		right.previous = center.myNodeReference;
		right.postAddElement();

		center.previous = left.myNodeReference;
		center.next = right.myNodeReference;
		center.postAddElement(); // Acabo de modificar center.
		
		// Creo una raiz definitiva para que apunte a los nuevos nodos.
		AbstractRootNode<E, K> definitiveRootNode = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createDefinitiveRootNode(this.bTreeSharpConfiguration);
		definitiveRootNode.firstChild = left.myNodeReference;
		definitiveRootNode.keysNodes.add(new KeyNodeReference<E, K>(center.elements.get(0).getKey(), center.myNodeReference));
		definitiveRootNode.keysNodes.add(new KeyNodeReference<E, K>(right.elements.get(0).getKey(), right.myNodeReference));
		
		// M�todo template.
		definitiveRootNode.myNodeReference = this.myNodeReference.getSameNodeReference(definitiveRootNode, NodeType.ROOT);
		definitiveRootNode.postAddElement();
		
		// Establezco el definitiveRootNode en el arbol.
		this.btree.setRootNode(definitiveRootNode);
		
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getNodeMaxCapacity()
	 */
	@Override
	protected int getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityRootNode();
	}
}
