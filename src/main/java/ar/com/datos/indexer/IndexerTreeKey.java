package ar.com.datos.indexer;

import ar.com.datos.btree.elements.Key;

public class IndexerTreeKey implements Key {

	private String term;
	public IndexerTreeKey(String term) {
		super();
		this.term = term;
	}
	@Override
	public int compareTo(Key arg0) {
		if (arg0 == null || !(arg0 instanceof IndexerTreeKey)) throw new UncomparableException();
		IndexerTreeKey other = (IndexerTreeKey) arg0;
		return this.getTerm().compareTo(other.getTerm());
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}

}
