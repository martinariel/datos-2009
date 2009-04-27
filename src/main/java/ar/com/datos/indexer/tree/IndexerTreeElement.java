package ar.com.datos.indexer.tree;

import java.util.ArrayList;
import java.util.Collection;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.utils.sort.external.KeyCount;
/**
 * Elemento para las hojas del árbol que utiliza el {@link SimpleSessionIndexer}
 * @author jbarreneche
 * @param <T> tipo de objeto al que se va a realizar la indexación de términos
 */
public class IndexerTreeElement<T> implements Element<IndexerTreeKey> {

	private IndexerTreeKey key;
	private OffsetAddress addressInLexicon;
	private BlockAddress<Long, Short> dataCountAddress;
	private SimpleSessionIndexer<T> indexer;
	private Collection<KeyCount<T>> temporalCount = new ArrayList<KeyCount<T>>(0);
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
		Collection<KeyCount<T>> nuevaLista = ((IndexerTreeElement<T>)element).temporalCount;
		BlockAddress<Long, Short> currentAddress = this.getDataCountAddress();
		if (currentAddress == null) {
			this.setDataCountAddress(this.indexer.getListsForTerms().addEntity(nuevaLista));
		} else {
			Collection<KeyCount<T>> previousDataCount = this.getDataCounts();
			previousDataCount.addAll(nuevaLista);
			this.setDataCountAddress(this.indexer.getListsForTerms().updateEntity(currentAddress, previousDataCount));
		}
		return this.getDataCountAddress().equals(currentAddress);
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
	 * Devuelve los datos relacionados con el término actual junto con la cantidad
	 * de relaciones para este término
	 * @return
	 */
	public Collection<KeyCount<T>> getDataCounts() {
		return this.indexer.getListsForTerms().get(getDataCountAddress());
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

}
