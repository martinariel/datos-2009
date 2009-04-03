package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para Bytes.
 *
 * @author fvalido
 */
public class ByteSerializer extends NumberSerializer<Byte> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, Byte object) {
		output.write(new byte[] { object });
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public Byte hydrate(InputBuffer input) {
		return input.read(new byte[1])[0];
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.marotte.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(Byte object) {
		return 1;
	}
}
