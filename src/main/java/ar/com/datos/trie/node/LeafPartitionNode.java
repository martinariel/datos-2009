/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.Iterator;
import java.util.List;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.exception.LeafPartitionLimitException;
import ar.com.datos.util.Tuple;

/**
 * @author marcos
 *
 */
public class 
LeafPartitionNode<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	private int maxPartitionItems;
	private List<Tuple<List<A>,E>> leafPartitionItems;
	
	public LeafPartitionNode(int level, int maxPartitionItems) {
		super(level);
		this.maxPartitionItems = maxPartitionItems;
	}

	@Override
	public boolean addElement(E element) {
		if(this.leafPartitionItems.size() < this.maxPartitionItems){
			this.leafPartitionItems.add(
				new Tuple<List<A>, E>(element.getKey().getRestOfKey(this.level), element)
			);
			return true;
		} else {
			throw new LeafPartitionLimitException();
		}
	}

	@Override
	public E findElement(K key) {
		Tuple<List<A>, E> item;
		Iterator<Tuple<List<A>, E>> it = leafPartitionItems.iterator();
		while(it.hasNext()){
			item = it.next();
			if (item.getSecond().getKey().equals(key)){
				return item.getSecond();
			}
		}
		return null;
	}
	
}
