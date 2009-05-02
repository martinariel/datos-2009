/**
 * 
 */
package ar.com.datos.trie.disk;

import ar.com.datos.btree.sharp.impl.disk.AdministrativeBTreeSharp;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.serializer.exception.SerializerException;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.Trie;
import ar.com.datos.trie.disk.serializer.LeafNodeSerializer;
import ar.com.datos.trie.disk.serializer.InternalNodeSerializer;
import ar.com.datos.trie.disk.serializer.InternalNodeStateSerializer;
import ar.com.datos.trie.node.InternalNode;
import ar.com.datos.trie.node.LeafNode;

/**
 * @author marcos
 *
 */
public class DiskTrie<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> implements Trie{

	private static final int DEFAULT_LEVELS = 4;
	private int nLevels;
	private BlockAccessor<BlockAddress<Long,Short>, LeafNode<E,K,A>> leafNodesFile;
	private BlockAccessor<BlockAddress<Long,Short>, InternalNode<E,K,A>> internalNodesFile;
	
	
	public DiskTrie(String internalFilename, int internalBlockSize, String leafFilename, int leafBlockSize){
		this(DEFAULT_LEVELS, internalFilename, internalBlockSize, leafFilename, leafBlockSize);
	}
	
	public DiskTrie(int nLevels, String internalFilename, int internalBlockSize, String leafFilename, int leafBlockSize){
		this.nLevels = nLevels;
		this.leafNodesFile = new VariableLengthFileManager<LeafNode<E,K,A>>(leafFilename, leafBlockSize, new LeafNodeSerializer<E,K,A>());
		this.internalNodesFile = new VariableLengthFileManager<InternalNode<E,K,A>>(internalFilename, internalBlockSize, new InternalNodeStateSerializer<E,K,A>(new InternalNodeSerializer<E,K,A>()));
		
		this.initializeTrie();
	}
	
	@Override
	public void addElement(Element element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Element findElement(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initializeTrie(){
		
		
	}
}
