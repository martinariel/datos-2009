package ar.com.datos.file.variableLength;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.file.exception.NullableSerializerRequiredException;
import ar.com.datos.persistencia.variableLength.BlockMetaData;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.persistencia.variableLength.BlockWriter;
import ar.com.datos.persistencia.variableLength.FlushListener;
import ar.com.datos.persistencia.variableLength.ReplaceResponsable;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;
/**
 * Esta entidad permite manejar la persistencia de objetos Serializables en un archivo de longitud variable.
 * Al momento de almacenar dicho objeto se devolvera un Address para poder recuperar al mismo
 * @author jbarreneche
 *
 * @param <T> tipo de objetos a persistir y o recuperar
 */
public class VariableLengthFileManager<T> implements DynamicAccesor<T>{

	// Referencia al archivo real donde se realiza la persistencia
	private BlockFile realFile;
	// Serializador utilizado para hidratar y deshidratar objetos
	private Serializer<T> serializador;

	private BlockReader blockReader;
	// Usar solo para insertar al final
	private BlockWriter lastBlockWriter;
	
	// Se maneja este cache por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock<T> cachedLastBlock = null;
	// Implementacion simple de cache de un solo bloque
	private HydratedBlock<T> cachedBlock;
	
	/**
	 * Permite crear una instancia indicando cual es el serializador a usar en lugar de usar el nativo de los objetos de tipo T
	 * 
     * @param fileName nombre de
     * @param blockSize
     * @param blockSize
	 */
    public VariableLengthFileManager(String fileName, Integer blockSize, Serializer<T> entitySerializer) {
		setRealFile(constructFile(fileName, blockSize));
		this.lastBlockWriter = new BlockWriter(this.getRealFile());
    	// Si no puedo almacenar al menos un byte por registro en los casos que el registro excede el tamaño del bloque
    	// nunca podrí­a almacenar dicho registro. Así que no tiene sentido un archivo de ese tamaño de blocksize
		if (this.lastBlockWriter.getMultipleBlockDataSize() < 1) throw new InvalidParameterException("InvalidBlockSize, blockSize must be greater than " + this.lastBlockWriter.getMultipleBlockDataSize());

		setEntitySerializer(entitySerializer);
		setBlockReader(new BlockReader(this.getRealFile()));
		syncCacheAndLastBlockWriter();
	}
    /**
     * @see ar.com.datos.file.DynamicAccesor#addEntity(Object)
     */
    @Override
	public Address<Long, Short> addEntity(T data) {
		getCachedLastBlock().getData().add(data);
		getSerializador().dehydrate(this.lastBlockWriter.getOutputBuffer(), data);
		this.lastBlockWriter.getOutputBuffer().closeEntity();
		return new VariableLengthAddress(this.lastBlockWriter.getCurrentWrittingBlock(),this.lastBlockWriter.getCurrentWrittingEntityNumber());
	}

	@Override
	public Address<Long, Short> updateEntity(Address<Long, Short> direccion, T entity) {
		HydratedBlock<T> bloque = getBlock(direccion.getBlockNumber());
		bloque.getData().remove(direccion.getObjectNumber());
		bloque.getData().add(direccion.getObjectNumber(), entity);
		BlockWriter writer = new BlockWriter(getRealFile());
		for (Long blockNumber : bloque.getBlockNumbers()) writer.addAvailableBlock(blockNumber);
		
		ReplaceResponsableImplementation replaceResponsable = new ReplaceResponsableImplementation(direccion.getObjectNumber());
		writer.requireReplaceTo(replaceResponsable);
		for (T data : bloque.getData()) {
			getSerializador().dehydrate(writer.getOutputBuffer(), data);
			writer.getOutputBuffer().closeEntity();
		}
		if (replaceResponsable.hasReplacedOccurred()) return addEntity(entity); 
		
		return direccion;
		
	}
	/**
	 * Devuelve un iterador que permite recorrer todos los objetos persistidos por esta entidad
	 * @see ar.com.datos.file.DynamicAccesor#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return new VLFMIterator(this);
	}

	/**
	 * @see ar.com.datos.file.DynamicAccesor#get(Address)
	 */
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
		
		InputBuffer data = getBlockReader().getData();
		Integer cantidadRegistrosHidratar = getBlockReader().getRegistryCount();
		List<T> li = new ArrayList<T>(cantidadRegistrosHidratar);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			li.add(this.getSerializador().hydrate(data));
		}
		
		return addToCache(new HydratedBlock<T>(li, getBlockNumbers(getBlockReader()), getBlockReader().getNextBlockNumber()));
	}
	private List<Long> getBlockNumbers(BlockReader blockReader2) {
		List<Long> blockNumbers = new ArrayList<Long>(blockReader2.getMetaData().size());
		for (BlockMetaData bmd: blockReader2.getMetaData())
			blockNumbers.add(bmd.getBlockNumber());
		return blockNumbers;
	}
	/**
	 * Manejo basico de cache
	 * @param hb
	 */
	protected HydratedBlock<T> addToCache(HydratedBlock<T> hb) {
		return this.cachedBlock = hb;
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
	 * @param fileName
	 * @param blockSize
	 * @return
	 */
	protected BlockFile constructFile(String fileName, Integer blockSize) {
		return new SimpleBlockFile(fileName, blockSize);
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
	private Long getLastBlockBufferBlockNumber() {
		return this.lastBlockWriter.getCurrentWrittingBlock();
	}
	protected HydratedBlock<T> getCachedLastBlock() {
		if (cachedLastBlock == null) {
			createCachedLastBlock();
		}
		return cachedLastBlock;
	}
	private void setCachedLastBlock(HydratedBlock<T> cachedLastBlock) {
		this.cachedLastBlock = cachedLastBlock;
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
	/**
	 * Se asegura que el HydratedBlock y el lastBlockWriter tengan siempre la misma cantidad de elementos
	 * De esta manera, si el writer flushea y reduce la cantidad de entidades en el buffer también, esas entidades,
	 * se retiran del caché del último bloque
	 */
	private void syncCacheAndLastBlockWriter() {
		// Maneja la sincronización entre los datos almacenados en el blockWriter y el lastBlockCache. Debería externalizarse a un objeto que maneje caché
		this.lastBlockWriter.addFlushListener(new FlushListener() {

			@Override
			public void flushed() {
//				cachedLastBlock.setBlockMetaData();
				while (lastBlockWriter.getOutputBuffer().getEntitiesCount() < cachedLastBlock.getData().size()) {
					cachedLastBlock.getData().remove(0);
				}
			}
			
		});
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
	private final class ReplaceResponsableImplementation implements	ReplaceResponsable {
		private Short replaceEntity;
		private Boolean replacedOccurred = false;

		public ReplaceResponsableImplementation(Short objectNumber) {
			this.replaceEntity = objectNumber;
		}

		@Override
		public void notifyExceed(BlockWriter blockWriter) {
			
			if (!(getSerializador() instanceof NullableSerializer)) throw new NullableSerializerRequiredException();
			NullableSerializer<T> serializer = (NullableSerializer<T>) getSerializador();

			serializer.dehydrateNull(blockWriter.getOutputBuffer());
			blockWriter.getOutputBuffer().closeEntity();

			replacedOccurred = true;
		}

		public Boolean hasReplacedOccurred() {
			return replacedOccurred;
		}

		@Override
		public Short replaceObject() {
			return replaceEntity;
		}

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
