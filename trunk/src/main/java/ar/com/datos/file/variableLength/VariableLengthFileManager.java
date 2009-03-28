package ar.com.datos.file.variableLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.QueueSerializer;

public class VariableLengthFileManager implements DynamicAccesor, BufferRealeaser {

	private static final Integer POINTER_SIZE = 8;
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
	public Queue<Object> get(Address<Long, Short> direccion) {
		byte[] bloque = getRealFile().readBlock(direccion.getBlockNumber());
		// Para el caso que el registro está en varios bloques me va a decir que no hay registros,
		// pero el inputBuffer finalmente tendrá todo el registro. Así que se corrige la cantidad de registros a uno
		Byte cantidadRegistrosHidratar = bloque[bloque.length-1] == 0? 1 : bloque[bloque.length-1];

		List<Queue<Object>> co = new ArrayList<Queue<Object>>(cantidadRegistrosHidratar);
		InputBuffer data = createInputBuffer(bloque);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			co.add(this.getSerializador().hydrate(data));
		}
		return co.get(direccion.getObjectNumber());
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
		// Caso contrario el último bloque _SIEMPRE_ va a ser un buffer nuevo 
		if (cantidadRegistros > 0) {
			this.setLastBlockBufferBlockNumber(getRealFile().getTotalBlocks() - 1);
			return fillLastBlockBufferWith(createInputBuffer(bloque), cantidadRegistros);
		}
		else {
			this.setLastBlockBufferBlockNumber(getRealFile().getTotalBlocks());
			return fillLastBlockBufferWith(null, Byte.MIN_VALUE);
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
	private InputBuffer createInputBuffer(byte[] bloque) {
		InputBufferImpl ib = constructEmptyIBuffer();
		ArrayByte miArr = new ArrayByte(bloque);
		if (bloque[bloque.length-1] == 0) {
			// Cargo en el input buffer los datos (es decir, saco el puntero al siguiente bloque, porque es un registro de varios
			// Bloques y el último byte que indica que el bloque es del tipo mencionado
			ib.fill(miArr.getLeftSubArray(bloque.length - 1 - POINTER_SIZE));
			Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
			return creatInputBufferMultipleBlocks(ib, proximaDireccion);
		}

		return ib.fill(miArr.getLeftSubArray(bloque.length - 1));
	}
	/**
	 * 
	 * @param buffer 
	 * @param blockNumber 
	 * @param bloque
	 * @param direccionSiguiente 
	 * @return
	 */
	private InputBuffer creatInputBufferMultipleBlocks(InputBufferImpl buffer, Long direccionActual) {
		byte[] bloque = getRealFile().readBlock(direccionActual);
		ArrayByte miArr = new ArrayByte(bloque);
		buffer.fill(miArr.getLeftSubArray(bloque.length - 1 - POINTER_SIZE));
		
		Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
		
		if (proximaDireccion != direccionActual) return creatInputBufferMultipleBlocks(buffer, proximaDireccion);
		
		return buffer;
	}
	/**
	 * Agarra un bloque completo y lee la dirección de las 8 posiciones anteriores al byte que indica si el bloque contiene 0 registros enteros 
	 * @param bloque
	 * @param miArr
	 * @return
	 */
	private Long extraerDireccionDeBloqueCompleto(byte[] bloque, ArrayByte miArr) {
		return PrimitiveTypeSerializer.toLong(miArr.getSubArray(bloque.length - 1 - POINTER_SIZE, bloque.length - 1).getArray());
	}
	/**
	 * Construye un OutputBuffer vacío
	 * @return
	 */
	private OutputBuffer constructEmptyOBuffer() {
		// TODO Auto-generated method stub
		return null;
	}
	protected InputBufferImpl constructEmptyIBuffer() {
		return new InputBufferImpl();
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
