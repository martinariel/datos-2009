/**
 * 
 */
package ar.com.datos.trie.node;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class 
LeafNode<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	public LeafNode(int level) {
		super(level);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean addElement(E Element) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public E findElement(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
