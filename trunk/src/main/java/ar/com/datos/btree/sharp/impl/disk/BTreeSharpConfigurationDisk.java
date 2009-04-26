package ar.com.datos.btree.sharp.impl.disk;

import java.io.IOException;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.StateInternalNodeSerializer;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;

/**
 * Implementacion de un {@link BTreeSharpConfiguration} para un {@link BTreeSharp} en disco.
 *
 * @author fvalido
 */
public class BTreeSharpConfigurationDisk<E extends Element<K>, K extends Key> extends BTreeSharpConfiguration<E, K> {
	/** Serializador que permite serializar diferentes tipos de nodos Internos. Ver Javadoc de la clase para detalles. */
	private StateInternalNodeSerializer<E, K> stateInternalNodeSerializer;
	/** Serializador para nodos hojas */
	private LeafNodeSerializer<E, K> leafNodeSerializer;
	
	/** VLFM para el archivo que guarda nodos internos. */
	private BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> internalNodesFileManager;
	/** VLFM para el archivo que guarda nodos hojas. */
	private BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> leafNodesFileManager;

	/**
	 * Permite crear un {@link BTreeSharpConfigurationDisk} recibiendo todos los parametros.
	 */
	public BTreeSharpConfigurationDisk(short maxCapacityInternalNode, short maxCapacityLeafNode, short maxCapacityRootNode, 
									StateInternalNodeSerializer<E, K> stateInternalNodeSerializer,
									LeafNodeSerializer<E, K> leafNodeSerializer,
									BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> internalNodesFileManager,
									BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> leafNodesFileManager) {
		super(maxCapacityInternalNode, maxCapacityLeafNode, maxCapacityRootNode, new BTreeSharpNodeDiskFactory<E, K>());
		this.stateInternalNodeSerializer = stateInternalNodeSerializer;
		this.leafNodeSerializer = leafNodeSerializer;
		this.internalNodesFileManager = internalNodesFileManager;
		this.leafNodesFileManager = leafNodesFileManager;
	}

	/**
	 * Constructor.
	 * Requiere que se llame a cada uno de los #set... por separado antes de ser usado.
	 */
	public BTreeSharpConfigurationDisk() {
		super(new BTreeSharpNodeDiskFactory<E, K>());
	}
	
    /*
     * (non-Javadoc)
     * @see ar.com.datos.btree.sharp.conf.AdministrativeBTreeSharp#closeTree()
     */
	@Override
	public void closeTree() throws BTreeException {
		try {
			this.internalNodesFileManager.close();
			this.leafNodesFileManager.close();
		} catch (IOException e) {
			throw new BTreeException(e);
		}
	}    
	
	/**
	 * Permite obtener el serializador para nodos del archivo de nodos internos.
	 */
	public StateInternalNodeSerializer<E, K> getStateInternalNodeSerializer() {
		return stateInternalNodeSerializer;
	}
	
	/**
	 * Permite obtener el serializador para nodos del archivo de hojas.
	 */
	public LeafNodeSerializer<E, K> getLeafNodeSerializer() {
		return this.leafNodeSerializer;
	}
	
	/**
	 * Permite obtener el VLFM correspondiente al archivo de nodos internos. 
	 */
	public BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> getInternalNodesFileManager() {
		return this.internalNodesFileManager;
	}

	/**
	 * Permite obtener el VLFM correspondiente al archivo de nodos hojas.
	 */
	public BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> getLeafNodesFileManager() {
		return this.leafNodesFileManager;
	}
	
	/**
	 * Permite establecer el VLFM correspondiente al archivo de nodos internos. 
	 */
	public void setInternalNodesFileManager(
			BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> internalNodesFileManager) {
		this.internalNodesFileManager = internalNodesFileManager;
	}
	
	/**
	 * Permite establecer el serializador para nodos del archivo de hojas.
	 */
	public void setLeafNodeSerializer(LeafNodeSerializer<E, K> leafNodeSerializer) {
		this.leafNodeSerializer = leafNodeSerializer;
	}

	/**
	 * Permite establecer el VLFM correspondiente al archivo de nodos hojas.
	 */
	public void setLeafNodesFileManager(
			BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> leafNodesFileManager) {
		this.leafNodesFileManager = leafNodesFileManager;
	}

	/**
	 * Permite establecer el serializador para nodos del archivo de nodos internos.
	 */
	public void setStateInternalNodeSerializer(
			StateInternalNodeSerializer<E, K> stateInternalNodeSerializer) {
		this.stateInternalNodeSerializer = stateInternalNodeSerializer;
	}
}
