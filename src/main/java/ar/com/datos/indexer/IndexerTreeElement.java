package ar.com.datos.indexer;

import java.util.ArrayList;
import java.util.Collection;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.keywordIndexer.KeyCount;

public class IndexerTreeElement<T> implements Element<IndexerTreeKey> {

	private IndexerTreeKey key;
	private OffsetAddress addressInLexicon;
	private BlockAddress<Long, Short> dataCountAddress;
	private SimpleSessionIndexer<T> indexer;
	private Collection<KeyCount<T>> temporalCount = new ArrayList<KeyCount<T>>(0);
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

	public void addTemporalDataCount(T data, Integer count) {
		this.temporalCount.add(new KeyCount<T>(data, count));
	}
	public OffsetAddress getAddressInLexicon() {
		return this.addressInLexicon;
	}

	public void setAddressInLexicon(OffsetAddress addressInLexicon) {
		this.addressInLexicon = addressInLexicon;
	}

	public Collection<KeyCount<T>> getDataCounts() {
		return this.indexer.getListsForTerms().get(getDataCountAddress());
	}

	public BlockAddress<Long, Short> getDataCountAddress() {
		return dataCountAddress;
	}

	public void setDataCountAddress(BlockAddress<Long, Short> dataCountAddress) {
		this.dataCountAddress = dataCountAddress;
	}

	public SimpleSessionIndexer<T> getIndexer() {
		return indexer;
	}

	public void setIndexer(SimpleSessionIndexer<T> indexer) {
		this.indexer = indexer;
	}

}
