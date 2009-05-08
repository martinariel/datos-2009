package ar.com.datos.trie;

import java.io.Closeable;

public interface Trie <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Closeable {

	/**
	 * Agrega un K al trie
	 * @param key
	 * @param element
	 */
	public void addElement(E element);
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public E findElement(K key);
	
}
