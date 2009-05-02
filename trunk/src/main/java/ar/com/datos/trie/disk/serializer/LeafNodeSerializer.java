package ar.com.datos.trie.disk.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.LeafNode;

public class 
LeafNodeSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements Serializer<LeafNode<E,K,A>>{

	@Override
	public void dehydrate(OutputBuffer output, LeafNode<E, K, A> node)
			throws SerializerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getDehydrateSize(LeafNode<E, K, A> node) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LeafNode<E, K, A> hydrate(InputBuffer input)
			throws SerializerException {
		// TODO Auto-generated method stub
		return null;
	}

}