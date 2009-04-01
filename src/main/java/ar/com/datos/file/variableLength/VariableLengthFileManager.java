package ar.com.datos.file.variableLength;

import java.lang.reflect.ParameterizedType;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.buffer.SimpleOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.file.exception.ValidacionIncorrectaException;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializable;
import ar.com.datos.serializer.Serializer;

public class VariableLengthFileManager<T> implements DynamicAccesor<T>, BufferRealeaser {

	private static final Integer POINTER_SIZE = 8;
	private static final Long END_BLOCK = -1L;
	private BlockFile realFile;
	private Serializer<T> serializador;

	private OutputBuffer lastBlockBuffer;
	// Se maneja este cache por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock<T> cachedLastBlock = null;
	// Implementacion simple de cache de un solo bloque
	private HydratedBlock<T> cachedBlock;
	
	/**
	 * Permite obtener el tipo parametrizado de este objeto. 
	 */
	private Class<T> getReturnClass(){
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<T>)parameterizedType.getActualTypeArguments()[0];
	}

	/**
	 * Permite obtener el Serializer de manera automatica desde el ParameterizedType.
	 * (Esto es posible porque el ParameterizedType es un Serializable).
	 */
	@SuppressWarnings("unchecked")
	private Serializer<T> getSerializerFromParameterizedType() {
		Class<T> parameterizedType = getReturnClass();
		Serializable<T> instance;
		try {
			instance = (Serializable<T>)parameterizedType.newInstance();
		} catch (Exception e) {
			throw new ValidacionIncorrectaException("Debe parametrizarse con una clase concreta con un constructor sin parametros que implemente Serializable o usar el otro constructor");
		}
		return instance.getSerializer();
    }
    

	/**
	 * Permite crear una instancia indicando cual es el serializador a usar.
	 *
	 * TODO: Solo explico lo del Serializador. Completar el resto del javadoc.
	 */
    public VariableLengthFileManager(String nombreArchivo, Integer blockSize, Serializer<T> serializador) {
		if (blockSize < (POINTER_SIZE + 2)) throw new InvalidParameterException("block Size Invalido, debe ser mayor a 10");
		setRealFile(constructFile(nombreArchivo, blockSize));
		if (serializador == null) {
			serializador = getSerializerFromParameterizedType();
		}
		setSerializador(serializador);
		setLastBlockBuffer(retrieveLastBlock());
	}
	
	/**
	 * Permite crear una instancia cuyo serializador sera obtenido a partir del 
	 * {@link Serializable#getSerializer()} de la clase parametrizada. Por tanto,
	 * para poder usar este constructor la clase parametrizada debe implementar
	 * {@link Serializable} y poseer un constructor sin parametros.
	 *
	 * TODO: Solo explico lo del Serializador. Completar el resto del javadoc.
	 */
    public VariableLengthFileManager(String nombreArchivo, Integer blockSize) {
    	this(nombreArchivo, blockSize, null);
	}
    
	@Override
	public Address<Long, Short> addEntity(T dato) {
		getSerializador().dehydrate(getLastBlockBuffer(), dato);
		getLastBlockBuffer().closeEntity();
		getCachedLastBlock().getDatos().add(dato);
		Short s = getLastBlockBuffer().getEntitiesCount() ;
		s-=1;
		return new VariableLengthAddress(getLastBlockBufferBlockNumber(),s);
	}

	@Override
	public Iterator<T> iterator() {
		return new VLFMIterator(this);
	}

	@Override
	public T get(Address<Long, Short> direccion) {
		return getBlock(direccion.getBlockNumber()).getDatos().get(direccion.getObjectNumber());
	}

	protected HydratedBlock<T> getBlock(Long blockNumber) {
		if (isBlockInCache(blockNumber)) return getBlockFromCache(blockNumber);
		
		byte[] block = getRealFile().readBlock(blockNumber);
		// Para el caso que el registro esta en varios bloques me va a decir que no hay registros,
		// pero el inputBuffer finalmente tendra todo el registro. Asi­ que se corrige la cantidad de registros a uno
		Byte cantidadRegistrosHidratar = block[block.length-1] == 0? 1 : block[block.length-1];

		List<T> li = new ArrayList<T>(cantidadRegistrosHidratar);
		SimpleInputBuffer data = constructEmptyIBuffer();
		Long ultimoBloqueLeido = createInputBuffer(blockNumber, block, data);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			T hidratado = this.getSerializador().hydrate(data); 
			li.add(hidratado);
		}
		HydratedBlock<T> hb = new HydratedBlock<T>(li, blockNumber, ultimoBloqueLeido + 1L);
		addToCache(hb);
		return hb;
	}
	private void addToCache(HydratedBlock<T> hb) {
		this.cachedBlock = hb;
	}
	protected HydratedBlock<T> getBlockFromCache(Long blockNumber) {
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
		return new SimpleBlockFile(nombreArchivo, blockSize);
	}
	/**
	 * Recupera el ultimo bloque, y lo hidrata en un buffer. En caso que el ultimo bloque
	 * pertenezca a un registro que no esta completo o que el archivo esta vaci­o crea un nuevo
	 * buffer sin datos
	 * @return
	 */
	protected OutputBuffer retrieveLastBlock() {
		if (getRealFile().getTotalBlocks().equals(0L)) {
			this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), 0L, END_BLOCK));
			return setLastBlockBuffer(constructEmptyOBuffer());
		}
		byte[] bloque = getRealFile().readBlock(getRealFile().getTotalBlocks() - 1);
		Byte cantidadRegistros = bloque[bloque.length-1];
		// En caso que sean varios registros en el bloque ese es el ultimo bloque ya que podri­a entrar un nuevo registro
		// Caso contrario el ultimo bloque _SIEMPRE_ va a ser un buffer nuevo 
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
	 * El metodo esta implementado de esta manera para 
	 * @param iBuffer
	 * @param lastBlockNumber
	 * @param cantidadRegistros
	 * @return
	 */
	private OutputBuffer fillLastBlockBufferWith(InputBuffer iBuffer, Long lastBlockNumber, Byte cantidadRegistros) {
		setLastBlockBuffer(constructEmptyOBuffer());
		this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), lastBlockNumber, END_BLOCK));
		for (Byte i = 0; i < cantidadRegistros; i++) {
			T hydrate = this.getSerializador().hydrate(iBuffer);
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
	 * @return ultimo bloque leido
	 */
	private Long createInputBuffer(Long blockNumber, byte[] bloque, SimpleInputBuffer ib) {
		ArrayByte miArr = new ArrayByte(bloque);
		if (bloque[bloque.length-1] == 0) {
			// Cargo en el input buffer los datos (es decir, saco el puntero al siguiente bloque, porque es un registro de varios
			// Bloques y el ultimo byte que indica que el bloque es del tipo mencionado
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
	 * @return ultimo bloque leido
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
	 * Agarra un bloque completo y lee la direccion de las 8 posiciones anteriores al byte que indica si el bloque contiene 0 registros enteros 
	 * @param bloque
	 * @param miArr
	 * @return
	 */
	private Long extraerDireccionDeBloqueCompleto(byte[] bloque, ArrayByte miArr) {
		return PrimitiveTypeSerializer.toLong(miArr.getSubArray(bloque.length - 1 - POINTER_SIZE, bloque.length - 1).getArray());
	}
	/**
	 * Construye un OutputBuffer vacio
	 * @return
	 */
	private OutputBuffer constructEmptyOBuffer() {
		return new SimpleOutputBuffer(this);
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
	public Serializer<T> getSerializador() {
		return serializador;
	}
	public void setSerializador(Serializer<T> serializador) {
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
	public HydratedBlock<T> getCachedLastBlock() {
		return cachedLastBlock;
	}
	public void setCachedLastBlock(HydratedBlock<T> cachedLastBlock) {
		this.cachedLastBlock = cachedLastBlock;
	}
	public Boolean isEmpty() {
		return this.getLastBlockBufferBlockNumber() == 0 && getCachedLastBlock().getDatos().isEmpty();
	}
	protected void updateInformation(VLFMIterator iterator) {
		if (iterator.getNextBlock() == END_BLOCK) return;
		HydratedBlock<T> hb = this.getBlock(iterator.getNextBlock());
		iterator.setCachedObjects(hb.getDatos().iterator());
		iterator.setNextBlock(hb.getNextBlockNumber());
		
	}
	/**
	 * Inner class para iterar a este archivo
	 */
	protected class VLFMIterator implements Iterator<T> {
		private VariableLengthFileManager<T> vlfm;
		private Boolean initialized = false;
		private Iterator<T> cachedObjects = null;
		private Long nextBlock = 0L;

		public VLFMIterator(VariableLengthFileManager<T> vlfm) {
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
		public T next() {
			if (!isInitialized()) this.initialize();
			if (this.hasNext() && !cachedObjects.hasNext()) this.getVLFM().updateInformation(this);
			return this.cachedObjects.next();
		}

		@Override
		public void remove() {
			throw new NotImplementedException();
		}
		public VariableLengthFileManager<T> getVLFM() {
			return vlfm;
		}
		public void setVLFM(VariableLengthFileManager<T> vlfm) {
			this.vlfm = vlfm;
		}
		public Boolean isInitialized() {
			return initialized;
		}
		public void setInitialized(Boolean initialized) {
			this.initialized = initialized;
		}
		public Iterator<T> getCachedObjects() {
			return cachedObjects;
		}
		public void setCachedObjects(Iterator<T> cachedObjects) {
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
