package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Doubles.
 *
 * @author fvalido
 */
public class DoubleSerializer implements Serializer<Double> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Double object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Double> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[8];
		System.arraycopy(preObject, 0, realPreObject, 0, 8);
		double value = PrimitiveTypeSerializer.toDouble(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 8];
		System.arraycopy(preObject, 8, remaining, 0, remaining.length);

		return new HydrateInfo<Double>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Double object) {
		return 8;
	}

}
