/**
 * 
 */
package ar.com.datos.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.variableLength.BufferRealeaser;

/**
 * Una implementacion simple (pero util) de OutputBuffer.
 * @author marcos
 */
public class SimpleOutputBuffer implements OverloadableOutputBuffer {

	private Collection<SimpleArrayByte> bufferedEntities;
	private Collection<SimpleArrayByte> lastEntity;
	private Long bufferSize;
	private int currentSize;
	private short nEntities;
	private BufferRealeaser releaser;
	private static Long defaultBufferSize = 1024L;
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer predeterminado.
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleOutputBuffer(BufferRealeaser releaser){
		this(defaultBufferSize, releaser);
	}
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer recibido por parametro.
	 * @param bufferSize size del buffer. 
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleOutputBuffer(Long bufferSize, BufferRealeaser releaser){
		this.currentSize = 0;
		this.bufferSize = bufferSize;
		this.bufferedEntities = new ArrayList<SimpleArrayByte>();
		this.lastEntity = new ArrayList<SimpleArrayByte>();
		this.releaser = releaser;
	}	
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#closeEntity()
	 */
	@Override
	public void closeEntity() {
		this.nEntities++;
		while (this.isOverloaded()) this.releaser.release(this);
		
		Iterator<SimpleArrayByte> it = this.lastEntity.iterator();
		while(it.hasNext()){
			this.bufferedEntities.add(it.next());
		}
		this.lastEntity.clear();
	}
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#extractAllButLast()
	 */
	@Override
	public Collection<SimpleArrayByte> extractAllButLast() {
		ArrayList<SimpleArrayByte> retorno = new ArrayList<SimpleArrayByte>(this.bufferedEntities);
		for (ArrayByte ab : this.bufferedEntities)
			this.currentSize -= ab.getLength();
		this.bufferedEntities.clear();
		this.nEntities = 1;
		return retorno;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#extractLast()
	 */
	@Override
	public Collection<SimpleArrayByte> extractLast() {
		ArrayList<SimpleArrayByte> retorno = new ArrayList<SimpleArrayByte>(this.lastEntity);
		for (ArrayByte ab : this.lastEntity)
			this.currentSize -= ab.getLength();
		this.lastEntity.clear();
		this.nEntities = 0;
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
		this.lastEntity.add(new SimpleArrayByte(data));
		this.currentSize += data.length;
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#write(byte)
	 */
	@Override
	public void write(byte data) {
		byte[] aByte = new byte[1];
		aByte[0] = data;
		this.lastEntity.add(new SimpleArrayByte(aByte));
		this.currentSize += 1;
	}
}
