package ar.com.datos.serializer.common;

import ar.com.datos.serializer.HydrateInfo;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para String que, en la deshidratacion, pone al final del
 * String a guardar un delimitador fijo para reconocer el final del String.
 * Queda en el usuario de esta clase especificar un delimitador que no este
 * incluido en ninguno de los Strings a serializar.
 *
 * @author fvalido
 */
public class StringSerializerDelimiter implements Serializer<String> {
	/**
	 * Secuencia que sera usada como delimitador del String.
	 */
	private byte[] delimiter;

	/**
	 * Convierte un String a byte[]
	 */
	private byte[] stringToByte(String string) {
		char[] charsValue = new char[string.length()];
		string.getChars(0, string.length(), charsValue, 0);
		byte[] bytes = PrimitiveTypeSerializer.toByte(charsValue);

		return bytes;
	}

	/**
	 * Permite construir una instancia que usara como delimitador la secuencia
	 * pasada.
	 */
	public StringSerializerDelimiter(byte[] delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Permite construir una instancia que usara como delimitador el string
	 * pasado.
	 */
	public StringSerializerDelimiter(String delimiter) {
		this.delimiter = stringToByte(delimiter);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#dehydrate(java.lang.Object)
	 */
	public byte[] dehydrate(String object) {
		byte[] bytes = new byte[object.length() * 2 + this.delimiter.length];

		byte[] convertedString = stringToByte(object);
		System.arraycopy(convertedString, 0, bytes, 0, convertedString.length);
		System.arraycopy(this.delimiter, 0, bytes, convertedString.length, this.delimiter.length);

		return bytes;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#hydrate(byte[])
	 */
	public HydrateInfo<String> hydrate(byte[] preObject) {
		// Encuentro el delimitador.
		int pos = 0;
		int posDelimiter = 0;
		boolean found = false;
		while (!found) {
			if (this.delimiter[posDelimiter] == preObject[pos]) {
				found = true;
				posDelimiter++;
				while (found && posDelimiter < this.delimiter.length) {
					found = this.delimiter[posDelimiter] == preObject[pos + posDelimiter];
					posDelimiter++;
				}
				if (!found) {
					posDelimiter = 0;
				}
			}
			pos++;
		}

		// Obtengo el String buscado.
		byte[] realPreObject = new byte[pos - 1];
		System.arraycopy(preObject, 0, realPreObject, 0, realPreObject.length);
		String string = new String(PrimitiveTypeSerializer.toCharArray(realPreObject));

		// Obtengo el resto del Byte
		byte[] remaining = new byte[preObject.length - (realPreObject.length + this.delimiter.length)];
		System.arraycopy(preObject, realPreObject.length + this.delimiter.length, remaining, 0, remaining.length);

		return new HydrateInfo<String>(string, remaining);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public int getDehydrateSize(String object) {
		return object.length() * 2 + this.delimiter.length;
	}
}
