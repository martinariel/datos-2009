package ar.com.datos.btree.sharp.impl.memory;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.exception.BTreeException;
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
	 * @param maxCapacityNode
	 * Máxima capacidad para un nodo.
	 */
	public BTreeSharpConfigurationMemory(int maxCapacityNode) throws BTreeConfException {
			super(maxCapacityNode, 2 * maxCapacityNode, new BTreeSharpNodeMemoryFactory<E, K>());
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration#closeTree()
	 */
	@Override
	public void closeTree() throws BTreeException {
		// No hace nada.
	}
}
