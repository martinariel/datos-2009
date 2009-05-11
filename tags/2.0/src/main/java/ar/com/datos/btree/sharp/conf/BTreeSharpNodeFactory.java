package ar.com.datos.btree.sharp.conf;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.memory.node.LeafNodeMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Permite crear las implementaciones de {@link AbstractLeafNode}, {@link AbstractInternalNode},
 * etc. de tal manera que haya una correspondencia entre ellas; por ejemplo un {@link LeafNodeMemory}
 * solo debe ser usado en conjunto con un {@link AbstractInternalNode}
 *
 * (Patrón de Diseño AbstractFactory)
 *
 * @author fvalido
 */
public interface BTreeSharpNodeFactory<E extends Element<K>, K extends Key> {
	/**
	 * Permite crear una implementación en particular de {@link AbstractLeafNode}
	 *
	 * @see AbstractLeafNode#AbstractLeafNode(BTreeSharpConfiguration, NodeReference, NodeReference)
	 */
	public AbstractLeafNode<E, K> createLeafNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, 
												 NodeReference<E, K> previous,
												 NodeReference<E, K> next);

	/**
	 * Permite crear una implementación en particular de {@link AbstractInternalNode}
	 *
	 * @see AbstractInternalNode#AbstractInternalNode(BTreeSharpConfiguration)
	 */
	public AbstractInternalNode<E, K> createInternalNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration);
	
	/**
	 * Permite crear una implementación en particular de {@link AbstractRootNode}
	 *  
	 * @see AbstractRootNode#AbstractRootNode(BTreeSharpConfiguration)
	 */
	public AbstractRootNode<E, K> createDefinitiveRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration);

	/**
	 * Permite crear una implementación en particular de {@link AbstractEspecialRootNode}
	 *  
	 * @see AbstractEspecialRootNode#AbstractEspecialRootNode(BTreeSharpConfiguration, BTreeSharp)
	 */
	public AbstractEspecialRootNode<E, K> createEspecialRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree);
}
