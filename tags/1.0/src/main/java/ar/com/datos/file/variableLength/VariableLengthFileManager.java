package ar.com.datos.file.variableLength;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import java.lang.UnsupportedOperationException;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.buffer.SimpleOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.serializer.PrimitiveTypeSerializer;
import ar.com.datos.serializer.Serializer;
/**
 * Esta entidad permite manejar la persistencia de objetos Serializables en un archivo de longitud variable.
 * Al momento de almacenar dicho objeto se devolvera un Address para poder recuperar al mismo
 * @author jbarreneche
 *
 * @param <T> tipo de objetos a persistir y o recuperar
 */
public class VariableLengthFileManager<T> implements DynamicAccesor<T>, BufferRealeaser{

	// Longitud del puntero al siguiente bloque en caso que un registro ocupe varios bloques
	private static final Integer INNER_BLOCK_POINTER_SIZE = 8;
	// Longitud de la marca de cantidad de registros
	private static final Integer CANTIDAD_REGISTROS_SIZE = 2;
	// Marca que indica que no existe un siguiente bloque persistido
	private static final Long END_BLOCK = -1L;
	
	// Referencia al archivo real donde se realiza la persistencia
	private BlockFile realFile;
	// Serializador utilizado para hidratar y deshidratar objetos
	private Serializer<T> serializador;

	// Buffers
	
	// Buffer de salida que contiene el ultimo bloque del archivo o el nuevo a persistir 
	private OutputBuffer lastBlockBuffer;
	// Se maneja este cache por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock<T> cachedLastBlock = null;
	// Implementacion simple de cache de un solo bloque
	private HydratedBlock<T> cachedBlock;
	private Address<Long, Short> lastMultipleBlockAddress = null;
	

	/**
	 * Permite crear una instancia indicando cual es el serializador a usar en lugar de usar el nativo de los objetos de tipo T
	 * 
     * @param fileName nombre de
     * @param blockSize
     * @param blockSize
	 */
    public VariableLengthFileManager(String nombreArchivo, Integer blockSize, Serializer<T> serializador) {
    	// Si no puedo almacenar al menos un byte por registro en los casos que el registro excede el tamaño del bloque
    	// nunca podrÃ­a almacenar dicho registro. AsÃ­ que no tiene sentido un archivo de ese tamaño de blocksize
		if (blockSize < (INNER_BLOCK_POINTER_SIZE + CANTIDAD_REGISTROS_SIZE + 1)) throw new InvalidParameterException("block Size Invalido, debe ser mayor a 10");
		setRealFile(constructFile(nombreArchivo, blockSize));
		setSerializador(serializador);
		setLastBlockBuffer(retrieveLastBlock());
	}

	@Override
	public Address<Long, Short> addEntity(T dato) {
		getSerializador().dehydrate(getLastBlockBuffer(), dato);
		getCachedLastBlock().getData().add(dato);
		getLastBlockBuffer().closeEntity();
		if (getCachedLastBlock().getData().size() == 0) return getLastMultipleBlockAddress();
		short s = getLastBlockBuffer().getEntitiesCount() ;
		s--;
		return new VariableLengthAddress(getLastBlockBufferBlockNumber(),s);
	}

	/**
	 * Devuelve un iterador que permite recorrer todos los objetos persistidos por esta entidad
	 */
	@Override
	public Iterator<T> iterator() {
		return new VLFMIterator(this);
	}

	@Override
	public T get(Address<Long, Short> direccion) {
		return getBlock(direccion.getBlockNumber()).getData().get(direccion.getObjectNumber());
	}

