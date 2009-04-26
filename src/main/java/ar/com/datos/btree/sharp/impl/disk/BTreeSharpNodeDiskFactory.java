package ar.com.datos.btree.sharp.impl.disk;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeConfException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory;
import ar.com.datos.btree.sharp.impl.disk.node.EspecialRootNodeDisk;
import ar.com.datos.btree.sharp.impl.disk.node.InternalNodeDisk;
import ar.com.datos.btree.sharp.impl.disk.node.LeafNodeDisk;
import ar.com.datos.btree.sharp.impl.disk.node.RootNodeDisk;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Implementación de {@link BTreeSharpNodeFactory} para un {@link BTreeSharp} en
 * disco.
 *
 * @author fvalido
 */
public class BTreeSharpNodeDiskFactory<E extends Element<K>, K extends Key> implements BTreeSharpNodeFactory<E, K> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createInternalNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration)
	 */
	@Override
	public final AbstractInternalNode<E, K> createInternalNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationDisk)) {
			throw new BTreeConfException();
		}

		return new InternalNodeDisk<E, K>((BTreeSharpConfigurationDisk<E, K>)bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createLeafNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration, ar.com.datos.btree.sharp.node.NodeReference)
	 */
	@Override
	public final AbstractLeafNode<E, K> createLeafNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationDisk)) {
			throw new BTreeConfException();
		}

		return new LeafNodeDisk<E, K>((BTreeSharpConfigurationDisk<E, K>)bTreeSharpConfiguration, previous, next);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createDefinitiveRootNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration)
	 */
	@Override
	public AbstractRootNode<E, K> createDefinitiveRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationDisk)) {
			throw new BTreeConfException();
		}
		
		return new RootNodeDisk<E, K>((BTreeSharpConfigurationDisk<E, K>)bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.BTreeSharpNodeFactory#createEspecialRootNode(ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration, ar.com.datos.btree.sharp.BTreeSharp)
	 */
	@Override
	public AbstractEspecialRootNode<E, K> createEspecialRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		if (!(bTreeSharpConfiguration instanceof BTreeSharpConfigurationDisk)) {
			throw new BTreeConfException();
		}
		
		return new EspecialRootNodeDisk<E, K>((BTreeSharpConfigurationDisk<E, K>)bTreeSharpConfiguration, btree);
	}
}
