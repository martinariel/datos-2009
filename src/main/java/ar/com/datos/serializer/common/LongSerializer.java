package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Longs.
 *
 * @author fvalido
 */
public class LongSerializer extends NumberSerializer<Long> {

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, Long object) {
		output.write(PrimitiveTypeSerializer.toByte(object));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public Long hydrate(InputBuffer input) {
		return PrimitiveTypeSerializer.toLong(input.read(new byte[8]));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(Long object) {
		return 8;
	}
}
