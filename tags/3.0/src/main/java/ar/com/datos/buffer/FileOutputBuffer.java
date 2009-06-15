package ar.com.datos.buffer;

import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.StandardFileWrapper;

public class FileOutputBuffer implements OutputBuffer {

	private StandardFileWrapper file;
	private Long offset;

	public FileOutputBuffer(StandardFileWrapper file, Long offset) {
		super();
		this.file = file;
		this.offset = offset;
	}

	public FileOutputBuffer(StandardFileWrapper file) {
		this(file, file.getSize());
	}

	@Override
	public void write(byte[] data) {
		this.file.write(offset, new SimpleArrayByte(data));
		offset += data.length;
	}

	@Override
	public void write(byte data) {
		this.file.write(offset, new SimpleArrayByte(new byte[]{data}));
		offset ++;
	}

}
