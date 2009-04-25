package ar.com.datos.buffer;

import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.StandardFileWrapper;

public class FileOutputBuffer implements OutputBuffer {

	private StandardFileWrapper file;

	public FileOutputBuffer(StandardFileWrapper file) {
		this.file = file;
	}

	@Override
	public void write(byte[] data) {
		this.file.write(this.file.getSize(), new SimpleArrayByte(data));
	}

	@Override
	public void write(byte data) {
		this.file.write(this.file.getSize(), new SimpleArrayByte(new byte[]{data}));
	}

}
