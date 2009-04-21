package ar.com.datos.btree.sharp.impl.memory;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;

/**
 * Implementación en memoria de un {@link BTreeSharpConfiguration}
 *
 * @author fvalido
 */
public class BTreeSharpConfigurationMemory<E extends Element<K>, K extends Key> extends BTreeSharpConfiguration<E, K> {
	/**
	 * Permite crear un {@link BTreeSharpConfigurationMemory}
	 *
	 * @param maxCapacityInternalNode
	 * Máxima capacidad para un nodo interno.
	 * @param maxCapacityLeafNode
	 * Máxima capacidad para un nodo hoja.
	 */
	public BTreeSharpConfigurationMemory(short maxCapacityInternalNode, short maxCapacityLeafNode) throws BTreeConfException {
			super(maxCapacityInternalNode, maxCapacityLeafNode, (short)(2 * maxCapacityInternalNode), new BTreeSharpNodeMemoryFactory<E, K>());
	}
}
