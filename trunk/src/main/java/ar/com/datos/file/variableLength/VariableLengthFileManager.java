package ar.com.datos.file.variableLength;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.serializer.QueueSerializer;

public class VariableLengthFileManager implements DynamicAccesor, BufferRealeaser {

	private BlockFile realFile;
	private QueueSerializer serializador;
	
	private OutputBuffer lastBlockBuffer;
	private Long lastBlockBufferBlockNumber;
	public VariableLengthFileManager(String nombreArchivo, Integer blockSize, QueueSerializer serializador) {
		setRealFile(constructFile(nombreArchivo, blockSize));
		setSerializador(serializador);
		setLastBlockBufferBlockNumber(0L);
		setLastBlockBuffer(retrieveLastBlock());
	}
	@Override
	public Address<Long, Short> addEntity(Queue<Object> campos) {
		getSerializador().dehydrate(getLastBlockBuffer(), campos);
		getLastBlockBuffer().closeEntity();
		Short s = getLastBlockBuffer().getEntitiesCount() ;
		s-=1;
		return new VariableLengthAddress(getLastBlockBufferBlockNumber(),s);
	}

	@Override
	public Iterator<Collection<Object>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> get(Address<Long, Short> direccion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release(OutputBuffer ob) {
		// TODO Auto-generated method stub
		
	}
	public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
		return new BlockFileImpl(nombreArchivo, blockSize);
	}
	/**
	 * Recupera el último bloque, y lo hidrata en un buffer. En caso que el último bloque
	 * pertenezca a un registro que no está completo o que el archivo esté vacío crea un nuevo
	 * buffer sin datos
	 * @return
	 */
	protected OutputBuffer retrieveLastBlock() {
		if (getRealFile().getTotalBlocks().equals(0L)) return setLastBlockBuffer(constructEmptyOBuffer());
		byte[] bloque = getRealFile().readBlock(getRealFile().getTotalBlocks() - 1);
		Byte cantidadRegistros = bloque[bloque.length-1];
		// En caso que sean varios registros en el bloque ese es el último bloque ya que podría entrar un nuevo registro
		// Caso contrario el último bloque _SIEMPRE_ va a 
		if (cantidadRegistros > 0) {
			this.setLastBlockBufferBlockNumber(getRealFile().getTotalBlocks() - 1);
			return fillLastBlockBufferWith(hidratarVariosRegistros(getRealFile().getTotalBlocks() - 1, bloque), cantidadRegistros);
		}
		else {
			this.setLastBlockBufferBlockNumber(getRealFile().getTotalBlocks());
			return fillLastBlockBufferWith(null, (byte)0);
		}
	}
	/**
	 * El método está implementado de esta manera para 
	 * @param iBuffer
	 * @param cantidadRegistros
	 * @return
	 */
	private OutputBuffer fillLastBlockBufferWith(InputBuffer iBuffer, Byte cantidadRegistros) {
		setLastBlockBuffer(constructEmptyOBuffer());
		for (Byte i = 0; i < cantidadRegistros; i++) {
			this.getSerializador().dehydrate(getLastBlockBuffer(), this.getSerializador().hydrate(iBuffer));
			getLastBlockBuffer().closeEntity();
		}
		return getLastBlockBuffer();
	}
	/**
	 * 
	 * @param blockNumber 
	 * @param bloque
	 * @return
	 */
	private InputBuffer hidratarVariosRegistros(Long blockNumber, byte[] bloque) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Construye un OutputBuffer vacío
	 * @return
	 */
	private OutputBuffer constructEmptyOBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
	protected InputBuffer constructEmptyIBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
	public BlockFile getRealFile() {
		return realFile;
	}
	public void setRealFile(BlockFile realFile) {
		this.realFile = realFile;
	}
	public QueueSerializer getSerializador() {
		return serializador;
	}
	public void setSerializador(QueueSerializer serializador) {
		this.serializador = serializador;
	}
	public OutputBuffer getLastBlockBuffer() {
		return lastBlockBuffer;
	}
	public OutputBuffer setLastBlockBuffer(OutputBuffer lastBlockBuffer) {
		return this.lastBlockBuffer = lastBlockBuffer;
	}
	public Long getLastBlockBufferBlockNumber() {
		return lastBlockBufferBlockNumber;
	}
	public void setLastBlockBufferBlockNumber(Long lastBlockBufferBlockNumber) {
		this.lastBlockBufferBlockNumber = lastBlockBufferBlockNumber;
	}
}
