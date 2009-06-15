package ar.com.datos.compressor.lzp.table.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextPosition;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.util.UnsignedInt;

/**
 * Serializar de un {@link LzpContextPosition}.
 * 
 * @author fvalido
 */
public class LzpContextPositionSerializer implements Serializer<LzpContextPosition> {
	/** Serializador para la posición*/
	private IntegerSerializer integerSerializer;
	/** Serializar para un lzpContext */
	private LzpContextSerializer lzpContextSerializer;
	
	/**
	 * Constructor.
	 */
	public LzpContextPositionSerializer() {
		this.integerSerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
		this.lzpContextSerializer = new LzpContextSerializer();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, LzpContextPosition object) throws SerializerException {
		this.integerSerializer.dehydrate(output, object.getPosition().getAsSignedInt());
		this.lzpContextSerializer.dehydrate(output, object.getKey());
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public LzpContextPosition hydrate(InputBuffer input) throws SerializerException {
		UnsignedInt position = new UnsignedInt(this.integerSerializer.hydrate(input));
		LzpContext lzpContext = this.lzpContextSerializer.hydrate(input);
		
		return new LzpContextPosition(lzpContext, position);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(LzpContextPosition object) {
		return this.integerSerializer.getDehydrateSize(null) + this.lzpContextSerializer.getDehydrateSize(object.getKey());
	}
}
