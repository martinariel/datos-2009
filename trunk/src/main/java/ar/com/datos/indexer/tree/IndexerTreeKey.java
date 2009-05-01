package ar.com.datos.indexer.tree;

import ar.com.datos.btree.elements.Key;
import ar.com.datos.indexer.SimpleSessionIndexer;
/**
 * Implementación de Key para el árbol que utiliza para la indexación
 * el {@link SimpleSessionIndexer}
 * @author jbarreneche
 *
 */
public class IndexerTreeKey implements Key {

	private String term;
	public IndexerTreeKey(String term) {
		super();
		this.term = term;
	}
	@Override
	public int compareTo(Key arg0) {
		IndexerTreeKey other = (IndexerTreeKey) arg0;
		return this.getTerm().compareTo(other.getTerm());
	}
	/**
	 * Término indexado
	 * @return
	 */
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
}
