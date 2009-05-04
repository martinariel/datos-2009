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
public class NodeReference<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{
	private A keyAtom;
	private BlockAddress<Long, Short> address;
	private VariableLengthFileManager<Node<E,K,A>> vlfm;
	
	public NodeReference(VariableLengthFileManager<Node<E,K,A>> vlfm, A keyAtom, 
			BlockAddress<Long, Short> address){
		this.vlfm = vlfm;
		this.keyAtom = keyAtom;
		this.address = address;
	}
	public NodeReference(VariableLengthFileManager<Node<E,K,A>> vlfm, A keyAtom){
		this.vlfm = vlfm;
		this.keyAtom = keyAtom;
	}
	
	/**
	 * Salva el nodo referenciado por esta NodeReference.
	 * Si el nodo no existia, se guarda su direccion y se devuelve true
	 * Si el nodo ya existia, se actualiza y se devuelve true si la direccion
	 * cambia o false en otro caso.
	 */
	public boolean saveNode(Node<E, K, A> node){
		if (this.address == null){
			this.address = this.vlfm.addEntity(node);
		} else {
			BlockAddress<Long, Short> newAddress = this.vlfm.updateEntity(this.address, node);
			if (newAddress.getBlockNumber() == this.address.getBlockNumber()
					&& newAddress.getObjectNumber() == this.address.getObjectNumber()){
				return false;
			} else {
				this.address = newAddress;
			}
		}	
		return true;
	}
	
	/**
	 * Devuelve el nodo referenciado por esta NodeReference (lo obtiene del vlfm)
	 * Null en otro caso (por ahora)
	 * @return {@link Node} el nodo referenciado por esta NodeReference. 
	 */
	public Node<E,K,A> getNode(){
		if (this.address == null){
			return null; // TODO: Lanzar excepcion!
		}
		Node<E, K, A> node = this.vlfm.get(this.address);
		
		return node;
	}
}
