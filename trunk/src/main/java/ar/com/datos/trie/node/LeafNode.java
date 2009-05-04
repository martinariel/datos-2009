/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.List;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class 
LeafNode<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	// La porcion de clave de este nodo (this)
	protected A keyAtom;
	
	// Indica si en este nodo hay una terminacion (una clave termina en este nodo).
	// Ej: si se agrega "MAR" y este nodo tiene la "R" entonces, este element 
	// contendra la referencia al elemento referenciado por la clave "MAR".
	// Si ninguna clave termina en este nodo, entonces este element es null.
	protected E element;
	
	public LeafNode(int level, A keyAtom) {
		super(level);
		this.keyAtom = keyAtom;
	}

	@Override
	public boolean addElement(E Element) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public E findElement(K key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
