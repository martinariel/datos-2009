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

	public A getKeyAtom(int level);
	
	public List<A> getRestOfKey(int level);
}
