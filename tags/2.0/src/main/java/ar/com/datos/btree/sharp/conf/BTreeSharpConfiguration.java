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
	/** M�xima capacidad de un nodo. */
	private int maxCapacityNode;
	
	/** M�xima capacidad de un nodo raiz */
	private int maxCapacityRootNode;
	
	/** Abstract factory con las implementaciones a usar. */
	private BTreeSharpNodeFactory<E, K> bTreeSharpNodeFactory;

	/**
	 * Permite crear un {@link BTreeSharpConfiguration}
	 *
	 * @param maxCapacityNode
	 * M�xima capacidad para un nodo.
	 * @param maxCapacityRootNode
	 * M�xima capacidad para un nodo raiz (Normalmente ser� 2 * maxCapacityNode)
	 * @param bTreeSharpNodeFactory
	 * Factory correspondiente a la implementaci�n del �rbol B# que se est� usando.
	 */
	public BTreeSharpConfiguration(int maxCapacityNode, int maxCapacityRootNode, BTreeSharpNodeFactory<E, K> bTreeSharpNodeFactory) {
		this.maxCapacityNode = maxCapacityNode;
		this.maxCapacityRootNode = maxCapacityRootNode;
		this.bTreeSharpNodeFactory = bTreeSharpNodeFactory;
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
		this.bTreeSharpNodeFactory = bTreeSharpFactory;
	}
	
	/**
	 * Permite obtener la m�xima capacidad de un nodo.
	 */
	public int getMaxCapacityNode() {
		return this.maxCapacityNode;
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
	public BTreeSharpNodeFactory<E, K> getBTreeSharpNodeFactory() {
		return this.bTreeSharpNodeFactory;
	}

	/**
	 * Permite establecer la m�xima capacidad de un nodo interno.
	 */
	public void setMaxCapacityNode(int maxCapacityNode) {
		this.maxCapacityNode = maxCapacityNode;
	}

	/**
	 * Permite establecer la m�xima capacidad de un nodo raiz.
	 */
	public void setMaxCapacityRootNode(int maxCapacityRootNode) {
		this.maxCapacityRootNode = maxCapacityRootNode;
	}
}
