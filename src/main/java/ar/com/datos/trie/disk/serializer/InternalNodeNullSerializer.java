package ar.com.datos.trie.disk.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.InternalNode;

/**
 * Serializador que llena el primer bloque con datos basura para así reservar el 
 * @author jbarreneche
 *
 * @param <E>
 * @param <K>
 * @param <A>
 */
public class InternalNodeNullSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements Serializer<InternalNode<E,K,A>> {

	private Integer dataSize;
	private InternalNodeSerializer<E,K,A> realSerializer;
	public InternalNodeNullSerializer(InternalNodeSerializer<E,K,A> realSerializer, Integer dataSize) {
		this.dataSize = dataSize;
		this.realSerializer = realSerializer;
	}

	@Override
	public void dehydrate(OutputBuffer output, InternalNode<E, K, A> node) throws SerializerException {
		Long garbage = this.dataSize - this.realSerializer.getDehydrateSize(node);
		this.realSerializer.dehydrate(output, node);
		output.write(new byte[garbage.intValue()]);
	}

	@Override
	public long getDehydrateSize(InternalNode<E, K, A> object) {
		return dataSize;
	}

	@Override
	public InternalNode<E, K, A> hydrate(InputBuffer input) throws SerializerException {
		// No debería hidratarse con este serializador
		return this.realSerializer.hydrate(input);
	}

}
