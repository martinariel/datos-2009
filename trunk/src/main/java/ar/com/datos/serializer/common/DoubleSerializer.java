package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Doubles.
 *
 * @author fvalido
 */
public class DoubleSerializer extends NumberSerializer<Double> {
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, Double object) {
		output.write(PrimitiveTypeSerializer.toByte(object));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public Double hydrate(InputBuffer input) {
		return PrimitiveTypeSerializer.toDouble(input.read(new byte[8]));
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(Double object) {
		return 8;
	}
}
