package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Shorts.
 *
 * @author fvalido
 */
public class ShortSerializer implements Serializer<Short> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Short object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Short> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[2];
		System.arraycopy(preObject, 0, realPreObject, 0, 2);
		short value = PrimitiveTypeSerializer.toShort(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 2];
		System.arraycopy(preObject, 2, remaining, 0, remaining.length);

		return new HydrateInfo<Short>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Short object) {
		return 2;
	}

}
