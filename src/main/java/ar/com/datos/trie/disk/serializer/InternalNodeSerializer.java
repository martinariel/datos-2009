package ar.com.datos.trie.disk.serializer;

import java.util.ArrayList;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.indexer.serializer.VariableLengthAddressSerializer;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.disk.DiskTrie;
import ar.com.datos.trie.node.InternalNode;
import ar.com.datos.trie.node.NodeFactory;
import ar.com.datos.trie.node.NodeReference;

public class InternalNodeSerializer<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements NullableSerializer<InternalNode<E,K,A>>{

	private ShortSerializer shortSerializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	private VariableLengthAddressSerializer addressSerializer = new VariableLengthAddressSerializer();

	private DiskTrie<E, K, A> diskTrie;
	private NodeFactory<E, K, A> nodeFactory;
	
	public InternalNodeSerializer(DiskTrie<E, K, A> diskTrie, NodeFactory<E,K,A> nodeFactory) {
		this.diskTrie = diskTrie;
		this.nodeFactory = nodeFactory;
	}

	@Override
	public void dehydrate(OutputBuffer output, InternalNode<E, K, A> node) throws SerializerException {
		if (node == null) {
			this.dehydrateNull(output);
			return;
		}
		// Deshidrato el nivel del nodo a serializar
		this.shortSerializer.dehydrate(output, (short) node.getLevel());
		// Deshidrato el elemento para este nivel
		this.diskTrie.getElementSerializer().dehydrate(output, node.getElement());
		
		// Recupero la lista de referencias hijas y la deshidrato como #nodeReferences (Atom,Address)*
		List<NodeReference<E, K, A>> referenciasAHijos = node.getChildNodesReferences();
		this.shortSerializer.dehydrate(output, (short) referenciasAHijos.size());
		for (NodeReference<E, K, A> nodeReference : referenciasAHijos) {
			this.diskTrie.getAtomSerializer().dehydrate(output, nodeReference.getKeyAtom());
			// Si es el anteúltimo nivel entonces puedo tener varios childNodeReference
			if (node.getLevel() == this.diskTrie.getLeafLevel() - 1) {
				this.shortSerializer.dehydrate(output, (short) nodeReference.getAddresses().size());
			} else {
				if (nodeReference.getAddresses().size() > 1) throw new RuntimeException("Tiene varias childNodeReference pero no es el anteúltimo nivel");
			}
			for (BlockAddress<Long, Short> childAddress : nodeReference.getAddresses()) {
				this.addressSerializer.dehydrate(output, childAddress);
			}
		}
	}

	@Override
	public long getDehydrateSize(InternalNode<E, K, A> node) {
		
		// Deshidrato el nivel del nodo a serializar
		Long acumulado = this.shortSerializer.getDehydrateSize((short) node.getLevel());
		// Deshidrato el elemento para este nivel
		acumulado += this.diskTrie.getElementSerializer().getDehydrateSize(node.getElement());
		
		// Recupero la lista de referencias hijas y la deshidrato como #nodeReferences (Atom,Address)*
		List<NodeReference<E, K, A>> a = node.getChildNodesReferences();
		acumulado += this.shortSerializer.getDehydrateSize((short) a.size());
		for (NodeReference<E, K, A> nodeReference : a) {
			acumulado += this.diskTrie.getAtomSerializer().getDehydrateSize(nodeReference.getKeyAtom());
			// Si es el anteúltimo nivel entonces puedo tener varios childNodeReference
			if (node.getLevel() == this.diskTrie.getLeafLevel() - 1) {
				acumulado += this.shortSerializer.getDehydrateSize((short) nodeReference.getAddresses().size());
			} else {
				if (nodeReference.getAddresses().size() > 1) throw new RuntimeException("Tiene varias childNodeReference pero no es el anteúltimo nivel");
			}
			for (BlockAddress<Long, Short> childAddress : nodeReference.getAddresses()) {
				acumulado += this.addressSerializer.getDehydrateSize(childAddress);
			}
		}
		return acumulado;
	}

	@Override
	public InternalNode<E, K, A> hydrate(InputBuffer input) throws SerializerException {
		
		// Hidrato el nivel del nodo a serializar
		Short nivel = this.shortSerializer.hydrate(input);
		// Trato el caso de que se haya almacenado null
		if (nivel.equals(Short.MIN_VALUE)) return null;
		
		// Hidrato el elemento para este nivel
		E element = this.diskTrie.getElementSerializer().hydrate(input);
		
		// Recupero la lista de referencias hijas y la deshidrato como #nodeReferences (Atom,Address)*
		Short numberOfNodeReferences = this.shortSerializer.hydrate(input);
		List<NodeReference<E, K, A>> childNodesReferences = new ArrayList<NodeReference<E,K,A>>(numberOfNodeReferences);
		for (Short i = 0; i < numberOfNodeReferences; i++) {
			A atom = this.diskTrie.getAtomSerializer().hydrate(input);
			Short numberOfReferences = (nivel == this.diskTrie.getLeafLevel() - 1)? this.shortSerializer.hydrate(input) : 1;
			List<BlockAddress<Long, Short>> addresses = new ArrayList<BlockAddress<Long,Short>>(numberOfReferences); 
			for (Short j = 0; j < numberOfNodeReferences; j++)  {
				addresses.add(this.addressSerializer.hydrate(input));
			}
			NodeReference<E,K,A> nodeReference = this.nodeFactory.createNodeReference(nivel + 1, atom);
			nodeReference.setAddresses(addresses);
			childNodesReferences.add(nodeReference);
		}
		return (InternalNode<E, K, A>) this.nodeFactory.createNode(nivel, element, childNodesReferences);
	}

	public void dehydrateNull(OutputBuffer buffer) {
		this.shortSerializer.dehydrate(buffer, Short.MIN_VALUE);
	}

}
