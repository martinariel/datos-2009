package ar.com.datos.btree.sharp.impl.disk.serializer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.node.InternalNodeDisk;
import ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.ByteSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de un nodo interno.
 *
 * @author fvalido
 */
public class InternalNodeSerializer<E extends Element<K>, K extends Key> implements Serializer<InternalNodeDisk<E, K>> {
	/** Configuraciones del árbol entre las cuales está la configuración del nodo. */
	private BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	/** Serializador de direcciones. */
	private AddressBlockSerializer addressSerializer;
	/** Serializador de tipo de nodo */
	private ByteSerializer byteSerializer;
	/** Serializador de un listado de keys. */
	private ListKeysSerializer<K> listKeysSerializer;
	
	/**
	 * Constructor.
	 *
	 * @param listKeysSerializer
	 * Serializador de un listado de keys.
	 *
	 * @param bTreeSharpConfigurationDisk
	 * Configuraciones del árbol entre las cuales está la configuración del nodo.
	 */
	public InternalNodeSerializer(ListKeysSerializer<K> listKeysSerializer, BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk) {
		this.listKeysSerializer = listKeysSerializer;
		this.bTreeSharpConfiguration = bTreeSharpConfigurationDisk;
		this.addressSerializer = SerializerCache.getInstance().getSerializer(AddressBlockSerializer.class);
		if (this.addressSerializer == null) {
			this.addressSerializer = new AddressBlockSerializer();
		}
		this.byteSerializer = SerializerCache.getInstance().getSerializer(ByteSerializer.class);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, InternalNodeDisk<E, K> object) throws SerializerException {
		// Debo hacer que el nodo tenga el tamaño de un bloque. Le agregaré al final basura hasta llenarlo.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityNode() - 1 - getDehydrateSize(object));
		
		if (trashSize < 0) {
			throw new SerializerException(this.getClass().getCanonicalName() + ": Se intenta grabar un nodo de un " +
					"tamaño mayor al permitido. Esto se debe a que la key puede ser demasiado grande para el" +
					"tamaño que se definió para el nodo.");
		}
		
		// Primero armo listas por separado de Keys y NodeReferences
		List<K> keys = new LinkedList<K>();
		List<NodeReferenceDisk<E, K>> nodeReferences = new LinkedList<NodeReferenceDisk<E,K>>();
		nodeReferences.add((NodeReferenceDisk<E, K>)object.getFirstChild()); // Agrego el primer hijo.
		Iterator<KeyNodeReference<E, K>> it = object.getKeysNodes().iterator();
		KeyNodeReference<E, K> currentKeyNodeReference;
		while (it.hasNext()) {
			currentKeyNodeReference = it.next();
			keys.add(currentKeyNodeReference.getKey());
			nodeReferences.add((NodeReferenceDisk<E, K>)currentKeyNodeReference.getNodeReference());
		}
		
		// Serializo las keys.
		this.listKeysSerializer.dehydrate(output, keys);
		
		// Serializo los NodeReference.
		// En primer lugar debo marcar que tipo de nodos son. Para saber que tipo de de referencias
		// contiene tomo cualquier referencia y me fijo el tipo.
		Byte nodeType = ((NodeReferenceDisk<E, K>)object.getFirstChild()).getNodeType().getType();
		this.byteSerializer.dehydrate(output, nodeType);
		// Ahora serializo todos los NodeReference. No hace falta que ponga la cantidad porque
		// se que es la cantidad de Keys + 1.
		Iterator<NodeReferenceDisk<E, K>> itNodeReferences = nodeReferences.iterator();
		while (itNodeReferences.hasNext()) {
			this.addressSerializer.dehydrate(output, itNodeReferences.next().getNodeAddress());
		}
		
		if (trashSize > 0) {
			output.write(new byte[trashSize]);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public InternalNodeDisk<E, K> hydrate(InputBuffer input) {
		// Obtengo las keys
		List<K> keys = this.listKeysSerializer.hydrate(input);
		
		// Obtengo los NodeReference.
		// En primer lugar debo saber que tipo de nodos son. Esto fue marcado con un byte.
		Byte nodeTypeByte = this.byteSerializer.hydrate(input);
		NodeType nodeType = NodeType.getNodeType(nodeTypeByte);
		// Ahora hidrato los NodeReference. La cantidad de nodeReference a hidratar será
		// la cantidad de Keys + 1.
		List<NodeReference<E, K>> nodeReferences = new LinkedList<NodeReference<E, K>>();
		for (int i = 0; i < keys.size() + 1; i++) {
			nodeReferences.add(new NodeReferenceDisk<E, K>(this.addressSerializer.hydrate(input),
															getFileManagerFor(nodeType),
															nodeType));
		}
		
		// Junto las keys y las referencias en orden adecuado para poder crear el nodo.
		NodeReference<E, K> firstChild = nodeReferences.remove(0); 
		Iterator<K> itKeys = keys.iterator();
		Iterator<NodeReference<E, K>> itNodeReferences = nodeReferences.iterator();
		List<KeyNodeReference<E, K>> keysNodes = new LinkedList<KeyNodeReference<E,K>>();
		while (itKeys.hasNext()) {
			keysNodes.add(new KeyNodeReference<E, K>(itKeys.next(), itNodeReferences.next()));
		}
		
		InternalNodeDisk<E, K> returnValue = new InternalNodeDisk<E, K>(this.bTreeSharpConfiguration, firstChild, keysNodes);
		
		// Vacio el buffer de información basura que había dejado al final.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityNode() - 1 - getDehydrateSize(returnValue));
		if (trashSize > 0) {
			input.read(new byte[trashSize]);
		}
		
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(InternalNodeDisk<E, K> object) {
		// Primero armo una lista de keys.
		List<K> keys = new LinkedList<K>();
		Iterator<KeyNodeReference<E, K>> it = object.getKeysNodes().iterator();
		while (it.hasNext()) {
			keys.add(it.next().getKey());
		}

		return this.listKeysSerializer.getDehydrateSize(keys) + this.byteSerializer.getDehydrateSize(null) +
				(object.getKeysNodes().size() + 1) * this.addressSerializer.getDehydrateSize(null);
	}

	/**
	 * Permite saber el archivo a usar para el tipo de nodo pasado.
	 */
	private BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> getFileManagerFor(NodeType nodeType) {
		BlockAccessor<BlockAddress<Long, Short>, Node<E, K>> returnValue = null;
		switch (nodeType) {
			case LEAF: returnValue = this.bTreeSharpConfiguration.getLeafNodesFileManager(); break;
			case INTERNAL: returnValue = this.bTreeSharpConfiguration.getInternalNodesFileManager(); break;
			case ROOT: returnValue = this.bTreeSharpConfiguration.getInternalNodesFileManager(); break;
			case ESPECIALROOT: returnValue = this.bTreeSharpConfiguration.getInternalNodesFileManager(); break;
		}
		
		return returnValue;
	}
}
