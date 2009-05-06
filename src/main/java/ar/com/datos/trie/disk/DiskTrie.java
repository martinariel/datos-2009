/**
 * 
 */
package ar.com.datos.trie.disk;

import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.Trie;
import ar.com.datos.trie.disk.serializer.InternalNodeSerializer;
import ar.com.datos.trie.disk.serializer.InternalNodeStateSerializer;
import ar.com.datos.trie.disk.serializer.LeafPartitionNodeSerializer;
import ar.com.datos.trie.node.InternalNode;
import ar.com.datos.trie.node.LeafPartitionNode;

/**
 * @author marcos
 *
 */
public class DiskTrie<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> implements Trie<E,K,A>{

	private static final int DEFAULT_LEVELS = 4;
	private int nLevels;
	private InternalNode<E,K,A> rootNode;
	private BlockAddress<Long, Short> rootNodeAddress = new VariableLengthAddress((long)0,(short)0);
	private BlockAccessor<BlockAddress<Long,Short>, LeafPartitionNode<E,K,A>> leafNodesFile;
	private BlockAccessor<BlockAddress<Long,Short>, InternalNode<E,K,A>> internalNodesFile;
	
	
	public DiskTrie(String internalFilename, int internalBlockSize, String leafFilename, int leafBlockSize){
		this(DEFAULT_LEVELS, internalFilename, internalBlockSize, leafFilename, leafBlockSize);
	}
	
	public DiskTrie(int nLevels, String internalFilename, int internalBlockSize, String leafFilename, int leafBlockSize){
		this.nLevels = nLevels;
		
		this.leafNodesFile = new VariableLengthFileManager<LeafPartitionNode<E,K,A>>(leafFilename, leafBlockSize, new LeafPartitionNodeSerializer<E,K,A>());
		this.internalNodesFile = new VariableLengthFileManager<InternalNode<E,K,A>>(internalFilename, internalBlockSize, new InternalNodeStateSerializer<E,K,A>(new InternalNodeSerializer<E,K,A>()));
		
		this.initializeTrie();
	}
	
	// este metodo debe crear un nodo interno para el nivel cero 
	// y debe llenar con basura el primer bloque para poder garantizar que el
	// vlfm deje la raiz siempre en el bloque 0.
	private void initializeTrie(){
		
	}

	@Override
	public void addElement(E element) {
		this.rootNode.addElement(element);
	}

	@Override
	public E findElement(K key) {
		return this.rootNode.findElement(key);
	}
}
