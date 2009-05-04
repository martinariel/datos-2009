package ar.com.datos.trie.node;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

public class 
SpecialInternalNode<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>
extends InternalNode<E,K,A> {

	public SpecialInternalNode(int level, A keyAtom, NodeFactory<E,K,A> nodeFactory) {
		super(level, keyAtom, nodeFactory);
		
	}

}
