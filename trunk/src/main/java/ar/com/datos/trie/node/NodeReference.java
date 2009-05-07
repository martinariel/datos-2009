/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class NodeReference<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{
	private A keyAtom;
	private List<BlockAddress<Long, Short>> addresses = new ArrayList<BlockAddress<Long,Short>>();
	private BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> vlfm;
	
	public NodeReference(BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> vlfm, A keyAtom){
		this.vlfm = vlfm;
		this.keyAtom = keyAtom;
	}
	
	public A getKeyAtom(){
		return this.keyAtom;
	}
	
	/**
	 * Salva el nodo referenciado por esta NodeReference.
	 * Si el nodo no existia, se guarda su direccion y se devuelve true
	 * Si el nodo ya existia, se actualiza y se devuelve true si la direccion
	 * cambia o false en otro caso.
	 */
	public boolean saveNode(Node<E, K, A> node){
		BlockAddress<Long, Short> previousAddress, newAddress;
		previousAddress = node.getAddress();
		if (previousAddress==null){
			newAddress = this.vlfm.addEntity(node);
			this.addresses.add(newAddress);
			node.setAddress(newAddress);
		} else {
			newAddress = this.vlfm.updateEntity(previousAddress, node);
			if (newAddress.equals(previousAddress)){
				return false;
			} else {
				node.setAddress(newAddress);
				this.addresses.remove(previousAddress);
				this.addresses.add(newAddress);
			}
		}
		return true;
	}
	
	/**
	 * Devuelve el nodo referenciado por esta NodeReference (lo obtiene del vlfm)
	 * Null en otro caso (por ahora)
	 * @return {@link Node} el nodo referenciado por esta NodeReference.
	 * 
	 *  
	 *  Devolver el ultimo
	 */
	public Node<E,K,A> getNode(){
		if (this.addresses.size() >= 1){
			BlockAddress<Long, Short> address = this.addresses.get(this.addresses.size()-1);
			Node<E, K, A> node = this.vlfm.get(address);
			node.setAddress(address);
			return node;
		}
		return null;
	}

	public Iterator<Node<E,K,A>> iterator() {
		return new NodeReferenceIterator<E,K,A>(this.vlfm, this.addresses);
	}

	public List<BlockAddress<Long, Short>> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<BlockAddress<Long, Short>> addresses) {
		this.addresses = addresses;
	}
}

class 
NodeReferenceIterator<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> 
implements Iterator<Node<E,K,A>>{

	private int index;
	private List<BlockAddress<Long, Short>> addresses;
	private BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> vlfm;
	
	public NodeReferenceIterator(BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> vlfm,
			List<BlockAddress<Long, Short>> addresses){
		this.vlfm = vlfm;
		this.addresses = addresses;
		this.index = 0;
	}
	
	@Override
	public boolean hasNext() {
		return this.addresses.size() > this.index;
	}

	@Override
	public Node<E,K,A> next() {
		Node<E, K, A> retorno = this.vlfm.get(this.addresses.get(this.index));
		retorno.setAddress(this.addresses.get(this.index));
		this.index++;
		return retorno;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Remove not implemented for this iterator!");
	}
}
