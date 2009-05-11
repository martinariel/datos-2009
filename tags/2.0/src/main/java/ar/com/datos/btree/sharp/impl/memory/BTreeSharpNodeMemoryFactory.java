package ar.com.datos.btree.sharp.impl.memory;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory;
import ar.com.datos.btree.sharp.impl.memory.node.EspecialRootNodeMemory;
import ar.com.datos.btree.sharp.impl.memory.node.InternalNodeMemory;
import ar.com.datos.btree.sharp.impl.memory.node.LeafNodeMemory;
import ar.com.datos.btree.sharp.impl.memory.node.RootNodeMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Implementación de {@link BTreeSharpNodeFactory} para un {@link BTreeSharp} en
 * memoria.
 *
 * @author fvalido
 */
public class BTreeSharpNodeMemoryFactory<E extends Element<K>, K extends Key> implements BTreeSharpNodeFactory<E, K> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createInternalNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration)
	 */
	@Override
	public final AbstractInternalNode<E, K> createInternalNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationMemory)) {
			throw new BTreeConfException();
		}

		return new InternalNodeMemory<E, K>((BTreeSharpConfigurationMemory<E, K>)bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createLeafNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration, ar.com.datos.btree.sharp.node.NodeReference)
	 */
	@Override
	public final AbstractLeafNode<E, K> createLeafNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationMemory)) {
			throw new BTreeConfException();
		}

		return new LeafNodeMemory<E, K>((BTreeSharpConfigurationMemory<E, K>)bTreeSharpConfiguration, previous, next);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createDefinitiveRootNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration)
	 */
	@Override
	public AbstractRootNode<E, K> createDefinitiveRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationMemory)) {
			throw new BTreeConfException();
		}
		
		return new RootNodeMemory<E, K>((BTreeSharpConfigurationMemory<E, K>)bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createEspecialRootNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration, ar.com.datos.btree.sharp.BTreeSharp)
	 */
	@Override
	public AbstractEspecialRootNode<E, K> createEspecialRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationMemory)) {
			throw new BTreeConfException();
		}
		
		return new EspecialRootNodeMemory<E, K>((BTreeSharpConfigurationMemory<E, K>)bTreeSharpConfiguration, btree);
	}
}
