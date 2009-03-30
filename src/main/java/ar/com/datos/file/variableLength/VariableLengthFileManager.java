package ar.com.datos.file.variableLength;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.QueueSerializer;

public class VariableLengthFileManager implements DynamicAccesor, BufferRealeaser {

	private static final Integer POINTER_SIZE = 8;
	private static final Long END_BLOCK = -1L;
	private BlockFile realFile;
	private QueueSerializer serializador;

	private OutputBuffer lastBlockBuffer;
	// Se maneja esté caché por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock cachedLastBlock = null;
	// Implementación simple de caché de un solo bloque
	private HydratedBlock cachedBlock;
	public VariableLengthFileManager(String nombreArchivo, Integer blockSize, QueueSerializer serializador) {
		if (blockSize < (POINTER_SIZE + 2)) throw new InvalidParameterException("block Size Inválido, debe ser mayor a 10");
		setRealFile(constructFile(nombreArchivo, blockSize));
		setSerializador(serializador);
		setLastBlockBuffer(retrieveLastBlock());
	}
	@Override
	public Address<Long, Short> addEntity(Queue<Object> campos) {
		getSerializador().dehydrate(getLastBlockBuffer(), campos);
		getLastBlockBuffer().closeEntity();
		getCachedLastBlock().getDatos().add(campos);
		Short s = getLastBlockBuffer().getEntitiesCount() ;
		s-=1;
		return new VariableLengthAddress(getLastBlockBufferBlockNumber(),s);
	}

	@Override
	public Iterator<Queue<Object>> iterator() {
		return new VLFMIterator(this);
	}

	@Override
	public Queue<Object> get(Address<Long, Short> direccion) {
		return getBlock(direccion.getBlockNumber()).getDatos().get(direccion.getObjectNumber());
	}

