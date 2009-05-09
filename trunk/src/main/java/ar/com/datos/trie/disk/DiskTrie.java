/**
 * 
 */
package ar.com.datos.trie.disk;

import java.io.IOException;

import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.InvalidAddressException;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.Trie;
import ar.com.datos.trie.disk.serializer.InternalNodeNullSerializer;
import ar.com.datos.trie.disk.serializer.InternalNodeSerializer;
import ar.com.datos.trie.disk.serializer.InternalNodeStateSerializer;
import ar.com.datos.trie.disk.serializer.LeafPartitionNodeSerializer;
import ar.com.datos.trie.node.InternalNode;
import ar.com.datos.trie.node.LeafPartitionNode;
import ar.com.datos.trie.node.NodeFactory;

/**
 * @author marcos
 *
 */
public class DiskTrie<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> implements Trie<E,K,A> {

	private static final int DEFAULT_LEVELS = 4;
	private static final int DEFAULT_LEAF_PARTITON_SIZE = 10;

	public static final String leafSuffix = ".leaf"; 
	public static final String internalSuffix = ".internal"; 

	private Integer nLevels;
	private Integer leafPartitionSize = DEFAULT_LEAF_PARTITON_SIZE;

	private InternalNode<E,K,A> rootNode;
	private BlockAddress<Long, Short> rootNodeAddress = new VariableLengthAddress(0L,(short)0);
	
	private BlockAccessor<BlockAddress<Long,Short>, LeafPartitionNode<E,K,A>> leafNodesFile;
	private BlockAccessor<BlockAddress<Long,Short>, InternalNode<E,K,A>> internalNodesFile;

	private Serializer<A> atomSerializer;
	private Serializer<E> elementSerializer;

	private InternalNodeStateSerializer<E,K,A> internalNodeSerializer;
	private NodeFactory<E, K, A> nodeFactory;
	
	public DiskTrie(String filename, int internalBlockSize, int leafBlockSize, 
			NullableSerializer<E> elementSerializer, Serializer<A> atomSerializer){
		this(DEFAULT_LEVELS, filename, internalBlockSize, leafBlockSize, elementSerializer, atomSerializer);
	}
	
	public DiskTrie(int nLevels, String filename, int internalBlockSize, int leafBlockSize,
			NullableSerializer<E> elementSerializer, Serializer<A> atomSerializer){
		this.nLevels = nLevels - 1;

		if (this.nLevels < 0) throw new RuntimeException("Valor incorrecto para niveles");
		
	
		this.elementSerializer = elementSerializer;
		this.atomSerializer = atomSerializer;
		
		this.internalNodeSerializer = new InternalNodeStateSerializer<E,K,A>();
		this.leafNodesFile = new VariableLengthFileManager<LeafPartitionNode<E,K,A>>(filename + leafSuffix, leafBlockSize, new LeafPartitionNodeSerializer<E,K,A>(this));
		this.internalNodesFile = new VariableLengthFileManager<InternalNode<E,K,A>>(filename + internalSuffix, internalBlockSize, internalNodeSerializer);
		this.nodeFactory = new NodeFactory<E, K, A>(this.nLevels, DEFAULT_LEAF_PARTITON_SIZE, this.leafNodesFile,this.internalNodesFile);

		this.initializeTrie();
	}
	
	// este metodo debe crear un nodo interno para el nivel cero 
	// y debe llenar con basura el primer bloque para poder garantizar que el
	// vlfm deje la raiz siempre en el bloque 0.
	private void initializeTrie(){
		InternalNodeSerializer<E, K, A> realSerializer = new InternalNodeSerializer<E, K, A>(this, this.nodeFactory);
		try {
			this.internalNodeSerializer.setRealSerializer(realSerializer);
			this.rootNode = this.internalNodesFile.get(this.rootNodeAddress);
		} catch (InvalidAddressException e) {
			this.internalNodeSerializer.setRealSerializer(new InternalNodeNullSerializer<E,K,A>(realSerializer, this.internalNodesFile.getDataSizeFor((short)1)));
			this.rootNode = (InternalNode<E, K, A>) this.nodeFactory.createNode(-1);
			this.internalNodesFile.addEntity(this.rootNode);
			this.internalNodeSerializer.setRealSerializer(realSerializer);
		}
		this.rootNode.setAddress(this.rootNodeAddress);
	}

	@Override
	public void addElement(E element) {
		if (getRootNode().addElement(element)) {
			BlockAddress<Long, Short> newRootAddress = this.internalNodesFile.updateEntity(this.rootNodeAddress, getRootNode());
			if (!newRootAddress.equals(this.rootNodeAddress)) throw new RuntimeException("Se modificó el root address... K-OS!!!");
		}
	}

	protected InternalNode<E, K, A> getRootNode() {
		return this.rootNode;
	}

	@Override
	public E findElement(K key) {
		return getRootNode().findElement(key);
	}

	public Serializer<A> getAtomSerializer() {
		return atomSerializer;
	}

	public Serializer<E> getElementSerializer() {
		return elementSerializer;
	}

	public Integer getLeafLevel() {
		return this.nLevels;
	}

	public Integer getLeafPartitionSize() {
		return this.leafPartitionSize;
	}

	@Override
	public void close() throws IOException {
		this.leafNodesFile.close();
		this.internalNodesFile.close();
	}
	
}
