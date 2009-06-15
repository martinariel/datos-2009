package ar.com.datos.test.serializer.mock;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;

/**
 * Implementacion de {@link InputBuffer} para poder realizar los tests.
 * 
 * @author fvalido
 */
public class InputBufferTest implements InputBuffer {
	private byte[] data;
	private int pos;
	
	public InputBufferTest(byte[] data) {
		this.data = data;
		this.pos = 0;
	}
	
	public byte[] read(byte[] data) throws BufferException {
		if (this.pos + data.length > this.data.length) {
			throw new BufferException("No hay suficientes datos");
		}
		
		System.arraycopy(this.data, this.pos, data, 0, data.length);
		pos += data.length;
		
		return data;
	}

	public byte read() throws BufferException {
		if (this.pos == this.data.length) {
			throw new BufferException("No hay suficientes datos");
		}
		
		this.pos++;
		
		return this.data[pos - 1];
	}
	

}
