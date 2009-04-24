package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.util.ArraysUtils;

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
	 * Permite construir una instancia que usará un delimitador por defecto.
	 */
	public StringSerializerDelimiter() {
		this(new byte[] {0, 0});
	}
	
	/**
	 * Permite construir una instancia que usará como delimitador la secuencia
	 * pasada.
	 */
	public StringSerializerDelimiter(byte[] delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Permite construir una instancia que usará como delimitador el string
	 * pasado.
	 */
	public StringSerializerDelimiter(String delimiter) {
		this.delimiter = stringToByte(delimiter);
	}

	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, String object) {
		byte[] bytes = new byte[object.length() * 2 + this.delimiter.length];

		byte[] convertedString = stringToByte(object);
		System.arraycopy(convertedString, 0, bytes, 0, convertedString.length);
		System.arraycopy(this.delimiter, 0, bytes, convertedString.length, this.delimiter.length);

		output.write(bytes);
	}

    /*
     * (non-Javadoc)
     * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
     */
	@Override
	public String hydrate(InputBuffer input) {
		// Pido bytes hasta llegar al delimitador.
		byte[] preObject = new byte[10];
		byte[] delimiterSuspect = new byte[this.delimiter.length];
		int pos = 0;
		int posDelimiter = 0;
		boolean found = false;
		byte currentByte = input.read();
		while (!found) {
			if (this.delimiter[posDelimiter] == currentByte) {
				found = true;
				posDelimiter = 0;
				delimiterSuspect[posDelimiter] = currentByte;
				posDelimiter++;
				while (found && posDelimiter < this.delimiter.length) {
					currentByte = input.read();
					delimiterSuspect[posDelimiter] = currentByte;
					found = this.delimiter[posDelimiter] == currentByte;
					posDelimiter++;
				}
				if (!found) {
					preObject = ArraysUtils.ensureCapacity(preObject, pos + posDelimiter);
					System.arraycopy(delimiterSuspect, 0, preObject, pos, posDelimiter - 1);
					pos += posDelimiter - 1;
					posDelimiter = 0;
				}
			}
			if (!found) {
				preObject = ArraysUtils.ensureCapacity(preObject, pos + 1);
				preObject[pos] = currentByte;
				pos++;
				currentByte = input.read();
			}
		}
				
		byte[] realPreObject = new byte[pos];
		System.arraycopy(preObject, 0, realPreObject, 0, pos);
		return new String(PrimitiveTypeSerializer.toCharArray(realPreObject));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(String object) {
		return object.length() * 2 + this.delimiter.length;
	}
}
