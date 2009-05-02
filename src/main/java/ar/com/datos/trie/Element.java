/**
 * 
 */
package ar.com.datos.trie;

/**
 * @author marcos
 *
 */
public interface Element<K extends Key<A>,A extends KeyAtom> {

	/**
	 * 
	 * @return
	 */
	public K getKey();
	
}
