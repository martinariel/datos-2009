package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Shorts.
 *
 * @author fvalido
 */
public class ShortSerializer extends NumberSerializer<Short> {

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, Short object) {
		output.write(PrimitiveTypeSerializer.toByte(object));		
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public Short hydrate(InputBuffer input) {
		return PrimitiveTypeSerializer.toShort(input.read(new byte[2]));
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(Short object) {
		return 2;
	}
}