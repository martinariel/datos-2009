package ar.com.datos.btree.sharp.conf;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;

/**
 * Configuraciones necesarias para un {@link BTreeSharp}
 * Adem�s: m�todos relacionados con la administraci�n de un BTree.
 * Debe ser implementado por cada Implementaci�n (Memoria, Disco, "etc.").
 * 
 * @author fvalido
 */
public abstract class BTreeSharpConfiguration<E extends Element<K>, K extends Key> {
	/** M�xima capacidad de un nodo interno. */
	private int maxCapacityInternalNode;
	
	/** M�xima capacidad de un nodo hoja. */
	private int maxCapacityLeafNode;
	
	/** M�xima capacidad de un nodo raiz */
	private int maxCapacityRootNode;
	
	/** Abstract factory con las implementaciones a usar. */
	private BTreeSharpNodeFactory<E, K> bTreeSharpFactory;

	/**
	 * Permite crear un {@link BTreeSharpConfiguration}
	 *
	 * @param maxCapacityInternalNode
	 * M�xima capacidad para un nodo interno.
	 * @param maxCapacityLeafNode
	 * M�xima capacidad para un nodo hoja.
	 * @param maxCapacityRootNode.
	 * M�xima capacidad para un nodo raiz (Normalmente ser� 2 * maxCapacityInternalNode)
	 * @param bTreeSharpFactory
	 * Factory correspondiente a la implementaci�n del �rbol B# que se est� usando.
	 */
	public BTreeSharpConfiguration(int maxCapacityInternalNode, int maxCapacityLeafNode, int maxCapacityRootNode, BTreeSharpNodeFactory<E, K> bTreeSharpFactory) {
		this.maxCapacityInternalNode = maxCapacityInternalNode;
		this.maxCapacityLeafNode = maxCapacityLeafNode;
		this.maxCapacityRootNode = maxCapacityRootNode;
		this.bTreeSharpFactory = bTreeSharpFactory;
	}

	/**
	 * Especifica que el �rbol no ser� usado m�s por ahora.
	 */
	public abstract void closeTree() throws BTreeException;
	
	/**
	 * Constructor.
	 * Requiere que se llame a cada uno de los #set... por separado antes de ser usado.
	 */
	public BTreeSharpConfiguration(BTreeSharpNodeFactory<E, K> bTreeSharpFactory) {
		this.bTreeSharpFactory = bTreeSharpFactory;
	}
	
	/**
	 * Permite obtener la m�xima capacidad de un nodo interno.
	 */
	public int getMaxCapacityInternalNode() {
		return this.maxCapacityInternalNode;
	}
	/**
	 * Permite obtener la m�xima capacidad de un nodo hoja.
	 */
	public int getMaxCapacityLeafNode() {
		return this.maxCapacityLeafNode;
	}

	/**
	 * Permite obtener la m�xima capacidad de un nodo raiz.
	 */
	public int getMaxCapacityRootNode() {
		return this.maxCapacityRootNode;
	}
	
	/**
	 * Permite obtener el factory correspondiente a la implementaci�n del
	 * �rbol B# que se est� usando.
	 */
	public BTreeSharpNodeFactory<E, K> getBTreeSharpFactory() {
		return this.bTreeSharpFactory;
	}

	/**
	 * Permite establecer la m�xima capacidad de un nodo interno.
	 */
	public void setMaxCapacityInternalNode(int maxCapacityInternalNode) {
		this.maxCapacityInternalNode = maxCapacityInternalNode;
	}

	/**
	 * Permite establecer la m�xima capacidad de un nodo hoja.
	 */
	public void setMaxCapacityLeafNode(int maxCapacityLeafNode) {
		this.maxCapacityLeafNode = maxCapacityLeafNode;
	}

	/**
	 * Permite establecer la m�xima capacidad de un nodo raiz.
	 */
	public void setMaxCapacityRootNode(int maxCapacityRootNode) {
		this.maxCapacityRootNode = maxCapacityRootNode;
	}
}
