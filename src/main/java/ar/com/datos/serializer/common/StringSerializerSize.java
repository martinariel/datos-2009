package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.exception.StringSerializerSizeException;

/**
 * {@link Serializer} para String que antepone en la deshidratacion el largo del String
 * en un campo fijo de 1 byte de largo. Por tanto, el maximo String que se puede serializar
 * medira 256 caracteres.
 *
 * @author fvalido
 */
public class StringSerializerSize implements Serializer<String> {
	/**
	 * Maximo largo al que se serializa. Pasado ese largo se trunca el string a serializar.
	 * Debe ser un valor entre 1 y 256.
	 */
	private short maxLength;

	/**
	 * @param maxLength
	 * Maximo largo al que se serializa. Pasado ese largo se trunca el string a serializar.
	 * Debe ser un valor entre 1 y 256.
	 *
	 * @throws
	 * Si se paso un maxLength que no esta entre 1 y 256.
	 */
	public StringSerializerSize(short maxLength) throws StringSerializerSizeException {
		if (maxLength < 1 || maxLength > 256) {
			throw new StringSerializerSizeException();
		}

		this.maxLength = maxLength;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(String object) {
		String value = object;
		int length = object.length();
		if (length > maxLength) {
			value = object.substring(0, this.maxLength);
		}

		byte[] bytes = new byte[value.length() * 2 + 1];
		bytes[0] = (byte)(value.length() - 128 - 1);

		char[] charsValue = new char[value.length()];
		value.getChars(0, value.length(), charsValue, 0);
		byte[] convertedString = PrimitiveTypeSerializer.toByte(charsValue);
		System.arraycopy(convertedString, 0, bytes, 1, convertedString.length);

		return bytes;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<String> hydrate(byte[] preObject) {
		int length = preObject[0] + 128 + 1;

		byte[] sourceString = new byte[length * 2];
		System.arraycopy(preObject, 1, sourceString, 0, sourceString.length);

		String value = new String(PrimitiveTypeSerializer.toCharArray(sourceString));

		byte[] remaining = new byte[preObject.length - (sourceString.length + 1)];
		System.arraycopy(preObject, length * 2 + 1, remaining, 0, remaining.length);

		return new HydrateInfo<String>(value, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(String object) {
		return (object.length() > this.maxLength ? this.maxLength : object.length()) * 2 + 1;
	}
}
