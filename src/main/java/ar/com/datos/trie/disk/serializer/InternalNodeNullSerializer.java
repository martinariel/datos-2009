package ar.com.datos.trie.disk.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.InternalNode;

public class InternalNodeNullSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements Serializer<InternalNode<E,K,A>>{

	@Override
	public void dehydrate(OutputBuffer output, InternalNode<E, K, A> node)
			throws SerializerException {
		
	}

	@Override
	public long getDehydrateSize(InternalNode<E, K, A> object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InternalNode<E, K, A> hydrate(InputBuffer input)
			throws SerializerException {
		// TODO Auto-generated method stub
		return null;
	}

}
