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
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;

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
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, LeafNodeDisk<E, K> object) {
		this.addressSerializer.dehydrate(output, object.getPreviousNodeReference().getNodeAddress());
		this.listElementsSerializer.dehydrate(output, object.getElements());
		this.addressSerializer.dehydrate(output, object.getNextNodeReference().getNodeAddress());
		
		// Debo hacer que el nodo tenga el tamaño de un bloque. Le agrego basura hasta llenarlo.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityLeafNode() - getDehydrateSize(object));
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
		NodeReference<E, K> previous = new NodeReferenceDisk<E, K>(this.addressSerializer.hydrate(input), 
														this.bTreeSharpConfiguration.getLeafNodesFileManager(),
														NodeType.LEAF);
		List<E> elements = this.listElementsSerializer.hydrate(input);
		NodeReference<E, K> next = new NodeReferenceDisk<E, K>(this.addressSerializer.hydrate(input), 
														this.bTreeSharpConfiguration.getLeafNodesFileManager(),
														NodeType.LEAF);
		
		LeafNodeDisk<E, K> returnValue = new LeafNodeDisk<E, K>(this.bTreeSharpConfiguration, previous, next, elements);
		
		// Vacio el buffer de información basura que había dejado al final.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityLeafNode() - getDehydrateSize(returnValue));
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
		return this.addressSerializer.getDehydrateSize(object.getPreviousNodeReference().getNodeAddress()) * 2 +
				this.listElementsSerializer.getDehydrateSize(object.getElements());
	}

}
