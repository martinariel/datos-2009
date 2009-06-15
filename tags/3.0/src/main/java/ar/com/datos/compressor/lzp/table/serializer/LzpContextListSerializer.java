package ar.com.datos.compressor.lzp.table.serializer;

import java.util.List;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de un listado de {@link LzpContext}. Necesario para el árbol b# usado como persistencia.
 * 
 * @author fvalido
 */
public class LzpContextListSerializer implements ListKeysSerializer<LzpContext> {
	/** Serializador de una colección de {@link LzpContext} */
	private CollectionSerializer<LzpContext> collectionSerializer; 
	
	/**
	 * Constructor
	 */
	public LzpContextListSerializer() {
		this.collectionSerializer = new CollectionSerializer<LzpContext>(new LzpContextSerializer());
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, List<LzpContext> object) throws SerializerException {
		this.collectionSerializer.dehydrate(output, object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public List<LzpContext> hydrate(InputBuffer input) throws SerializerException {
		// Ese cast es muy sucio :P
		return (List<LzpContext>)this.collectionSerializer.hydrate(input);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(List<LzpContext> object) {
		return this.collectionSerializer.getDehydrateSize(object);
	}
}
