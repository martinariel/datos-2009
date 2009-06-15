package ar.com.datos.btree.sharp.impl.disk.serializer;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.node.EspecialRootNodeDisk;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de un nodo raiz especial.
 *
 * @author fvalido
 */
public class EspecialRootNodeSerializer<E extends Element<K>, K extends Key> implements Serializer<EspecialRootNodeDisk<E, K>>{
	/** Configuraciones del árbol entre las cuales está la configuración del nodo. */
	private BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	/** Serializador de un listado de elementos. */
	private ListElementsSerializer<E, K> listElementsSerializer;
	/** Árbol que hará uso de este serializador */
	private BTreeSharp<E, K> btree;
		
	/**
	 * Constructor.
	 *
	 * @param listElementsSerializer
	 * Serializador de un listado de elementos.
	 *
	 * @param bTreeSharpConfigurationDisk
	 * Configuraciones del árbol entre las cuales está la configuración del nodo.
	 * 
	 * NOTA: Para terminar la construcción debe llamarse al método {@link #setBTree(BTreeSharp)}
	 */
	public EspecialRootNodeSerializer(ListElementsSerializer<E, K> listElementsSerializer, BTreeSharpConfigurationDisk<E, K> bTreeSharpConfigurationDisk) {
		this.listElementsSerializer = listElementsSerializer;
		this.bTreeSharpConfiguration = bTreeSharpConfigurationDisk;
	}

	/**
	 * Permite establecer el BTree que usará este Serializer.
	 */
	public void setBTree(BTreeSharp<E, K> btree) {
		this.btree = btree;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, EspecialRootNodeDisk<E, K> object) throws SerializerException {
		// Debo hacer que el nodo tenga el tamaño de un bloque. Le agregaré al final basura hasta llenarlo.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityRootNode() - 1 - getDehydrateSize(object));
		
		if (trashSize < 0) {
			throw new SerializerException(this.getClass().getCanonicalName() + ": Se intenta grabar un nodo de un " +
					"tamaño mayor al permitido. Esto se debe a que el elemento puede ser demasiado grande para el" +
					"tamaño que se definió para el nodo.");
		}
		
		this.listElementsSerializer.dehydrate(output, object.getElements());
		
		if (trashSize > 0) {
			output.write(new byte[trashSize]);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public EspecialRootNodeDisk<E, K> hydrate(InputBuffer input) {
		List<E> elements = this.listElementsSerializer.hydrate(input);
		
		EspecialRootNodeDisk<E, K> returnValue = new EspecialRootNodeDisk<E, K>(this.bTreeSharpConfiguration, this.btree, elements);
		
		// Vacio el buffer de información basura que había dejado al final.
		int trashSize = (int)(this.bTreeSharpConfiguration.getMaxCapacityRootNode() - 1 - getDehydrateSize(returnValue));
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
	public long getDehydrateSize(EspecialRootNodeDisk<E, K> object) {
		return this.listElementsSerializer.getDehydrateSize(object.getElements());
	}

}
