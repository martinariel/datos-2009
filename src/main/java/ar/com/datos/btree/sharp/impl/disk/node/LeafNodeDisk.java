package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Nodo hoja en disco.
 * 
 * @author fvalido
 */
public class LeafNodeDisk<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del árbol (que incluirán la configuración del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un nodo hoja en disco.
	 * Nota: El nodo no se grabará en disco inmediatamente cuando se crea. El
	 *       grabado se hará cuando se llame por primera vez a {@link #postAddElement()}.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 * 
	 * @param next
	 * Nodo posterior a este. En caso de ser el último debe ser null.
	 */
	public LeafNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		super(bTreeSharpConfiguration, previous, next);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo hoja en disco.
	 * Nota: El nodo ya existe previamente en disco.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 *
	 * @param next
	 * Nodo posterior a este. En caso de ser el último puede ser null.
	 *
	 * @param elements
	 * Elementos precontenidos en este nodo.
	 */
	public LeafNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous,
						NodeReference<E, K> next, List<E> elements) {
		super(bTreeSharpConfiguration, previous, next);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		
		this.elements = elements;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected long calculateNodeSize() {
		return this.bTreeSharpConfiguration.getLeafNodeSerializer().getDehydrateSize(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		if (this.myNodeReference == null) {
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getLeafNodesFileManager(), NodeType.LEAF);
		}
		NodeReferenceDisk<E, K> myNodeReference = (NodeReferenceDisk<E, K>)this.myNodeReference;
		myNodeReference.saveNode(this);
	}
	
	@Override
	protected List<E> getThirdPart(boolean left) {
		// TODO
		// Necesito el serializador de colección de elements pues lo demás es fijo.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.node.DiskNode#setNodeReference(ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk)
	 */
	@Override
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference) {
		this.myNodeReference = nodeReference;
	}
	
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getNextNodeReference() {
		return (NodeReferenceDisk<E, K>)this.next;
	}
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getPreviousNodeReference() {
		return (NodeReferenceDisk<E, K>)this.previous;
	}
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public List<E> getElements() {
		return this.elements;
	}
}
