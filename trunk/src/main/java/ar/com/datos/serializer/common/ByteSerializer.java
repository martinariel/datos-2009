package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Bytes.
 *
 * @author fvalido
 */
public class ByteSerializer implements Serializer<Byte> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Byte object) {
		return new byte[] { object };
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Byte> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte value = preObject[0];

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 1];
		System.arraycopy(preObject, 1, remaining, 0, remaining.length);

		return new HydrateInfo<Byte>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Byte object) {
		return 1;
	}

}
