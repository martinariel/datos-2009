/**
 * 
 */
package ar.com.datos.buffer.blockWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;

/**
 * Una implementacion simple (pero util) de OutputBuffer.
 * @author marcos
 */
public class SimpleRestrictedOutputBuffer implements RestrictedOutputBuffer {

	private Deque<Collection<ArrayByte>> bufferedEntities;
	private Collection<ArrayByte> lastEntity;
	private Integer bufferSize;
	private int currentSize;
	private RestrictedBufferRealeaser releaser;
	private static Integer defaultBufferSize = 1024;
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer predeterminado.
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleRestrictedOutputBuffer(RestrictedBufferRealeaser releaser){
		this(defaultBufferSize, releaser);
	}
	
	/**
	 * Crea un SimpleOuputBuffer con un size de buffer recibido por parametro.
	 * @param integer size del buffer. 
	 * @param releaser un BufferReleaser para este buffer.
	 */
	public SimpleRestrictedOutputBuffer(Integer bufferSize, RestrictedBufferRealeaser releaser){
		this.currentSize = 0;
		this.bufferSize = bufferSize;
		this.bufferedEntities = new LinkedList<Collection<ArrayByte>>();
		this.lastEntity = new ArrayList<ArrayByte>();
		this.releaser = releaser;
	}	
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#closeEntity()
	 */
	@Override
	public void closeEntity() {
		
		this.bufferedEntities.addLast(lastEntity);
		lastEntity = new ArrayList<ArrayByte>();

		if (this.isOverloaded()) this.releaser.release(this);

	}
	
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#getEntitiesCount()
	 */
	@Override
	public Short getEntitiesCount() {
		return (short)this.bufferedEntities.size();
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
		this.write(new SimpleArrayByte(data));
	}

	/**
	 * @see ar.com.datos.buffer.OutputBuffer#write(byte[])
	 */
	protected void write(ArrayByte data) {
		this.lastEntity.add(data);
		this.currentSize += data.getLength();
	}
	/**
	 * @see ar.com.datos.buffer.OutputBuffer#write(byte)
	 */
	@Override
	public void write(byte data) {
		this.write(new byte[] {data});
	}

	@Override
	public Deque<Collection<ArrayByte>> retrieveEntities() {
		Deque<Collection<ArrayByte>> retorno = this.bufferedEntities;
		this.currentSize = 0;
		this.bufferedEntities = new LinkedList<Collection<ArrayByte>>();
		return retorno;
	}

	@Override
	public void addEntity(Collection<ArrayByte> last) {
		for (ArrayByte ab : last) this.write(ab);
		this.closeEntity();
	}

}