	/**
	 * recupera un bloque hidratado con objetos. Puede obtenerlos de la cache {@link}
	 * @param blockNumber
	 * @return
	 */
	protected HydratedBlock<T> getBlock(Long blockNumber) {
		if (isBlockInCache(blockNumber)) return getBlockFromCache(blockNumber);
		
		byte[] block = getRealFile().readBlock(blockNumber);
		// Para el caso que el registro esta en varios bloques me va a decir que no hay registros,
		// pero el inputBuffer finalmente tendra todo el registro. Asi que se corrige la cantidad de registros a uno
		short cantidadRegistrosHidratar = getCantidadRegistros(block);
		if (cantidadRegistrosHidratar == 0) cantidadRegistrosHidratar ++;

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
	/**
	 * Manejo basico de cache
	 * @param hb
	 */
	protected void addToCache(HydratedBlock<T> hb) {
		this.cachedBlock = hb;
	}
	protected HydratedBlock<T> getBlockFromCache(Long blockNumber) {
		if (blockNumber.equals(getCachedLastBlock().getBlockNumber())) return getCachedLastBlock();
		return this.cachedBlock;
	}
	protected Boolean isBlockInCache(Long blockNumber) {
		return this.getLastBlockBufferBlockNumber().equals(blockNumber) || (this.cachedBlock != null && blockNumber.equals(this.cachedBlock.getBlockNumber()));
	}
	/**
	 * Libera al OutputBuffer cuando el tamanio de los datos que esta manejando exceden el tamanio del bloque
	 */
	@Override
	public void release(OutputBuffer ob) {
		short cantidadObjetos = ob.getEntitiesCount();
		List<T> c = new ArrayList<T>();
		if (cantidadObjetos > 1) {
			cantidadObjetos --;
			c.add(getCachedLastBlock().getData().get(getCachedLastBlock().getData().size() - 1));
			writeEntitiesInOneBlock(getCachedLastBlock().getBlockNumber(), ob.extractAllButLast(), cantidadObjetos);
		} else { 
			setLastMultipleBlockAddress(new VariableLengthAddress(getCachedLastBlock().getBlockNumber(), (short)0));
			writeOneEntityInMultipleBlocks(ob.extractLast());
		}
		setCachedLastBlock(new HydratedBlock<T>(c, this.getRealFile().getTotalBlocks(), END_BLOCK));
	}
	/**
	 * Resuelve la escritura de un solo bloque con varios registros
	 * @param partes
	 * @param cantidadObjetos
	 */
	protected void writeEntitiesInOneBlock(Long blockNumber, Collection<ArrayByte> partes, Short cantidadObjetos) {
		Integer resto = getRealFile().getBlockSize() - CANTIDAD_REGISTROS_SIZE;
		// Reduzco el resto para ver cuanto espacio sin utilizar quedñ en la entidad
		for (ArrayByte ab : partes) resto -= ab.getLength();
		if (resto > 0) partes.add(new ArrayByte(new byte[resto]));
		partes.add(new ArrayByte(PrimitiveTypeSerializer.toByte(cantidadObjetos)));
		getRealFile().writeBlock(blockNumber, partes);
	}

	protected void writeOneEntityInMultipleBlocks(Collection<ArrayByte> extractLast) {
		Collection<Collection<ArrayByte>> segmentos = partirEnSegmentos(extractLast);
		Short cero = 0;
		Iterator<Collection<ArrayByte>> it = segmentos.iterator();
		// Agrego a los datos la información de control
		while(it.hasNext()) {
			Collection<ArrayByte> segmento = it.next();
			Long proximoBloque = getRealFile().getTotalBlocks();
			if (it.hasNext()) proximoBloque += 1;
			segmento.add(new ArrayByte(PrimitiveTypeSerializer.toByte(proximoBloque)));
			segmento.add(new ArrayByte(PrimitiveTypeSerializer.toByte(cero)));
			getRealFile().writeBlock(getRealFile().getTotalBlocks(), segmento);
		}
		
	}

	/**
	 * Corta en pedazos de igual tamaño igual al tamaño de datos para registros que se encuentran en varios bloques
	 * rellenando con bytes vací­os el ultimo segmento
	 * @param extractLast
	 * @return
	 */
	private Collection<Collection<ArrayByte>> partirEnSegmentos(Collection<ArrayByte> extractLast) {
		Integer tamanioSegmentos = getRealFile().getBlockSize() - CANTIDAD_REGISTROS_SIZE - INNER_BLOCK_POINTER_SIZE;
		Integer sumaParcial = 0;
		Collection<ArrayByte> pedacitoActual = new ArrayList<ArrayByte>();
		Collection<Collection<ArrayByte>> pedacitos = new ArrayList<Collection<ArrayByte>>();
		for (ArrayByte ab: extractLast) {
			if ((ab.getLength() + sumaParcial) > tamanioSegmentos) {
				pedacitoActual.add(ab.getLeftSubArray(tamanioSegmentos - sumaParcial));
				pedacitos.add(pedacitoActual);
				
				ArrayByte resto = ab.getRightSubArray(tamanioSegmentos - sumaParcial);
				pedacitoActual = new ArrayList<ArrayByte>();
				while (resto.getLength() > tamanioSegmentos) {
					pedacitoActual.add(resto.getLeftSubArray(tamanioSegmentos));
					pedacitos.add(pedacitoActual);
					resto = resto.getRightSubArray(tamanioSegmentos);
					pedacitoActual = new ArrayList<ArrayByte>();
				}
				sumaParcial = resto.getLength();
				if (resto.getLength() > 0) {
					pedacitoActual.add(resto);
				}
			} else {
				pedacitoActual.add(ab);
				sumaParcial += ab.getLength();
			}
		}
		if (sumaParcial > 0) {
			if (sumaParcial < tamanioSegmentos) {
				pedacitoActual.add(new ArrayByte(new byte[tamanioSegmentos - sumaParcial]));
			}
			pedacitos.add(pedacitoActual);
		}
		return pedacitos;
	}

	/**
	 * Construye el archivo de bloques que a utilizar para la persistencia 
	 * @param nombreArchivo
	 * @param blockSize
	 * @return
	 */
	protected BlockFile constructFile(String nombreArchivo, Integer blockSize) {
		return new SimpleBlockFile(nombreArchivo, blockSize);
	}
	/**
	 * Recupera el ultimo bloque, y lo hidrata en un buffer. En caso que el ultimo bloque
	 * pertenezca a un registro que no esta completo o que el archivo esta vacio crea un nuevo
	 * buffer sin datos
	 * @return
	 */
	protected OutputBuffer retrieveLastBlock() {
		if (getRealFile().getTotalBlocks().equals(0L)) {
			this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), 0L, END_BLOCK));
			return setLastBlockBuffer(constructEmptyOBuffer());
		}
		byte[] bloque = getRealFile().readBlock(getRealFile().getTotalBlocks() - 1);
		Short cantidadRegistros = getCantidadRegistros(bloque);
		// En caso que sean varios registros en el bloque ese es el ultimo bloque ya que podria entrar un nuevo registro
		// Caso contrario el ultimo bloque _SIEMPRE_ va a ser un buffer nuevo 
		if (cantidadRegistros > 0) {
			SimpleInputBuffer ib = constructEmptyIBuffer();
			createInputBuffer(getRealFile().getTotalBlocks() - 1, bloque, ib);
			return fillLastBlockBufferWith(ib, getRealFile().getTotalBlocks() - 1, cantidadRegistros);
		} else {
			return fillLastBlockBufferWith(null, getRealFile().getTotalBlocks(), cantidadRegistros);
		}
	}
	/**
	 * Recupera de un bloque completo la cantidad de registros
	 * @param bloque
	 * @return
	 */
	private Short getCantidadRegistros(byte[] bloque) {
		return  PrimitiveTypeSerializer.toShort(new byte[] { bloque[bloque.length-2],  bloque[bloque.length-1]});
	}

	/**
	 * El metodo esta implementado de esta manera para 
	 * @param iBuffer
	 * @param lastBlockNumber
	 * @param cantidadRegistros
	 * @return
	 */
	private OutputBuffer fillLastBlockBufferWith(InputBuffer iBuffer, Long lastBlockNumber, Short cantidadRegistros) {
		setLastBlockBuffer(constructEmptyOBuffer());
		this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), lastBlockNumber, END_BLOCK));
		for (Short i = 0; i < cantidadRegistros; i++) {
			T hydrate = this.getSerializador().hydrate(iBuffer);
			this.getCachedLastBlock().getData().add(hydrate);
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
		if (getCantidadRegistros(bloque) == 0) {
			// Cargo en el input buffer los datos (es decir, saco el puntero al siguiente bloque, porque es un registro de varios
			// Bloques y el ultimo byte que indica que el bloque es del tipo mencionado
			ib.append(miArr.getLeftSubArray(bloque.length - CANTIDAD_REGISTROS_SIZE - INNER_BLOCK_POINTER_SIZE));
			Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
			return createInputBufferMultipleBlocks(ib, proximaDireccion);
		}
		ib.append(miArr.getLeftSubArray(bloque.length - CANTIDAD_REGISTROS_SIZE));
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
		ib.append(miArr.getLeftSubArray(bloque.length - CANTIDAD_REGISTROS_SIZE - INNER_BLOCK_POINTER_SIZE));
		
		Long proximaDireccion = extraerDireccionDeBloqueCompleto(bloque, miArr);
		
		if (!proximaDireccion.equals(direccionActual)) return createInputBufferMultipleBlocks(ib, proximaDireccion);
		
		return direccionActual;
	}
	/**
	 * Agarra un bloque completo y lee la direccion de las 8 posiciones anteriores al byte que indica si el bloque contiene 0 registros enteros 
	 * @param bloque
	 * @param miArr
	 * @return
	 */
	private Long extraerDireccionDeBloqueCompleto(byte[] bloque, ArrayByte miArr) {
		return PrimitiveTypeSerializer.toLong(miArr.getSubArray(bloque.length - CANTIDAD_REGISTROS_SIZE - INNER_BLOCK_POINTER_SIZE, bloque.length - CANTIDAD_REGISTROS_SIZE).getArray());
	}
	/**
	 * Construye un OutputBuffer vacio
	 * @return
	 */
	private OutputBuffer constructEmptyOBuffer() {
		return new SimpleOutputBuffer(getRealFile().getBlockSize() - CANTIDAD_REGISTROS_SIZE.longValue(), this);
	}
	protected SimpleInputBuffer constructEmptyIBuffer() {
		return new SimpleInputBuffer();
	}
	protected BlockFile getRealFile() {
		return realFile;
	}
	protected void setRealFile(BlockFile realFile) {
		this.realFile = realFile;
	}
	protected Serializer<T> getSerializador() {
		return serializador;
	}
	protected void setSerializador(Serializer<T> serializador) {
		this.serializador = serializador;
	}
	protected OutputBuffer getLastBlockBuffer() {
		return lastBlockBuffer;
	}
	protected OutputBuffer setLastBlockBuffer(OutputBuffer lastBlockBuffer) {
		return this.lastBlockBuffer = lastBlockBuffer;
	}
	protected Long getLastBlockBufferBlockNumber() {
		return getCachedLastBlock().getBlockNumber();
	}
	protected HydratedBlock<T> getCachedLastBlock() {
		return cachedLastBlock;
	}
	protected void setCachedLastBlock(HydratedBlock<T> cachedLastBlock) {
		this.cachedLastBlock = cachedLastBlock;
	}
	@Override
	public Boolean isEmpty() {
		return this.getLastBlockBufferBlockNumber() == 0 && getCachedLastBlock().getData().isEmpty();
	}
	/**
	 * Actualiza la información del iterador que itera sobre este archivo
	 * @param iterator
	 */
	protected void updateInformation(VLFMIterator iterator) {
		if (iterator.getNextBlock() == END_BLOCK) return;
		HydratedBlock<T> hb = this.getBlock(iterator.getNextBlock());
		iterator.setCachedObjects(hb.getData().iterator());
		Long nextBlockNumber = hb.getNextBlockNumber();
		if (nextBlockNumber.equals(this.getCachedLastBlock().getBlockNumber()) && this.getCachedLastBlock().getData().size() ==  0) {
			iterator.setNextBlock(END_BLOCK);
		} else {
			iterator.setNextBlock(nextBlockNumber);
		}
		
	}
	protected void setLastMultipleBlockAddress(Address<Long, Short> lastMultipleBlockAddress) {
		this.lastMultipleBlockAddress = lastMultipleBlockAddress;
	}

	/**
	 * Propiedad donde se almacena la dirección en caso de que un registro agregado
	 * ocupe múltiples bloques
	 * @return
	 */
	protected Address<Long, Short> getLastMultipleBlockAddress() {
		return lastMultipleBlockAddress;
	}
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
	@Override
	public void close() throws IOException {
		Short cantidadObjetos = this.getLastBlockBuffer().getEntitiesCount();
		if (cantidadObjetos > 0) {
			List<T> c = new ArrayList<T>();
			c.add(getCachedLastBlock().getData().get(getCachedLastBlock().getData().size() - 1));
			Collection<ArrayByte> todos = this.getLastBlockBuffer().extractAllButLast();
			todos.addAll(this.getLastBlockBuffer().extractLast());
			writeEntitiesInOneBlock(getCachedLastBlock().getBlockNumber(), todos, cantidadObjetos);
		}
		this.getRealFile().close();
	}
	/**
	 * Inner class para iterar a este archivo
	 * La clase cada vez que no tiene objetos en su caché interna le pide
	 * al VLFM que lo actualice.
	 * Por cada actualización el VLFM le deja a este iterador una cierta cantidad de entidades
	 * sobre las cuales puede iterarar. y el próximo bloque (que necesita el VLFM para poder
	 * actualizar la caché en la próxima iteración) 
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
			throw new UnsupportedOperationException();
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
