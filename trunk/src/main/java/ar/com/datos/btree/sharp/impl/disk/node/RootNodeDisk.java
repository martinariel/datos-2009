package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.serializer.RootNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Nodo raiz en disco.
 *
 * @author fvalido
 */
public final class RootNodeDisk<E extends Element<K>, K extends Key> extends AbstractRootNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del �rbol (que incluir�n la configuraci�n del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un nodo raiz en disco.
	 * 
	 * Nota: El nodo no se grabar� en disco inmediatamente cuando se crea. El
	 *       grabado se har� cuando se llame por primera vez a postAddElement.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 */
	public RootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo raiz en disco.
	 * Nota: El nodo ya existe previamente en disco.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 *
	 * @param firstChild
	 * NodeReference al primer nodo hijo.
	 *
	 * @param keysNodes
	 * Claves y referencias a hijos.
	 */
	public RootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration,
							NodeReference<E, K> firstChild, List<KeyNodeReference<E, K>> keysNodes) {
		super(bTreeSharpConfiguration);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		
		this.firstChild = firstChild;
		this.keysNodes = keysNodes;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		if (this.myNodeReference == null) {
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getInternalNodesFileManager(), NodeType.ROOT);
		}
		NodeReferenceDisk<E, K> myNodeReference = (NodeReferenceDisk<E, K>)this.myNodeReference;
		myNodeReference.saveNode(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected long calculateNodeSize() {
		return this.bTreeSharpConfiguration.getStateInternalNodeSerializer().getDehydrateSize(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractRootNode#getParts()
	 */
	@Override
	protected List<List<KeyNodeReference<E, K>>> getParts() {
		// TODO
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
	 * Elemento visible para su uso desde {@link RootNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getFirstNodeReference() {
		return (NodeReferenceDisk<E, K>)this.firstChild;
	}

	/**
	 * Elemento visible para su uso desde {@link RootNodeSerializer}
	 */
	public List<KeyNodeReference<E, K>> getKeysNodeReferences() {
		return this.keysNodes;
	}
}