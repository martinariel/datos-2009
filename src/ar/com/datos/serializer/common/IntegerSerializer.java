package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Integer.
 *
 * @author fvalido
 */
public class IntegerSerializer implements Serializer<Integer> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Integer object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Integer> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[4];
		System.arraycopy(preObject, 0, realPreObject, 0, 4);
		int value = PrimitiveTypeSerializer.toInt(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 4];
		System.arraycopy(preObject, 4, remaining, 0, remaining.length);

		return new HydrateInfo<Integer>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Integer object) {
		return 4;
	}

}
