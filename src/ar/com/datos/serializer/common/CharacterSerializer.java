package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Chars.
 *
 * @author fvalido
 */
public class CharacterSerializer implements Serializer<Character> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(Character object) {
		return PrimitiveTypeSerializer.toByte(object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<Character> hydrate(byte[] preObject) {
		// Obtengo el valor convertido.
		byte[] realPreObject = new byte[2];
		System.arraycopy(preObject, 0, realPreObject, 0, 2);
		char value = PrimitiveTypeSerializer.toChar(realPreObject);

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - 2];
		System.arraycopy(preObject, 2, remaining, 0, remaining.length);

		return new HydrateInfo<Character>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(Character object) {
		return 2;
	}

}
