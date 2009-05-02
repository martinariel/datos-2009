package ar.com.datos.utils.sort.external;

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
/**
 * Clase encargada de contar la cantidad de veces que se solicitó, durante la sesión,
 * realizar un count de un objeto T. Luego se pueden recorrer todos los objetos que se
 * pidió contar y recuperar la cantidad de ocurrencias del mismo.
 * El orden de la iteración es por orden creciente al resultado de la serialización del objeto.
 * @author jbarreneche
 *
 * @param <T>
 */
public class FixedLengthKeyCounter<T> implements SessionHandler, Iterable<KeyCount<T>> {

	private static final Integer DEFAULT_AMMOUNT_OF_BLOCKS = 2048;
	protected static final IntegerSerializer countSerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
	private Serializer<T> serializer;
	private BlockFile data;
	private BlockFile countedKeys;
	
	public FixedLengthKeyCounter(Serializer<T> serializer) {
		super();
		this.serializer = serializer;
	}

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
	/**
	 * Hace un ordenamiento por merge que se encarga de contar los casos que son iguales
	 * @param semiPartes
	 * @return
	 */
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

	/**
	 * Dada la lista de currentkeys busca la que es menor
	 * @param currentKeys
	 * @param comparator
	 * @return
	 */
	private byte[] getMinimumKey(List<byte[]> currentKeys, ArrayOfBytesComparator comparator) {
		byte[] minimumKey = currentKeys.get(0);
		for (byte[] data : currentKeys)
			if (comparator.compare(minimumKey, data) > 0) minimumKey = data;
		return minimumKey;
	}

	/**
	 * Carga la lista de currentKeys con el primer dato de cada iterador
	 * @param currentKeys
	 * @param iteradores
	 */
	private void cargarInitialKeys(List<byte[]> currentKeys, List<Iterator<byte[]>> iteradores) {
		for (Iterator<byte[]> it: iteradores)  {
			currentKeys.add(it.next());
		}
	}

	/**
	 * Actualiza la siguiente clave del iterador {@code it}, en caso que dicho iterador no tenga mas datos lo remueve tanto
	 * de la lista de currentkeys como de la lista de iteradores 
	 * @param currentKeys
	 * @param iteradores
	 * @param it
	 * @return <code>true</code> si se pudo obtener una siguiente clave. <code>false</code> si no había una siguiente clave para el iterador
	 */
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

	/**
	 * Construye el archivo temporal donde almacenará las claves a contar
	 * @return
	 */
	protected BlockFile constructFile(Integer blockSize) {
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

	@Override
	public Iterator<KeyCount<T>> iterator() {
		return new KeyCounterIterator();
	}
	protected class KeyCounterIterator implements Iterator<KeyCount<T>> {

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
	}
}