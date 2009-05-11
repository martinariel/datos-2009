/**
 * 
 */
package ar.com.datos.trie.node;


import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public abstract class Node<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{

	// Indica el nivel dentro del Trie en el que esta este nodo. 
	// Esto se debe a que hay niveles con un comportamiento especial.
	protected int level;
	private BlockAddress<Long, Short> address; 
	
	public Node(int level) {
		this.level = level;
		this.address = null;
	}
	
	public abstract E findElement(K key);
	
	public abstract boolean addElement(E Element);

	public BlockAddress<Long, Short> getAddress() {
		return this.address;
	}
	public void setAddress(BlockAddress<Long, Short> address) {
		this.address = address;
	}

	public int getLevel() {
		return level;
	}

}
