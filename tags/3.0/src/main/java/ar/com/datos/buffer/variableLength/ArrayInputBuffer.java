package ar.com.datos.buffer.variableLength;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;

public class ArrayInputBuffer implements InputBuffer {

	private Integer currentPos;
	private ArrayByte array;
	
	public ArrayInputBuffer(ArrayByte array) {
		super();
		this.currentPos = -1;
		this.array = array;
	}

	@Override
	public byte[] read(byte[] data) throws BufferException {
		for (Integer i = 0; i < data.length; i++){
			data[i] = this.read();
		}
		return data;
	}

	@Override
	public byte read() throws BufferException {
		this.currentPos++;
		try {
			return this.array.getByte(currentPos);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new BufferException(e);
		}
	}

}
