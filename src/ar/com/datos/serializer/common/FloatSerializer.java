package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Floats.
 *
 * @author fvalido
 */
public class FloatSerializer implements Serializer<Float> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Float object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Float> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[4];
		System.arraycopy(preObject, 0, realPreObject, 0, 4);
		float value = PrimitiveTypeSerializer.toFloat(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 4];
		System.arraycopy(preObject, 4, remaining, 0, remaining.length);

		return new HydrateInfo<Float>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Float object) {
		return 4;
	}
}
