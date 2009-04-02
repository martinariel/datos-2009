/**
 * 
 */
package ar.com.datos.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.variableLength.BufferRealeaser;

/**
 * Una implementacion simple (pero util) de OutputBuffer.
 * @author marcos
 */
public class SimpleOutputBuffer implements OutputBuffer {

	private Collection<ArrayByte> bufferedEntities;
	private Collection<ArrayByte> lastEntity;
	private Long bufferSize;
	private int currentSize;
	private short nEntities;
	private BufferRealeaser releaser;
	private static Long defaultBufferSize = 1024L;
	
	public SimpleOutputBuffer(BufferRealeaser releaser){
		this(defaultBufferSize, releaser);
	}
	public SimpleOutputBuffer(Long bufferSize, BufferRealeaser releaser){
		this.currentSize = 0;
		this.bufferSize = bufferSize;
		this.bufferedEntities = new ArrayList<ArrayByte>();
		this.lastEntity = new ArrayList<ArrayByte>();
		this.releaser = releaser;
	}	
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#closeEntity()
	 */
	@Override
	public void closeEntity() {
		while (this.isOverloaded()) this.releaser.release(this);
		
		Iterator<ArrayByte> it = this.lastEntity.iterator();
		while(it.hasNext()){
			this.bufferedEntities.add(it.next());
		}
		this.lastEntity.clear();
		this.nEntities++;
	}
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#extractAllButLast()
	 */
	@Override
	public Collection<ArrayByte> extractAllButLast() {
		ArrayList<ArrayByte> retorno = new ArrayList<ArrayByte>(this.bufferedEntities);
		for (ArrayByte ab : this.bufferedEntities)
			this.bufferSize -= ab.getLength();
		this.bufferedEntities.clear();
		return retorno;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#extractLast()
	 */
	@Override
	public Collection<ArrayByte> extractLast() {
		ArrayList<ArrayByte> retorno = new ArrayList<ArrayByte>(this.lastEntity);
		for (ArrayByte ab : this.lastEntity)
			this.bufferSize -= ab.getLength();
		this.lastEntity.clear();
		return retorno;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#getEntitiesCount()
	 */
	@Override
	public Short getEntitiesCount() {
		return this.nEntities;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#isOverloaded()
	 */
	@Override
	public Boolean isOverloaded() {
		return this.currentSize > this.bufferSize;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#write(byte[])
	 */
	@Override
	public void write(byte[] data) {
		this.lastEntity.add(new ArrayByte(data));
		this.currentSize += data.length;
		
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#write(byte)
	 */
	@Override
	public void write(byte data) {
		
	}

}