	protected HydratedBlock getBlock(Long blockNumber) {
		if (isBlockInCache(blockNumber)) return getBlockFromCache(blockNumber);
		
		byte[] block = getRealFile().readBlock(blockNumber);
		// Para el caso que el registro está en varios bloques me va a decir que no hay registros,
		// pero el inputBuffer finalmente tendrá todo el registro. Así que se corrige la cantidad de registros a uno
		Byte cantidadRegistrosHidratar = block[block.length-1] == 0? 1 : block[block.length-1];

		List<Queue<Object>> co = new ArrayList<Queue<Object>>(cantidadRegistrosHidratar);
		SimpleInputBuffer data = constructEmptyIBuffer();
		Long ultimoBloqueLeido = createInputBuffer(blockNumber, block, data);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			Queue<Object> hidratado = this.getSerializador().hydrate(data); 
			co.add(hidratado);
		}
		HydratedBlock hb = new HydratedBlock(co, blockNumber, ultimoBloqueLeido + 1L);
		addToCache(hb);
		return hb;
	}
	private void addToCache(HydratedBlock hb) {
		this.cachedBlock = hb;
	}
	protected HydratedBlock getBlockFromCache(Long blockNumber) {
		if (blockNumber.equals(getCachedLastBlock().getBlockNumber())) return getCachedLastBlock();
		return this.cachedBlock;
	}
	protected Boolean isBlockInCache(Long blockNumber) {
		return this.getLastBlockBufferBlockNumber().equals(blockNumber) || (this.cachedBlock != null && blockNumber.equals(this.cachedBlock.getBlockNumber()));
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
		if (getRealFile().getTotalBlocks().equals(0L)) {
			this.setCachedLastBlock(new HydratedBlock(new ArrayList<Queue<Object>>(), 0L, END_BLOCK));
			return setLastBlockBuffer(constructEmptyOBuffer());
		}
		byte[] bloque = getRealFile().readBlock(getRealFile().getTotalBlocks() - 1);
		Byte cantidadRegistros = bloque[bloque.length-1];
		// En caso que sean varios registros en el bloque ese es el último bloque ya que podría entrar un nuevo registro
		// Caso contrario el último bloque _SIEMPRE_ va a ser un buffer nuevo 
		if (cantidadRegistros > 0) {
			SimpleInputBuffer ib = constructEmptyIBuffer();
			createInputBuffer(getRealFile().getTotalBlocks() - 1, bloque, ib);
			return fillLastBlockBufferWith(ib, getRealFile().getTotalBlocks() - 1, cantidadRegistros);
		}
		else {
			return fillLastBlockBufferWith(null, getRealFile().getTotalBlocks(), Byte.MIN_VALUE);
		}
	}
	/**
	 * El método está implementado de esta manera para 
	 * @param iBuffer
	 * @param lastBlockNumber
	 * @param cantidadRegistros
	 * @return
	 */
	private OutputBuffer fillLastBlockBufferWith(InputBuffer iBuffer, Long lastBlockNumber, Byte cantidadRegistros) {
		setLastBlockBuffer(constructEmptyOBuffer());
		this.setCachedLastBlock(new HydratedBlock(new ArrayList<Queue<Object>>(), lastBlockNumber, END_BLOCK));
		for (Byte i = 0; i < cantidadRegistros; i++) {
			Queue<Object> hydrate = this.getSerializador().hydrate(iBuffer);
			this.getCachedLastBlock().getDatos().add(hydrate);
			this.getSerializador().dehydrate(getLastBlockBuffer(), hydrate);
			getLastBlockBuffer().closeEntity();
		}
		return getLastBlockBuffer();
	}
	/**
	 * Carga el input buffer con los datos del bloque o bloques
	 * @param blockNumber 
	 * @param bloque
	 * @return último bloque leido
	 */
	private Long createInputBuffer(Long blockNumber, byte[] bloque, SimpleInputBuffer ib) {
		ArrayByte miArr = new ArrayByte(bloque);
		if (bloque[bloque.length-1] == 0) {
			// Cargo en el input buffer los datos (es decir, saco el puntero al siguiente bloque, porque es un registro de varios
			// Bloques y el último byte que indica que el bloque es del tipo mencionado
			ib.fill(miArr.getLeftSubArray(bloque.length - 1 - POINTER_SIZE));
			Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
			return createInputBufferMultipleBlocks(ib, proximaDireccion);
		}
		ib.fill(miArr.getLeftSubArray(bloque.length - 1));
		return blockNumber;
	}
	/**
	 * Carga el inputBuffer recibido con las partes restantes de un registro de multiples bloques 
	 * @param ib 
	 * @param blockNumber 
	 * @param bloque
	 * @param direccionSiguiente 
	 * @return último bloque leido
	 */
	private Long createInputBufferMultipleBlocks(SimpleInputBuffer ib, Long direccionActual) {
		byte[] bloque = getRealFile().readBlock(direccionActual);
		ArrayByte miArr = new ArrayByte(bloque);
		ib.fill(miArr.getLeftSubArray(bloque.length - 1 - POINTER_SIZE));
		
		Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
		
		if (proximaDireccion != direccionActual) return createInputBufferMultipleBlocks(ib, proximaDireccion);
		
		return direccionActual;
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
	protected SimpleInputBuffer constructEmptyIBuffer() {
		return new SimpleInputBuffer();
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
		return getCachedLastBlock().getBlockNumber();
	}
	public HydratedBlock getCachedLastBlock() {
		return cachedLastBlock;
	}
	public void setCachedLastBlock(HydratedBlock cachedLastBlock) {
		this.cachedLastBlock = cachedLastBlock;
	}
	public Boolean isEmpty() {
		return this.getLastBlockBufferBlockNumber() == 0 && getCachedLastBlock().getDatos().isEmpty();
	}
	protected void updateInformation(VLFMIterator iterator) {
		if (iterator.getNextBlock() == END_BLOCK) return;
		HydratedBlock hb = this.getBlock(iterator.getNextBlock());
		iterator.setCachedObjects(hb.getDatos().iterator());
		iterator.setNextBlock(hb.getNextBlockNumber());
		
	}
	/**
	 * Inner class para iterar a este archivo
	 */
	protected class VLFMIterator implements Iterator<Queue<Object>> {
		private VariableLengthFileManager vlfm;
		private Boolean initialized = false;
		private Iterator<Queue<Object>> cachedObjects = null;
		private Long nextBlock = 0L;

		public VLFMIterator(VariableLengthFileManager vlfm) {
			setVLFM(vlfm);
		}
		@Override
		public boolean hasNext() {
			if (!isInitialized()) this.initialize();
			return cachedObjects.hasNext() || nextBlock != END_BLOCK;
		}

		private void initialize() {
			this.getVLFM().updateInformation(this);
			this.setInitialized(true);
		}
		@Override
		public Queue<Object> next() {
			if (!isInitialized()) this.initialize();
			if (this.hasNext() && !cachedObjects.hasNext()) this.getVLFM().updateInformation(this);
			return this.cachedObjects.next();
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}
		public VariableLengthFileManager getVLFM() {
			return vlfm;
		}
		public void setVLFM(VariableLengthFileManager vlfm) {
			this.vlfm = vlfm;
		}
		public Boolean isInitialized() {
			return initialized;
		}
		public void setInitialized(Boolean initialized) {
			this.initialized = initialized;
		}
		public Iterator<Queue<Object>> getCachedObjects() {
			return cachedObjects;
		}
		public void setCachedObjects(Iterator<Queue<Object>> cachedObjects) {
			this.cachedObjects = cachedObjects;
		}
		public Long getNextBlock() {
			return nextBlock;
		}
		public void setNextBlock(Long nextBlock) {
			this.nextBlock = nextBlock;
		}
	}
}
