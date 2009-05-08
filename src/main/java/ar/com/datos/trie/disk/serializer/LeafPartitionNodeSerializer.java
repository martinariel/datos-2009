package ar.com.datos.trie.disk.serializer;

import java.util.ArrayList;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.disk.DiskTrie;
import ar.com.datos.trie.node.LeafPartitionNode;
import ar.com.datos.util.Tuple;

public class 
LeafPartitionNodeSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> implements NullableSerializer<LeafPartitionNode<E,K,A>>{

	private static ShortSerializer shortSerializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	private DiskTrie<E, K, A> diskTrie;
	
	public LeafPartitionNodeSerializer(DiskTrie<E, K, A> diskTrie) {
		this.diskTrie = diskTrie;
	}

	@Override
	public void dehydrate(OutputBuffer output, LeafPartitionNode<E, K, A> node)
			throws SerializerException {
		if (node == null) { 
			this.dehydrateNull(output);
			return;
		}
		
		List<Tuple<List<A>, E>> datos = node.getLeafPartitionItems();
		shortSerializer.dehydrate(output, (short)datos.size());
		for (Tuple<List<A>, E> tupla : datos) {
			shortSerializer.dehydrate(output, (short)tupla.getFirst().size());
			for (A atom : tupla.getFirst()) this.diskTrie.getAtomSerializer().dehydrate(output, atom);
			this.diskTrie.getElementSerializer().dehydrate(output, tupla.getSecond());
		}
	}

	@Override
	public long getDehydrateSize(LeafPartitionNode<E, K, A> node) {
		List<Tuple<List<A>, E>> datos = node.getLeafPartitionItems();
		Long acumulado = shortSerializer.getDehydrateSize((short)datos.size());
		for (Tuple<List<A>, E> tupla : datos) {
			acumulado += shortSerializer.getDehydrateSize((short)tupla.getFirst().size());
			for (A atom : tupla.getFirst()) acumulado += this.diskTrie.getAtomSerializer().getDehydrateSize(atom);
			acumulado += this.diskTrie.getElementSerializer().getDehydrateSize(tupla.getSecond());
		}
		
		return acumulado;
	}

	@Override
	public LeafPartitionNode<E, K, A> hydrate(InputBuffer input)
			throws SerializerException {
		Short nItems = shortSerializer.hydrate(input);
		List<Tuple<List<A>, E>> datos = new ArrayList<Tuple<List<A>,E>>(nItems);
		for (Short i = 0; i < nItems; i++) {
			Short nAtoms = shortSerializer.hydrate(input);
			List<A> atoms = new ArrayList<A>(nAtoms);
			for (Short j = 0; j < nAtoms; j++) {
				atoms.add(this.diskTrie.getAtomSerializer().hydrate(input));
			}
			E element = this.diskTrie.getElementSerializer().hydrate(input);
			datos.add(new Tuple<List<A>, E>(atoms, element));
		}
		return new LeafPartitionNode<E, K, A>(this.diskTrie.getLeafLevel(), this.diskTrie.getLeafPartitionSize(), datos);
	}

	public void dehydrateNull(OutputBuffer buffer) {
		shortSerializer.dehydrate(buffer, (short)0);
	}

}
