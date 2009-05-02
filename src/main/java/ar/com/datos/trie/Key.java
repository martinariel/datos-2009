/**
 * 
 */
package ar.com.datos.trie;

import java.util.List;

/**
 * @author marcos
 *
 */
public interface Key<A extends KeyAtom>{

	/**
	 *
	 * @param level
	 * @return
	 */
	public A getKeyAtom(int level);
	
	/**
	 *
	 * @param level
	 * @return
	 */
	public List<A> getRestOfKey(int level);
}
