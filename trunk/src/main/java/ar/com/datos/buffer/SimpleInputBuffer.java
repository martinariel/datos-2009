/**
 * 
 */
package ar.com.datos.buffer;

import java.io.BufferedOutputStream;

import ar.com.datos.buffer.exception.BufferException;
import ar.com.datos.buffer.variableLength.ArrayByte;

/**
 * @author marcos
 *
 */
public class SimpleInputBuffer implements InputBuffer {
	
	private int pos;
	private byte[] buffer;
	private static int defaultBufferSize = 1024;
	
	public SimpleInputBuffer(){
		this(new byte[defaultBufferSize]);
	}
	public SimpleInputBuffer(byte [] data){
		this.buffer = data;
		this.pos = 0;
	}
	
	/**
	 * @return the size of the buffer
	 */
	public int getBufferSize(){
		return this.buffer.length;
	}
	
	/**
	 * @see ar.com.datos.buffer.InputBuffer#read(byte[])
	 */
	@Override
	public byte[] read(byte[] data) throws BufferException {
		if (this.pos + data.length > this.buffer.length) {
			throw new BufferException("Not enough data to read");
		}
		
		System.arraycopy(this.buffer, this.pos, data, 0, data.length);
		this.pos += data.length;

		return data;
	}

	/**
	 * @see ar.com.datos.buffer.InputBuffer#read()
	 */
	@Override
	public byte read() throws BufferException {
		if (this.pos == this.buffer.length) {
			throw new BufferException("Not enough data to read");
		}
		this.pos++;
		return this.buffer[this.pos - 1];
	}
	public void fill(ArrayByte leftSubArray) {
		// TODO Auto-generated method stub
		
	}
	
}
