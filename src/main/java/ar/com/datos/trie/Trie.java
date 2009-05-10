package ar.com.datos.trie;

import java.io.Closeable;

public interface Trie <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Closeable {

	/**
	 * Agrega un K al trie
	 */
	public void addElement(E element);
	
	public E findElement(K key);
	
}
