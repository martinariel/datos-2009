package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Booleans.
 *
 * @author fvalido
 */
public class BooleanSerializer implements Serializer<Boolean> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Boolean object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Boolean> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[1];
		System.arraycopy(preObject, 0, realPreObject, 0, 1);
		boolean value = PrimitiveTypeSerializer.toBoolean(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 1];
		System.arraycopy(preObject, 1, remaining, 0, remaining.length);

		return new HydrateInfo<Boolean>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Boolean object) {
		return 1;
	}

}
