package ar.com.datos.btree.sharp.conf;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.BTreeSharp;

/**
 * Configuraciones necesarias para un {@link BTreeSharp}
 * Debe ser implementado por cada Implementación (Memoria, Disco, "etc.").
 * 
 * @author fvalido
 */
public abstract class BTreeSharpConfiguration<E extends Element<K>, K extends Key> {
	/** Máxima capacidad de un nodo interno. */
	private short maxCapacityInternalNode;
	
	/** Máxima capacidad de un nodo hoja. */
	private short maxCapacityLeafNode;
	
	/** Máxima capacidad de un nodo raiz */
	private short maxCapacityRootNode;
	
	/** Abstract factory con las implementaciones a usar. */
	private BTreeSharpNodeFactory<E, K> bTreeSharpFactory;

	/**
	 * Permite crear un {@link BTreeSharpConfiguration}
	 *
	 * @param maxCapacityInternalNode
	 * Máxima capacidad para un nodo interno.
	 * @param maxCapacityLeafNode
	 * Máxima capacidad para un nodo hoja.
	 * @param maxCapacityRootNode.
	 * Máxima capacidad para un nodo raiz (Normalmente será 2 * maxCapacityInternalNode)
	 * @param bTreeSharpFactory
	 * Factory correspondiente a la implementación del árbol B# que se esté usando.
	 */
	public BTreeSharpConfiguration(short maxCapacityInternalNode, short maxCapacityLeafNode, short maxCapacityRootNode, BTreeSharpNodeFactory<E, K> bTreeSharpFactory) {
		this.maxCapacityInternalNode = maxCapacityInternalNode;
		this.maxCapacityLeafNode = maxCapacityLeafNode;
		this.maxCapacityRootNode = maxCapacityRootNode;
		this.bTreeSharpFactory = bTreeSharpFactory;
	}

	/**
	 * Permite obtener la máxima capacidad de un nodo interno.
	 */
	public short getMaxCapacityInternalNode() {
		return maxCapacityInternalNode;
	}
	/**
	 * Permite obtener la máxima capacidad de un nodo hoja.
	 */
	public short getMaxCapacityLeafNode() {
		return maxCapacityLeafNode;
	}

	/**
	 * Permite obtener la máxima capacidad de un nodo raiz.
	 */
	public short getMaxCapacityRootNode() {
		return maxCapacityRootNode;
	}
	
	/**
	 * Permite obtener el factory correspondiente a la implementación del
	 * árbol B# que se esté usando.
	 */
	public BTreeSharpNodeFactory<E, K> getBTreeSharpFactory() {
		return bTreeSharpFactory;
	}
}
