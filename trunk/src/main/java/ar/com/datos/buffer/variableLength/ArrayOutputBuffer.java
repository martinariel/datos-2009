package ar.com.datos.buffer.variableLength;

import ar.com.datos.buffer.OutputBuffer;

public class ArrayOutputBuffer implements OutputBuffer {

	private Integer currentPos;
	private ArrayByte array;
	
	public ArrayOutputBuffer(ArrayByte array) {
		super();
		this.currentPos = 0;
		this.array = array;
	}

	public ArrayOutputBuffer(Integer bufferSize) {
		this(new SimpleArrayByte(new byte[bufferSize]));
	}

	@Override
	public void write(byte[] data) {
		for (Integer i = 0; i < data.length; i++) {
			this.array.setByte(i + currentPos, data[i]);
		}
		this.currentPos += data.length;
	}

	@Override
	public void write(byte data) {
		this.array.setByte(currentPos, data);
		this.currentPos ++;
	}

	public ArrayByte getArrayByte() {
		return this.array;
	}
}
