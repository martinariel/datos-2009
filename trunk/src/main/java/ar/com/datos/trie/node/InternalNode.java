/**
 * 
 */
package ar.com.datos.trie.node;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class InternalNode <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	private K keyAtom;
	private A address;
	private E element;
	
	public InternalNode(int level, K keyAtom, A address, E element) {
		super(level);
		this.keyAtom = keyAtom;
		this.address = address;
		this.element = element;
	}
	
	@Override
	public boolean addElement(E Element) {
		NodeReference<E,K,A> childNodeReference = this.findChild(element);
		Node<E,K,A> child = null; // this null is temporary :P
		if (childNodeReference != null) {
			child = childNodeReference.getNode();
		} else {
			// TODO Hay que encontrar una forma de saber de que tipo es el 
			// nodo que voy a crear.
			// No estaria bueno que los nodos supieran esto, habria que pensar
			// donde poner esa logica.
			
			//childNodeReference = new NodeReference<E,K,A>(correspondiente);
			//child = new NodeConcretoParticular(this.level + 1, childNodeReference);
		}
	    boolean wasChildModified = child.addElement(element);

	    boolean toReturn = false;
	    
	    if (/* IamNewNode || */ wasChildModified) {
	        toReturn = this.nodeReference.saveNode(this);
	    }

	    return toReturn;		
	}

	private NodeReference<E, K, A> findChild(E element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E findElement(K key) {
		// TODO Auto-generated method stub
		return null;
	}
}
