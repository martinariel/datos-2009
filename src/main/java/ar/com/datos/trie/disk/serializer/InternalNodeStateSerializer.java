package ar.com.datos.trie.disk.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.InternalNode;

public class InternalNodeStateSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements Serializer<InternalNode<E,K,A>>{

	private Serializer<InternalNode<E,K,A>> realSerializer;
	
	public InternalNodeStateSerializer(Serializer<InternalNode<E,K,A>> realSerializer) {
		this.setRealSerializer(realSerializer);
	}
	
	public InternalNodeStateSerializer() {
	}

	public void setRealSerializer(Serializer<InternalNode<E,K,A>> realSerializer){
		this.realSerializer = realSerializer;
	}
	
	@Override
	public void dehydrate(OutputBuffer output, InternalNode<E, K, A> node) throws SerializerException {
		this.realSerializer.dehydrate(output, node);
	}

	@Override
	public long getDehydrateSize(InternalNode<E, K, A> node) {
		return this.realSerializer.getDehydrateSize(node);
	}

	@Override
	public InternalNode<E, K, A> hydrate(InputBuffer input) throws SerializerException {
		return this.realSerializer.hydrate(input);
	}

}
