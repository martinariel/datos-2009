package ar.com.datos.file.variableLength;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.persistencia.variableLength.BlockWriter;
import ar.com.datos.persistencia.variableLength.FlushListener;
import ar.com.datos.persistencia.variableLength.NotHeadException;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
/**
 * Esta entidad permite manejar la persistencia de objetos Serializables en un archivo de longitud variable.
 * Al momento de almacenar dicho objeto se devolvera un Address para poder recuperar al mismo
 * @author jbarreneche
 *
 * @param <T> tipo de objetos a persistir y o recuperar
 */
public class VariableLengthFileManager<T> implements DynamicAccesor<T>{

	private static final LongSerializer LONG_SERIALIZER = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	private static final ShortSerializer SHORT_SERIALIZER = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	// Longitud del puntero al siguiente bloque en caso que un registro ocupe varios bloques
	private static final Integer INNER_BLOCK_POINTER_SIZE = new Long(LONG_SERIALIZER.getDehydrateSize(0L)).intValue();
	// Longitud de la marca de cantidad de registros
	private static final Integer CANTIDAD_REGISTROS_SIZE = new Long(SHORT_SERIALIZER.getDehydrateSize((short)0)).intValue();
	// Marca que indica que no existe un siguiente bloque persistido
	
	// Referencia al archivo real donde se realiza la persistencia
	private BlockFile realFile;
	// Serializador utilizado para hidratar y deshidratar objetos
	private Serializer<T> serializador;

	private BlockReader blockReader;
	private BlockWriter lastBlockWriter;
	// Buffers
	
	// Se maneja este cache por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock<T> cachedLastBlock = null;
	// Implementacion simple de cache de un solo bloque
	private HydratedBlock<T> cachedBlock;
	private boolean firstTime = true;
	
	/**
	 * Permite crear una instancia indicando cual es el serializador a usar en lugar de usar el nativo de los objetos de tipo T
	 * 
     * @param fileName nombre de
     * @param blockSize
     * @param blockSize
	 */
    public VariableLengthFileManager(String fileName, Integer blockSize, Serializer<T> entitySerializer) {
    	// Si no puedo almacenar al menos un byte por registro en los casos que el registro excede el tamaño del bloque
    	// nunca podrí­a almacenar dicho registro. Así que no tiene sentido un archivo de ese tamaño de blocksize
		if (blockSize < (INNER_BLOCK_POINTER_SIZE + CANTIDAD_REGISTROS_SIZE + 1)) throw new InvalidParameterException("InvalidBlockSize, blockSize must be greater than 10");
		setRealFile(constructFile(fileName, blockSize));
		setEntitySerializer(entitySerializer);
		setBlockReader(new BlockReader(this.getRealFile()));
		this.lastBlockWriter = new BlockWriter(this.getRealFile());
		this.lastBlockWriter.addFlushListener(new FlushListener() {

			@Override
			public void flushed() {
				cachedLastBlock.setBlockNumber(lastBlockWriter.getCurrentWrittingBlock());
				while (lastBlockWriter.getOutputBuffer().getEntitiesCount() < cachedLastBlock.getData().size()) {
					cachedLastBlock.getData().remove(0);
				}
			}
			
		});
	}

	@Override
	public Address<Long, Short> addEntity(T data) {
		if (this.firstTime) loadLastBlockWriter();
		getSerializador().dehydrate(this.lastBlockWriter.getOutputBuffer(), data);
		getCachedLastBlock().getData().add(data);
		this.lastBlockWriter.getOutputBuffer().closeEntity();
		return new VariableLengthAddress(this.lastBlockWriter.getCurrentWrittingBlock(),this.lastBlockWriter.getCurrentWrittingEntityNumber());
	}

