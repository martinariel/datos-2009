package ar.com.datos.btree.sharp.impl.disk.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;

/**
 * Implementación en Disco de {@link NodeReference}.
 *
 * @author fvalido
 */
public class NodeReferenceDisk<E extends Element<K>, K extends Key> implements NodeReference<E, K> {
	/** Dirección del nodo dentro del archivo de nodos correspondiente. */
	private BlockAddress<Long, Short> nodeAddress;
	/** VLFM para el archivo que guarda este nodo */
	private BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> nodeFileManager;
	/** Indica el tipo de nodo que refiere */
	private NodeType nodeType;
	
	/**
	 * Constructor.
	 *
	 * @param nodeAddress
	 * Dirección de nodo asociado.
	 * @param nodeFileManager
	 * VLFM para el archivo que guarda este nodo.
	 * @param nodeType
	 * Indica el tipo de nodo que refiere.
	 */
	public NodeReferenceDisk(BlockAddress<Long, Short> nodeAddress, BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> nodeFileManager, NodeType nodeType)  {
		this.nodeAddress = nodeAddress;
		this.nodeFileManager = nodeFileManager;
		this.nodeType = nodeType;
	}

	/**
	 * Constructor. No referenciará a nada hasta que se llame a {@link #saveNode(Node)}
	 * 
	 * @param nodeFileManager
	 * VLFM para el archivo que guarda este nodo.
	 * @param nodeType
	 * Indica el tipo de nodo que refiere.
	 */
	public NodeReferenceDisk(BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> nodeFileManager, NodeType nodeType)  {
		this.nodeFileManager = nodeFileManager;
		this.nodeType = nodeType;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.NodeReference#getNode()
	 */
	@Override
	public final Node<E, K> getNode() throws BTreeException {
		Node<E, K> node = null;
		if (this.nodeAddress != null) {
			try {
				node = this.nodeFileManager.get(this.nodeAddress);
			} catch (Exception e) {
				throw new BTreeException(e);
			}
			DiskNode<E, K> nodeDisk = (DiskNode<E, K>)node;
			nodeDisk.setNodeReference(this);
		}
		
		return node;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.NodeReference#getSameNodeReference(ar.com.datos.btree.sharp.node.Node, ar.com.datos.btree.sharp.impl.disk.node.NodeType)
	 */
	@Override
	public NodeReference<E, K> getSameNodeReference(Node<E, K> node, NodeType nodeType) {
		return new NodeReferenceDisk<E, K>(this.nodeAddress, this.nodeFileManager, nodeType);
	}
	
	/**
	 * Permite grabar el nodo correspondiente.
	 * 
	 * @param node
	 * Nodo que será referenciado por este.
	 * 
	 * @throws BTreeException
	 * Si hay algún problema grabando el nodo.
	 */
	public final void saveNode(Node<E, K> node) throws BTreeException {
		try {
			if (this.nodeAddress != null) {
				this.nodeFileManager.updateEntity(this.nodeAddress, node);
			} else {
				this.nodeAddress = this.nodeFileManager.addEntity(node);
			}
		} catch (Exception e) {
			throw new BTreeException(e);
		}
	}
	
	/**
	 * Permite obtener la dirección correspondiente a este {@link NodeReferenceDisk}.
	 */
	public final BlockAddress<Long, Short> getNodeAddress() {
		return this.nodeAddress;
	}

	/**
	 * Permite obtener el tipo de nodo que refiere esta {@link NodeReferenceDisk}.
	 */
	public NodeType getNodeType() {
		return this.nodeType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String node = (this.nodeAddress == null) ? "null" : getNode().toString();
		return "*" + node + "*";
	}
}
