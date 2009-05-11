package ar.com.datos.btree.sharp.impl.disk.serializer;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.node.LeafNodeDisk;
import ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de un nodo hoja.
 *
 * @author fvalido
 */
public class LeafNodeSerializer<E extends Element<K>, K extends Key> implements Serializer<LeafNodeDisk<E, K>>{
	/** Configuraciones del árbol entre las cuales está la configuración del nodo. */
	private BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	/** Serializador de direcciones. */
	private AddressBlockSerializer addressSerializer;
	/** Serializador de un listado de elementos. */
	private ListElementsSerializer<E, K> listElementsSerializer;
	
	/**
	 * Constructor.
	 *
	 * @param listElementsSerializer
	 * Serializador de un listado de elementos.
	 *
	 * @param bTreeSharpConfigurationDisk
	 * Configuraciones del árbol entre las cuales está la configuración del nodo.
	 */
	public LeafNodeSerializer(ListElementsSerializer<E, K> listElementsSerializer, BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk) {
		this.listElementsSerializer = listElementsSerializer;
		this.bTreeSharpConfiguration = bTreeSharpConfigurationDisk;
		this.addressSerializer = SerializerCache.getInstance().getSerializer(AddressBlockSerializer.class);
		if (this.addressSerializer == null) {
			this.addressSerializer = new AddressBlockSerializer();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, LeafNodeDisk<E, K> object) throws SerializerException {
		// Debo hacer que el nodo tenga el tamaño de un bloque. Le agregaré al final basura hasta llenarlo.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityNode() - getDehydrateSize(object));
		
		if (trashSize < 0) {
			throw new SerializerException(this.getClass().getCanonicalName() + ": Se intenta grabar un nodo de un " +
					"tamaño mayor al permitido. Esto se debe a que el elemento puede ser demasiado grande para el" +
					"tamaño que se definió para el nodo.");
		}
		
		BlockAddress<Long, Short> previousAddress = (object.getPreviousNodeReference() == null) ? null : object.getPreviousNodeReference().getNodeAddress(); 
		this.addressSerializer.dehydrate(output, previousAddress);
		this.listElementsSerializer.dehydrate(output, object.getElements());
		BlockAddress<Long, Short> nextAddress = (object.getNextNodeReference() == null) ? null : object.getNextNodeReference().getNodeAddress();
		this.addressSerializer.dehydrate(output, nextAddress);

		if (trashSize > 0) {
			output.write(new byte[trashSize]);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public LeafNodeDisk<E, K> hydrate(InputBuffer input) {
		BlockAddress<Long, Short> previousAddress = this.addressSerializer.hydrate(input);
		NodeReference<E, K> previous = (previousAddress == null) ? null : new NodeReferenceDisk<E, K>(previousAddress, 
														this.bTreeSharpConfiguration.getLeafNodesFileManager(),
														NodeType.LEAF);
		List<E> elements = this.listElementsSerializer.hydrate(input);
		BlockAddress<Long, Short> nextAddress = this.addressSerializer.hydrate(input);
		NodeReference<E, K> next = (nextAddress == null) ? null : new NodeReferenceDisk<E, K>(nextAddress, 
														this.bTreeSharpConfiguration.getLeafNodesFileManager(),
														NodeType.LEAF);
		
		LeafNodeDisk<E, K> returnValue = new LeafNodeDisk<E, K>(this.bTreeSharpConfiguration, previous, next, elements);
		
		// Vacio el buffer de información basura que había dejado al final.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityNode() - getDehydrateSize(returnValue));
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
	public long getDehydrateSize(LeafNodeDisk<E, K> object) {
		BlockAddress<Long, Short> previousAddress = (object.getPreviousNodeReference() == null) ? null : object.getPreviousNodeReference().getNodeAddress();
		return this.addressSerializer.getDehydrateSize(previousAddress) * 2 +
				this.listElementsSerializer.getDehydrateSize(object.getElements());
	}

}