	private void loadLastBlockWriter() {
		// TODO Auto-generated method stub
		
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

		getBlockReader().readBlock(blockNumber);
		
		if (!getBlockReader().isBlockHead()) throw new NotHeadException("Se pidió una lectura de bloque que no es Head");
		
		InputBuffer data = getBlockReader().getData();
		Integer cantidadRegistrosHidratar = getBlockReader().getRegistryCount();
		List<T> li = new ArrayList<T>(cantidadRegistrosHidratar);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			T hidratado = this.getSerializador().hydrate(data); 
			li.add(hidratado);
		}
		HydratedBlock<T> hb = new HydratedBlock<T>(li, blockNumber, getBlockReader().getNextBlockNumber());
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
		if (blockNumber.equals(this.getLastBlockBufferBlockNumber())) return getCachedLastBlock();
		return this.cachedBlock;
	}
	protected Boolean isBlockInCache(Long blockNumber) {
		return blockNumber.equals(this.getLastBlockBufferBlockNumber()) || (this.cachedBlock != null && blockNumber.equals(this.cachedBlock.getBlockNumber()));
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
	 * El metodo esta implementado de esta manera para 
	 * @param iBuffer
	 * @param lastBlockNumber
	 * @param cantidadRegistros
	 * @return
	 */
	private void fillLastBlockBufferWith(InputBuffer iBuffer, Long lastBlockNumber, Integer cantidadRegistros) {
		this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), lastBlockNumber, BlockFile.END_BLOCK));
		if (lastBlockNumber != null) {
			for (Integer i = 0; i < cantidadRegistros; i++) {
				T hydrate = this.getSerializador().hydrate(iBuffer);
				this.getCachedLastBlock().getData().add(hydrate);
				this.getSerializador().dehydrate(this.lastBlockWriter.getOutputBuffer(), hydrate);
				this.lastBlockWriter.getOutputBuffer().closeEntity();
			}
			this.lastBlockWriter.addAvailableBlock(lastBlockNumber);
		}
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
	protected void setEntitySerializer(Serializer<T> serializador) {
		this.serializador = serializador;
	}
	protected Long getLastBlockBufferBlockNumber() {
		return this.lastBlockWriter.getCurrentWrittingBlock();
	}
	protected HydratedBlock<T> getCachedLastBlock() {
		if (cachedLastBlock == null) {
			createCachedLastBlock();
		}
		return cachedLastBlock;
	}
	private void createCachedLastBlock() {
		if (getRealFile().getTotalBlocks() > 0) {
			BlockReader br = new BlockReader(this.getRealFile());
			// Voy al último bloque del archivo
			Long lastBlockNumber = getRealFile().getTotalBlocks() - 1;
			br.readBlock(lastBlockNumber);
			// Si es un head, significa que puedo hidratarlo
			if (br.isBlockHead()) {
				fillLastBlockBufferWith(br.getData(), lastBlockNumber, br.getRegistryCount());
			} else {
				fillLastBlockBufferWith(null, null, 0); 
			}
		} else {
			fillLastBlockBufferWith(null, null, 0); 
		}
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
		if (iterator.getNextBlock() == BlockFile.END_BLOCK) return;
		HydratedBlock<T> hb = this.getBlock(iterator.getNextBlock());
		iterator.setCachedObjects(hb.getData().iterator());
		Long nextBlockNumber = hb.getNextBlockNumber();
		if (nextBlockNumber.equals(this.getCachedLastBlock().getBlockNumber()) && this.getCachedLastBlock().getData().size() ==  0) {
			iterator.setNextBlock(BlockFile.END_BLOCK);
		} else {
			iterator.setNextBlock(nextBlockNumber);
		}
		
	}
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
	@Override
	public void close() throws IOException {
		this.lastBlockWriter.flush();
		this.getRealFile().close();
	}
	protected void setBlockReader(BlockReader blockReader) {
		this.blockReader = blockReader;
	}

	protected BlockReader getBlockReader() {
		return blockReader;
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
			return cachedObjects.hasNext() || nextBlock != BlockFile.END_BLOCK;
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
