package ar.com.datos.indexer.tree;

import java.util.ArrayList;
import java.util.Collection;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.SimpleSessionIndexer;
import ar.com.datos.utils.sort.external.KeyCount;
/**
 * Elemento para las hojas del �rbol que utiliza el {@link SimpleSessionIndexer}
 * @author jbarreneche
 * @param <T> tipo de objeto al que se va a realizar la indexaci�n de t�rminos
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
		IndexerTreeElement<T> newElement = (IndexerTreeElement<T>)element;
		Collection<KeyCount<T>> nuevaLista = newElement.temporalCount;
		BlockAddress<Long, Short> currentAddress = this.getDataCountAddress();
		if (currentAddress == null) {
			this.setDataCountAddress(newElement.indexer.getListsForTerms().addEntity(nuevaLista));
		} else {
			Collection<KeyCount<T>> previousDataCount = newElement.indexer.getListsForTerms().get(this.getDataCountAddress());
			previousDataCount.addAll(nuevaLista);
			this.setDataCountAddress(newElement.indexer.getListsForTerms().updateEntity(currentAddress, previousDataCount));
		}
		return this.getDataCountAddress().equals(currentAddress);
	}

	/**
	 * Agrega a la informaci�n no persistida el conteo del dato.
	 * Esta informaci�n ser� persistida cuando se agregue el elemento al �rbol y este ejecute un 
	 * {@link Element#updateElement(Element)} sobre el que ya existe en el �rbol.
	 * Usar cuando se cre� un elemento para actualizar otro que ya existe en el �rbol.
	 * @param data
	 * @param count
	 */
	public void addTemporalDataCount(T data, Integer count) {
		this.temporalCount.add(new KeyCount<T>(data, count));
	}
	/**
	 * @return direcci�n de la key en el archivo de l�xico
	 */
	public OffsetAddress getAddressInLexicon() {
		return this.addressInLexicon;
	}
	public void setAddressInLexicon(OffsetAddress addressInLexicon) {
		this.addressInLexicon = addressInLexicon;
	}

	/**
	 * direcci�n de la lista de datos para este t�rmino 
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