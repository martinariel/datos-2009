package ar.com.datos.compressor.lzp.table.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.CharacterSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializar de un {@link LzpContext}.
 * 
 * @author fvalido
 */
public class LzpContextSerializer implements Serializer<LzpContext> {
	/** Serializador de un único caracter */
	private CharacterSerializer characterSerializer;

	/**
	 * Constructor
	 */
	public LzpContextSerializer() {
		this.characterSerializer = SerializerCache.getInstance().getSerializer(CharacterSerializer.class);
	}
		
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, LzpContext object) throws SerializerException {
		String context = object.toString();
		this.characterSerializer.dehydrate(output, context.charAt(0));
		this.characterSerializer.dehydrate(output, context.charAt(1));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public LzpContext hydrate(InputBuffer input) throws SerializerException {
		return new LzpContext(this.characterSerializer.hydrate(input), this.characterSerializer.hydrate(input));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(LzpContext object) {
		return this.characterSerializer.getDehydrateSize(null) * 2;
	}
}
