package ar.com.datos.buffer;

import ar.com.datos.buffer.exception.BufferException;
import ar.com.datos.file.StandardFileWrapper;

public class FileInputBuffer implements InputBuffer {

	private StandardFileWrapper dataSource;
	private Long currentOffset;
	
	public FileInputBuffer(StandardFileWrapper dataSource, Long currentOffset) {
		super();
		this.dataSource = dataSource;
		this.currentOffset = currentOffset;
	}

	@Override
	public byte[] read(byte[] data) throws BufferException {
		this.dataSource.read(currentOffset, data);
		currentOffset += data.length;
		return data;
	}

	@Override
	public byte read() throws BufferException {
		return this.read(new byte[1])[0];
	}

	public Long getCurrentOffset() {
		return currentOffset;
	}

}
