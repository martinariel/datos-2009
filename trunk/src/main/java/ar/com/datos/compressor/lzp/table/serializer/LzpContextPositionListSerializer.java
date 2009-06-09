package ar.com.datos.compressor.lzp.table.serializer;

import java.util.List;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextPosition;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de un listado de {@link LzpContextPosition}. Necesario para el árbol b# usado como persistencia.
 * 
 * @author fvalido
 */
public class LzpContextPositionListSerializer implements ListElementsSerializer<LzpContextPosition, LzpContext> {
	/** Serializador de una colección de {@link LzpContextPosition} */
	private CollectionSerializer<LzpContextPosition> collectionSerializer; 
	
	/**
	 * Constructor
	 */
	public LzpContextPositionListSerializer() {
		this.collectionSerializer = new CollectionSerializer<LzpContextPosition>(new LzpContextPositionSerializer());
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, List<LzpContextPosition> object) throws SerializerException {
		this.collectionSerializer.dehydrate(output, object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public List<LzpContextPosition> hydrate(InputBuffer input) throws SerializerException {
		// Ese cast es muy sucio :P
		return (List<LzpContextPosition>)this.collectionSerializer.hydrate(input);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(List<LzpContextPosition> object) {
		return this.collectionSerializer.getDehydrateSize(object);
	}
}
