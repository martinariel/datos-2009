package ar.com.datos.test.serializer.mock;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.util.ArraysUtils;

/**
 * Implementacion de {@link OutputBuffer} para poder realizar los tests.
 * 
 * @author fvalido
 */
public class OutputBufferTest implements OutputBuffer {
	private byte[] data;
	private int pos;
	
	public OutputBufferTest() {
		this.data = new byte[10];
		this.pos = 0;
	}

	public InputBuffer getAsInputBuffer() {
		byte[] currentData = new byte[pos];
		System.arraycopy(data, 0, currentData, 0, pos);
		return new InputBufferTest(currentData);
	}
	
	public void write(byte[] data) {
		this.data = ArraysUtils.ensureCapacity(this.data, this.pos + data.length);
		System.arraycopy(data, 0, this.data, this.pos, data.length);
		this.pos += data.length;
	}

	public void write(byte data) {
		this.data = ArraysUtils.ensureCapacity(this.data, this.pos + 1);
		this.data[pos] = data;
		this.pos++;
	}

}