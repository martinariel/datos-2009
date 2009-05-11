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
@Deprecated
public class SimpleOverloadableOutputBuffer implements OverloadableOutputBuffer {

	private Collection<ArrayByte> bufferedEntities;
	private Collection<ArrayByte> lastEntity;
	private Long bufferSize;
	private int currentSize;
	private short nEntities;
	private BufferRealeaser releaser;
	private static Long defaultBufferSize = 1024L;
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer predeterminado.
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleOverloadableOutputBuffer(BufferRealeaser releaser){
		this(defaultBufferSize, releaser);
	}
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer recibido por parametro.
	 * @param bufferSize size del buffer. 
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleOverloadableOutputBuffer(Long bufferSize, BufferRealeaser releaser){
		this.currentSize = 0;
		this.bufferSize = bufferSize;
		this.bufferedEntities = new ArrayList<ArrayByte>();
		this.lastEntity = new ArrayList<ArrayByte>();
		this.releaser = releaser;
	}	
	
	@Override
	public void closeEntity() {
		this.nEntities++;
		while (this.isOverloaded()) this.releaser.release(this);
		
		Iterator<ArrayByte> it = this.lastEntity.iterator();
		while(it.hasNext()){
			this.bufferedEntities.add(it.next());
		}
		this.lastEntity.clear();
	}
	
	@Override
	public Collection<ArrayByte> extractAllButLast() {
		ArrayList<ArrayByte> retorno = new ArrayList<ArrayByte>(this.bufferedEntities);
		for (ArrayByte ab : this.bufferedEntities)
			this.currentSize -= ab.getLength();
		this.bufferedEntities.clear();
		this.nEntities = 1;
		return retorno;
	}

	@Override
	public Collection<ArrayByte> extractLast() {
		ArrayList<ArrayByte> retorno = new ArrayList<ArrayByte>(this.lastEntity);
		for (ArrayByte ab : this.lastEntity)
			this.currentSize -= ab.getLength();
		this.lastEntity.clear();
		this.nEntities = 0;
		return retorno;
	}

	@Override
	public Short getEntitiesCount() {
		return this.nEntities;
	}

	@Override
	public Boolean isOverloaded() {
		return this.currentSize > this.bufferSize;
	}

	@Override
	public void write(byte[] data) {
		this.lastEntity.add(new SimpleArrayByte(data));
		this.currentSize += data.length;
	}

	@Override
	public void write(byte data) {
		byte[] aByte = new byte[1];
		aByte[0] = data;
		this.lastEntity.add(new SimpleArrayByte(aByte));
		this.currentSize += 1;
	}
}
