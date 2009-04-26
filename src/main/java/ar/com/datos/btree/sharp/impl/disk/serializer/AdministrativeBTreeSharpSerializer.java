package ar.com.datos.btree.sharp.impl.disk.serializer;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.disk.AdministrativeBTreeSharp;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

/**
 * Permite serializar la información administrativa de un árbol b# en disco.
 * 
 * @author fvalido
 */
public class AdministrativeBTreeSharpSerializer<E extends Element<K>, K extends Key> implements Serializer<AdministrativeBTreeSharp<E, K>> {
	/** Serializador para el tamaño del bloque del archivo de las hojas. */
	private IntegerSerializer integerSerializer;
	/** 
	 * Serializador para el nombre del archivo de las hojas y para el nombre de la clase factory de los
	 * serializadores de listas de {@link Element} y de {@link Key}. 
	 */
	private StringSerializerDelimiter stringSerializer;

	/**
	 * Construye una instancia.
	 */
	public AdministrativeBTreeSharpSerializer() {
		this.integerSerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
		this.stringSerializer = new StringSerializerDelimiter();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, AdministrativeBTreeSharp<E, K> object) {
		this.stringSerializer.dehydrate(output, object.getLeafFileName());
		this.integerSerializer.dehydrate(output, object.getLeafBlockSize());
		this.stringSerializer.dehydrate(output, object.getElementAndKeyListSerializerFactoryClassFQDN());
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public AdministrativeBTreeSharp<E, K> hydrate(InputBuffer input) {
		String leafFileName = this.stringSerializer.hydrate(input);
		Integer leafBlockSize = this.integerSerializer.hydrate(input);
		String elementAndKeyListSerializerFactoryClassFQDN = this.stringSerializer.hydrate(input);
		
		return new AdministrativeBTreeSharp<E, K>(leafFileName, leafBlockSize, elementAndKeyListSerializerFactoryClassFQDN);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(AdministrativeBTreeSharp<E, K> object) {
		return this.stringSerializer.getDehydrateSize(object.getLeafFileName()) +
				this.integerSerializer.getDehydrateSize(object.getLeafBlockSize()) +
				this.stringSerializer.getDehydrateSize(object.getElementAndKeyListSerializerFactoryClassFQDN());
	}
}
