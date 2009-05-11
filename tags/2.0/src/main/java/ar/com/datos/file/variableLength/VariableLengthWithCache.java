package ar.com.datos.file.variableLength;

import java.util.ArrayList;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.persistencia.variableLength.FlushListener;
import ar.com.datos.serializer.Serializer;
/**
 * Esta entidad permite manejar la persistencia de objetos Serializables en un archivo de longitud variable.
 * Al momento de almacenar dicho objeto se devolvera un Address para poder recuperar al mismo
 * @author jbarreneche
 *
 * @param <T> tipo de objetos a persistir y o recuperar
 */
public class VariableLengthWithCache<T> extends VariableLengthFileManager<T> {

	// Se maneja este cache por separado para poder contemplar de manera mas simple los casos en que, al agregar, se debe generar el nuevo bloque
	private HydratedBlock<T> cachedLastBlock = null;
	// Implementacion simple de cache de un solo bloque
	private HydratedBlock<T> cachedBlock;
	
	/**
	 * Permite crear una instancia indicando cual es el serializador a usar en lugar de usar el nativo de los objetos de tipo T
	 */
    public VariableLengthWithCache(String fileName, Integer blockSize, Serializer<T> entitySerializer) {
    	super(fileName, blockSize, entitySerializer);
		syncCacheAndLastBlockWriter();
	}
    /**
     * @see ar.com.datos.file.DynamicAccesor#addEntity(Object)
     */
    @Override
	public BlockAddress<Long, Short> addEntity(T data) {
		getCachedLastBlock().getData().add(data);
		return addEntityNoFlush(data);
	}

	/**
	 * recupera un bloque hidratado con objetos. Puede obtenerlos de la cache
	 */
	protected HydratedBlock<T> getBlock(Long blockNumber) {
		if (isBlockInCache(blockNumber)) return getBlockFromCache(blockNumber);
		return addToCache(super.getBlock(blockNumber));
	}
	/**
	 * Manejo basico de cache
	 */
	protected HydratedBlock<T> addToCache(HydratedBlock<T> hb) {
		return this.cachedBlock = hb;
	}
	protected HydratedBlock<T> getBlockFromCache(Long blockNumber) {
		if (blockNumber.equals(this.getLastBlockBufferBlockNumber())) return getCachedLastBlock();
		return this.cachedBlock;
	}
	protected Boolean isBlockInCache(Long blockNumber) {
		boolean b = blockNumber >= this.getRealFile().getTotalBlocks() -1;
		return (b && blockNumber.equals(this.getLastBlockBufferBlockNumber()))
		|| (this.cachedBlock != null && blockNumber.equals(this.cachedBlock.getBlockNumber()));
	}
	private Long getLastBlockBufferBlockNumber() {
		return getCachedLastBlock().getBlockNumber();
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
				fillLastBlockBufferWith(null, BlockFile.END_BLOCK, 0); 
			}
		} else {
			fillLastBlockBufferWith(null, BlockFile.END_BLOCK, 0); 
		}
	}
	/**
	 * Se asegura que el HydratedBlock y el lastBlockWriter tengan siempre la misma cantidad de elementos
	 * De esta manera, si el writer flushea y reduce la cantidad de entidades en el buffer también, esas entidades,
	 * se retiran del caché del último bloque
	 */
	private void syncCacheAndLastBlockWriter() {
		// Maneja la sincronización entre los datos almacenados en el blockWriter y el lastBlockCache. Debería externalizarse a un objeto que maneje caché
		this.getLastBlockWriter().addFlushListener(new FlushListener() {

			@Override
			public void flushed() {
				while (getLastBlockWriter().getEntitiesCount() < cachedLastBlock.getData().size()) {
					cachedLastBlock.getData().remove(0);
				}
				if (cachedLastBlock.getData().isEmpty()) cachedLastBlock.setBlock(BlockFile.END_BLOCK);
				else cachedLastBlock.setBlock(getLastBlockWriter().getCurrentWrittingBlock());
			}
			
		});
	}

	/**
	 * El metodo esta implementado de esta manera para 
	 */
	private void fillLastBlockBufferWith(InputBuffer iBuffer, Long lastBlockNumber, Integer cantidadRegistros) {
		this.setCachedLastBlock(new HydratedBlock<T>(new ArrayList<T>(), lastBlockNumber, getRealFile()));
		if (lastBlockNumber != BlockFile.END_BLOCK) {
			this.getLastBlockWriter().addAvailableBlock(lastBlockNumber);
			for (Integer i = 0; i < cantidadRegistros; i++) {
				T hydrate = this.getSerializador().hydrate(iBuffer);
				this.getCachedLastBlock().getData().add(hydrate);
				this.getSerializador().dehydrate(this.getLastBlockWriter(), hydrate);
				this.getLastBlockWriter().closeEntity();
			}
		}
	}
	@Override
	public Boolean isEmpty() {
		return super.isEmpty() && this.getLastBlockBufferBlockNumber() == BlockFile.END_BLOCK && getCachedLastBlock().getData().isEmpty();
	}
	/**
	 * Actualiza la información del iterador que itera sobre este archivo
	 */
	protected void updateInformation(VLFMIterator iterator) {
		if (iterator.getNextBlock() == BlockFile.END_BLOCK) return;
		super.updateInformation(iterator);
		// Si tengo cacheado el último bloque y no tengo nada entonces no hay un siguiente bloque
		if (this.cachedLastBlock != null && iterator.getNextBlock().equals(this.getCachedLastBlock().getBlockNumber()) && this.getCachedLastBlock().getData().size() ==  0) 
			iterator.setNextBlock(BlockFile.END_BLOCK);
		
	}
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}