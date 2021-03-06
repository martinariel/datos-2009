/**
 * 
 */
package ar.com.datos.buffer;

import ar.com.datos.buffer.exception.BufferException;
import ar.com.datos.buffer.variableLength.ArrayByte;

/**
 * Una implementacion simple (pero no por eso menos util) de InputBuffer. 
 * @author marcos
 */
public class SimpleInputBuffer implements InputBuffer {
	
	private int pos;
	private int posAppend;
	private byte[] buffer;
	private static int defaultBufferSize = 1024;
	
	public SimpleInputBuffer(){
		this(new byte[defaultBufferSize]);
	}
	public SimpleInputBuffer(byte [] data){
		this.pos = 0;
		this.posAppend = 0;
		this.buffer = data;
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
	
	/** 
	 * Carga los datos de array en el buffer. Si se sobrepasa el tamaño del 
	 * buffer, este se expande para que sea capaz de almacenar lo pasado.
	 */
	public void append(ArrayByte array) {
		if (array.getLength() > (this.getBufferSize() - this.posAppend)){
			byte[] newBuffer = new byte[this.getBufferSize()+array.getLength()];
			System.arraycopy(this.buffer, 0, newBuffer, 0, this.posAppend);
			this.buffer = newBuffer;
		}
		System.arraycopy(array.getArray(), 0, this.buffer, this.posAppend, array.getLength());
		this.posAppend += array.getLength();
	}
}
