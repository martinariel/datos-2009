package ar.com.datos.indexer.keywordIndexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.buffer.variableLength.ArrayInputBuffer;
import ar.com.datos.buffer.variableLength.ArrayOutputBuffer;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.util.ArrayOfBytesComparator;
import ar.com.datos.wordservice.SessionHandler;

public class FixedLengthKeyCounter<T> implements SessionHandler, Iterable<KeyCount<T>> {

	private static final Integer DEFAULT_AMMOUNT_OF_BLOCKS = 60;
	protected static final IntegerSerializer countSerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
	private Serializer<T> serializer;
	private BlockFile data;
	private BlockFile countedKeys;
	@Override
	public void startSession() {
		this.data = constructFile(getBlockSize().intValue());
	}

	@Override
	public void endSession() {
		List<BlockFile> semiPartes = processSectionSort();
		this.countedKeys = mergeChunksAndCount(semiPartes);
		this.data.close();
		this.data = null;
	}

	private BlockFile mergeChunksAndCount(List<BlockFile> semiPartes) {
		BlockFile countingKeys = constructFile(data.getBlockSize() + new Long(countSerializer.getDehydrateSize(0)).intValue());
		List<byte[]> currentKeys = new ArrayList<byte[]>(semiPartes.size());
		List<Iterator<byte[]>> iteradores = new ArrayList<Iterator<byte[]>>();
		cargarIteradores(iteradores, semiPartes);
		cargarInitialKeys(currentKeys, iteradores);
		ArrayOfBytesComparator comparator = new ArrayOfBytesComparator();
		while (!currentKeys.isEmpty()) {
			byte[] minimumKey = getMinimumKey(currentKeys, comparator);
			int cantidadContada = 0;
			for (Integer i = currentKeys.size()-1 ; i >= 0; i--) {
				boolean actualizoClave = true;
				while (actualizoClave && comparator.compare(minimumKey, currentKeys.get(i)) == 0) {
					cantidadContada++;
					actualizoClave = getNextKey(currentKeys, iteradores, iteradores.get(i));
				}
			}
			ArrayOutputBuffer aob = new ArrayOutputBuffer(countingKeys.getBlockSize());
			aob.write(minimumKey);
			countSerializer.dehydrate(aob, cantidadContada);
			countingKeys.appendBlock(aob.getArrayByte().getArray());
		}
		return countingKeys;
	}

	private byte[] getMinimumKey(List<byte[]> currentKeys, ArrayOfBytesComparator comparator) {
		byte[] minimumKey = currentKeys.get(0);
		for (byte[] data : currentKeys)
			if (comparator.compare(minimumKey, data) > 0) minimumKey = data;
		return minimumKey;
	}

	private void cargarInitialKeys(List<byte[]> currentKeys, List<Iterator<byte[]>> iteradores) {
		for (Iterator<byte[]> it: iteradores)  {
			currentKeys.add(it.next());
		}
	}

	private boolean getNextKey(List<byte[]> currentKeys, List<Iterator<byte[]>> iteradores, Iterator<byte[]> it) {
		currentKeys.remove(iteradores.indexOf(it));
		if (!it.hasNext()) {
			iteradores.remove(it);
			return false;
		} else {
			currentKeys.add(iteradores.indexOf(it), it.next());
			return true;
		}
	}

	private void cargarIteradores(List<Iterator<byte[]>> iteradores, List<BlockFile> semiPartes) {
		for (BlockFile bf : semiPartes) iteradores.add(bf.iterator());
	}

	private List<BlockFile> processSectionSort() {
		return new ExternalSorter(this.data,DEFAULT_AMMOUNT_OF_BLOCKS).getSortedChunks();
	}

	@Override
	public boolean isActive() {
		return this.data != null;
	}

	protected Serializer<T> getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer<T> serializer) {
		this.serializer = serializer;
	}

	public void countKey(T key) {
		ArrayOutputBuffer carga = new ArrayOutputBuffer(getData().getBlockSize());
		getSerializer().dehydrate(carga, key);
		this.data.appendBlock(carga.getArrayByte().getArray());
	}

	@Override
	public Iterator<KeyCount<T>> iterator() {
		return new Iterator<KeyCount<T>>() {

			private Iterator<byte[]> blockIterator = countedKeys.iterator();
			@Override
			public boolean hasNext() {
				return blockIterator.hasNext();
			}

			@Override
			public KeyCount<T> next() {
				ArrayInputBuffer aib = new ArrayInputBuffer(new SimpleArrayByte(blockIterator.next()));
				T key = getSerializer().hydrate(aib);
				return new KeyCount<T>(key, countSerializer.hydrate(aib));
			}

			@Override
			public void remove() {
				blockIterator.remove();
			}
			
		};
	}
	/**
	 * Construye el archivo temporal donde almacenará las claves a contar
	 * @return
	 */
	public BlockFile constructFile(Integer blockSize) {
		return new SimpleBlockFile(blockSize);
	}

	protected Long getBlockSize() {
		return getSerializer().getDehydrateSize(null);
	}

	protected BlockFile getData() {
		return data;
	}

	protected void setData(BlockFile data) {
		this.data = data;
	}

}
