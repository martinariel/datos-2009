/**
 * 
 */
package ar.com.datos.trie.node;

import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class NodeReference  <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{
	private BlockAddress<Long, Short> address;
	private VariableLengthFileManager<Node<E,K,A>> vlfm;
	
	public NodeReference(VariableLengthFileManager<Node<E,K,A>> vlfm, 
			BlockAddress<Long, Short> address){
		this.vlfm = vlfm;
		this.address = address;
	}
	public NodeReference(VariableLengthFileManager<Node<E,K,A>> vlfm){
		this.vlfm = vlfm;
	}
	
	public boolean saveNode(Node<E, K, A> node){
		
		return true;
	}
	
	public Node<E,K,A> getNode(){
		Node<E, K, A> node = this.vlfm.get(this.address);
		node.setNodeReference(this);
		
		return node;
	}
}
