package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Longs.
 *
 * @author fvalido
 */
public class LongSerializer implements Serializer<Long> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Long object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Long> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[8];
		System.arraycopy(preObject, 0, realPreObject, 0, 8);
		long value = PrimitiveTypeSerializer.toLong(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 8];
		System.arraycopy(preObject, 8, remaining, 0, remaining.length);

		return new HydrateInfo<Long>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Long object) {
		return 8;
	}

}
