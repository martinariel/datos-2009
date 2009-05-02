package ar.com.datos.trie;

public interface Trie <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{

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
