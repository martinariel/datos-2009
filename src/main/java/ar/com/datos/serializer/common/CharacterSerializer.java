package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
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
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, Character object) {
		output.write(PrimitiveTypeSerializer.toByte(object));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public Character hydrate(InputBuffer input) {
		return PrimitiveTypeSerializer.toChar(input.read(new byte[2]));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(Character object) {
		return 2;
	}
}
