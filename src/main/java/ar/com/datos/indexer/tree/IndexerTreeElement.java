package ar.com.datos.indexer.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.IndexedTerm;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.KeyCount;
/**
 * Elemento para las hojas del árbol que utiliza el {@link SimpleSessionIndexer}
 * @author jbarreneche
 * @param <T> tipo de objeto al que se va a realizar la indexación de términos
 */
public class IndexerTreeElement<T> implements Element<IndexerTreeKey>, IndexedTerm<T> {

	private IndexerTreeKey key;
	private OffsetAddress addressInLexicon;
	private BlockAddress<Long, Short> dataCountAddress;
	private SimpleSessionIndexer<T> indexer;
	private Integer dataCount = 0;
	private List<KeyCount<T>> temporalCount = new ArrayList<KeyCount<T>>(0);
	/**
	 * Crea un nuevo indexerTreeElement cuya clave es la recibida en key 
	 * @param key
	 */
	public IndexerTreeElement(IndexerTreeKey key) {
		super();
		this.key = key;
	}

	@Override
	public IndexerTreeKey getKey() {
		return this.key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean updateElement(Element<IndexerTreeKey> element) {
		IndexerTreeElement<T> newElement = (IndexerTreeElement<T>)element;
		List<KeyCount<T>> nuevaLista = newElement.temporalCount;
		BlockAddress<Long, Short> currentAddress = this.getDataCountAddress();
		if (currentAddress == null) {
			sortByCount(nuevaLista);
			this.setDataCountAddress(newElement.indexer.getListsForTerms().addEntity(new Tuple<OffsetAddress, List<KeyCount<T>>>(this.getAddressInLexicon(), nuevaLista)));
		} else {
			Tuple<OffsetAddress, List<KeyCount<T>>> previousList = newElement.indexer.getListsForTerms().get(this.getDataCountAddress());
			nuevaLista.addAll(previousList.getSecond());
			sortByCount(nuevaLista);
			previousList.setSecond(nuevaLista);
			this.setDataCountAddress(newElement.indexer.getListsForTerms().updateEntity(currentAddress, previousList));
		}
		this.dataCount = nuevaLista.size();
		return true;
	}

	protected void sortByCount(List<KeyCount<T>> nuevaLista) {
		Collections.sort(nuevaLista, new Comparator<KeyCount<T>>() {

			@Override
			public int compare(KeyCount<T> o1, KeyCount<T> o2) {
				return o2.getCount().compareTo(o1.getCount());
			}
			
		});
		
	}

	/**
	 * Agrega a la información no persistida el conteo del dato.
	 * Esta información será persistida cuando se agregue el elemento al árbol y este ejecute un 
	 * {@link Element#updateElement(Element)} sobre el que ya existe en el árbol.
	 * Usar cuando se creó un elemento para actualizar otro que ya existe en el árbol.
	 * @param data
	 * @param count
	 */
	public void addTemporalDataCount(T data, Integer count) {
		this.temporalCount.add(new KeyCount<T>(data, count));
	}
	/**
	 * @return dirección de la key en el archivo de léxico
	 */
	public OffsetAddress getAddressInLexicon() {
		return this.addressInLexicon;
	}
	public void setAddressInLexicon(OffsetAddress addressInLexicon) {
		this.addressInLexicon = addressInLexicon;
	}

	/**
	 * dirección de la lista de datos para este término 
	 * @return
	 */
	public BlockAddress<Long, Short> getDataCountAddress() {
		return dataCountAddress;
	}

	public void setDataCountAddress(BlockAddress<Long, Short> dataCountAddress) {
		this.dataCountAddress = dataCountAddress;
	}

	/**
	 * @return indexer asociado a este elemento
	 */
	public SimpleSessionIndexer<T> getIndexer() {
		return indexer;
	}

	public void setIndexer(SimpleSessionIndexer<T> indexer) {
		this.indexer = indexer;
	}

	@Override
	public String getTerm() {
		return this.getKey().getTerm();
	}
	
	@Override
	public List<KeyCount<T>> getAssociatedData() {
		return this.indexer.getListsForTerms().get(this.getDataCountAddress()).getSecond();
	}

	@Override
	public Integer getNumberOfAssociatedData() {
		return this.dataCount;
	}

	public void setNumberOfAssociatedData(Integer dataCount) {
		this.dataCount = dataCount;
		
	}
}
