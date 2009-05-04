/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class InternalNode <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	// Referencia a los proximos nodos (nodos hijos). Estos nodos hijos estan 
	// en el nivel (this.level+1) del Trie, un nivel mas que este nodo (this).
	protected List<NodeReference<E,K,A>> childNodesReferences;
	
	// La porcion de clave de este nodo (this)
	protected A keyAtom;
	
	// Indica si en este nodo hay una terminacion (una clave termina en este nodo).
	// Ej: si se agrega "MAR" y este nodo tiene la "R" entonces, este element 
	// contendra la referencia al elemento referenciado por la clave "MAR".
	// Si ninguna clave termina en este nodo, entonces este element es null.
	protected E element;
	
	// clase necesaria para crear nodos. Tiene la logica de saber que tipo de
	//nodo crear en funcion del nivel en el que esta este nodo.
	protected NodeFactory<E,K,A> nodeFactory;
	
	
	public InternalNode(int level, A keyAtom, NodeFactory<E,K,A> nodeFactory) {
		super(level);
		this.element = null;
		this.keyAtom = keyAtom;
		this.nodeFactory = nodeFactory;
		this.childNodesReferences = new LinkedList<NodeReference<E,K,A>>();
	}
	
	@Override
	public boolean addElement(E element) {
		boolean wasChildModified = false;
		boolean wasThisNodeModified = false;
		
		K key = element.getKey();
		NodeReference<E,K,A> childNodeReference = this.findChildNodeReferenceFor(element);
		Node<E,K,A> child;
		
		if (childNodeReference != null) {
			child = childNodeReference.getNode();
		} else {
			int childLevel = this.level+1;
			A childKeyAtom = key.getKeyAtom(childLevel);
			
			// creo el nodo hijo y una referencia a ese nodo
			child = this.nodeFactory.createNode(childLevel, childKeyAtom);
			childNodeReference = this.nodeFactory.createNodeReference(childLevel, childKeyAtom);
			
			// me guardo la referencia ya que es un nodo hijo nuevo. 
			this.childNodesReferences.add(childNodeReference);
			
			wasThisNodeModified = true;
		}
		
		// agrego el elemento y me fijo si el child fue modificado.
	    wasChildModified = child.addElement(element);
	    
	    // si el child fue modificado guardo primero el child
	    if (wasChildModified){ 
	    	childNodeReference.saveNode(child);
	    }
	    
	    // devuelvo si este nodo fue modificado (solo si tuve que crear un subnodo)
	    return wasThisNodeModified;		
	}

	@Override
	public E findElement(K key) {
		NodeReference<E,K,A> childNodeReference = this.findChildNodeReferenceFor(element);
		return childNodeReference.getNode().findElement(key);
	}
	
	private NodeReference<E, K, A> findChildNodeReferenceFor(E element) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
