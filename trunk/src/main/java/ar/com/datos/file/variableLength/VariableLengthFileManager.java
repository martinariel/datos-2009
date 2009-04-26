package ar.com.datos.file.variableLength;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.persistencia.variableLength.BlockMetaData;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.persistencia.variableLength.BlockWriter;
import ar.com.datos.serializer.Serializer;
/**
 * Esta entidad permite manejar la persistencia de objetos Serializables en un archivo de longitud variable.
 * Al momento de almacenar dicho objeto se devolvera un Address para poder recuperar al mismo
 * @author jbarreneche
 *
 * @param <T> tipo de objetos a persistir y o recuperar
 */
public class VariableLengthFileManager<T> implements BlockAccessor<BlockAddress<Long, Short>, T> {

	// Referencia al archivo real donde se realiza la persistencia
	private BlockFile realFile;
	// Serializador utilizado para hidratar y deshidratar objetos
	private Serializer<T> serializador;

	private BlockReader blockReader;
	// Usar solo para insertar al final
	private BlockWriter lastBlockWriter;
	
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
		if (getLastBlockWriter().getMultipleBlockDataSize() < 1) throw new InvalidParameterException("InvalidBlockSize, blockSize must be greater than " + getLastBlockWriter().getMultipleBlockDataSize());

		setEntitySerializer(entitySerializer);
		setBlockReader(new BlockReader(this.getRealFile()));
	}
    /**
     * @see ar.com.datos.file.DynamicAccesor#addEntity(Object)
     */
    @Override
	public VariableLengthAddress addEntity(T data) {
    	VariableLengthAddress retorno = addEntityNoFlush(data);
    	getLastBlockWriter().flush();
		return retorno;
	}

    /**
     * Hace un addEntity pero sin hacer flush (requerido para las versiones con caché)
     */
	protected VariableLengthAddress addEntityNoFlush(T data) {
		getSerializador().dehydrate(getLastBlockWriter().getOutputBuffer(), data);
		getLastBlockWriter().getOutputBuffer().closeEntity();
		Short currentWrittingEntityNumber = getLastBlockWriter().getCurrentWrittingEntityNumber();
		return new VariableLengthAddress(getLastBlockWriter().getCurrentWrittingBlock(),--currentWrittingEntityNumber);
	}

	@Override
	public BlockAddress<Long, Short> updateEntity(BlockAddress<Long, Short> direccion, T entity) {
		HydratedBlock<T> bloque = getBlock(direccion.getBlockNumber());
		bloque.getData().remove(direccion.getObjectNumber().intValue());
		bloque.getData().add(direccion.getObjectNumber(), entity);
		BlockWriter writer = new BlockWriter(getRealFile());
		for (Long blockNumber : bloque.getBlockNumbers()) writer.addAvailableBlock(blockNumber);
		Integer cantidadEnElBloque = bloque.getData().size(); 
		
		if (cantidadEnElBloque > 1) {
			if (cantidadEnElBloque.equals(direccion.getObjectNumber().intValue() + 1)) {
				writer.requireReplaceTo(new ReplaceResponsableDontReplace(direccion.getObjectNumber()));
			} else {
				writer.requireReplaceTo(new ReplaceResponsableWithNull<T>(direccion.getObjectNumber(),getSerializador()));
			}
		}
		for (T data : bloque.getData()) {
			getSerializador().dehydrate(writer.getOutputBuffer(), data);
			writer.getOutputBuffer().closeEntity();
		}
		writer.flush();
		if (writer.isReplaceRequiredEnabled() && writer.getRequierResponsable().hasReplacedOccurred()) return addEntity(entity); 
		
		return direccion;
		
	}
	@Override
	public Short getAmountOfBlocksFor(BlockAddress<Long, Short> address) {
		HydratedBlock<T> bloque = getBlock(address.getBlockNumber());
		return (short)bloque.getBlockNumbers().size();
	}
	@Override
	public Integer getDataSizeFor(Short numberOfChainedBlocks) {
		return numberOfChainedBlocks > 1? getLastBlockWriter().getMultipleBlockDataSize() * numberOfChainedBlocks : getLastBlockWriter().getSimpleDataSize();
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
	 * @see ar.com.datos.file.DynamicAccesor#get(BlockAddress)
	 */
	@Override
	public T get(BlockAddress<Long, Short> address) {
		try {
			HydratedBlock<T> block = getBlock(address.getBlockNumber());
			if ((!block.getBlockNumber().equals(address.getBlockNumber())) || (block.getData().size() <= address.getObjectNumber().intValue())) throw new InvalidAddressException();
			return block.getData().get(address.getObjectNumber());
		} catch (OutOfBoundsException ob) {
			throw new InvalidAddressException(ob);
		}
	}

	/**
	 * recupera un bloque hidratado con objetos. Puede obtenerlos de la cache {@link}
	 * @param blockNumber
	 * @return
	 */
	protected HydratedBlock<T> getBlock(Long blockNumber) {
		if (this.isEmpty()) return new HydratedBlock<T>(new ArrayList<T>(0),0L, BlockFile.END_BLOCK); 

		getBlockReader().readBlock(blockNumber);
		
		InputBuffer data = getBlockReader().getData();
		Integer cantidadRegistrosHidratar = getBlockReader().getRegistryCount();
		List<T> li = new ArrayList<T>(cantidadRegistrosHidratar);
		for (Short i = 0; i < cantidadRegistrosHidratar; i++) {
			li.add(this.getSerializador().hydrate(data));
		}
		
		return new HydratedBlock<T>(li, getBlockNumbers(getBlockReader()), getBlockReader().getNextBlockNumber());
	}
	private List<Long> getBlockNumbers(BlockReader blockReader2) {
		List<Long> blockNumbers = new ArrayList<Long>(blockReader2.getMetaData().size());
		for (BlockMetaData bmd: blockReader2.getMetaData())
			blockNumbers.add(bmd.getBlockNumber());
		return blockNumbers;
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

	@Override
	public Boolean isEmpty() {
		return this.getRealFile().getTotalBlocks().equals(0L);
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
		iterator.setNextBlock(nextBlockNumber);
		
	}
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
	@Override
	public void close() throws IOException {
		getLastBlockWriter().flush();
		this.getRealFile().close();
	}
	protected void setBlockReader(BlockReader blockReader) {
		this.blockReader = blockReader;
	}

	protected BlockReader getBlockReader() {
		return blockReader;
	}
	protected BlockWriter getLastBlockWriter() {
		return this.lastBlockWriter;
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
