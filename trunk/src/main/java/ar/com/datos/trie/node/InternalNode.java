/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;
import ar.com.datos.trie.node.exception.LeafPartitionLimitException;

/**
 * @author marcos
 *
 */
public class InternalNode <E extends Element<K, A>, K extends Key<A>,A extends KeyAtom> extends Node<E,K,A> {
	
	// Referencia a los proximos nodos (nodos hijos). Estos nodos hijos estan 
	// en el nivel (this.level+1) del Trie, un nivel mas que este nodo (this).
	protected List<NodeReference<E,K,A>> childNodesReferences;
	
	// Indica si en este nodo hay una terminacion (una clave termina en este nodo).
	// Ej: si se agrega "MAR" y este nodo tiene la "R" entonces, este element 
	// contendra la referencia al elemento referenciado por la clave "MAR".
	// Si ninguna clave termina en este nodo, entonces este element es null.
	protected E element;
	
	// clase necesaria para crear nodos. Tiene la logica de saber que tipo de
	//nodo crear en funcion del nivel en el que esta este nodo.
	protected NodeFactory<E,K,A> nodeFactory;
	
	
	public InternalNode(int level, NodeFactory<E,K,A> nodeFactory) {
		super(level);
		this.element = null;
		this.nodeFactory = nodeFactory;
		this.childNodesReferences = new LinkedList<NodeReference<E,K,A>>();
	}
	
	@Override
	public boolean addElement(E element) {
		boolean wasChildModified = false;
		boolean wasThisNodeModified = false;
		Node<E,K,A> child;
		NodeReference<E,K,A> childNodeReference = this.findChildNodeReferenceFor(element.getKey());
		
		if (childNodeReference != null) {
			child = childNodeReference.getNode();
			if (child==null){
				child = this.nodeFactory.createNode(this.level+1);
			}
		} else {
			// creo una nueva NodeReference y la guardo en este nodo
			childNodeReference = this.nodeFactory.createNodeReference(this.level+1, 
					element.getKey().getKeyAtom(this.level+1));
			this.childNodesReferences.add(childNodeReference);
			
			// creo el nodo hijo
			child = this.nodeFactory.createNode(this.level+1);
			
			wasThisNodeModified = true;
		}
		
		try{
			// agrego el elemento y me fijo si el child fue modificado.
		    wasChildModified = child.addElement(element);
		    // si el child fue modificado guardo primero el child
		    if (wasChildModified){ 
		    	childNodeReference.saveNode(child);
		    }
		} catch(LeafPartitionLimitException e){
			// estoy en un nodo hoja, en una particion que esta completa
			// debo crear una nueva particion para el nodo
		    child = this.nodeFactory.createNode(this.level+1);
		    // agregarle el elemento a esa particion del nodo
		    child.addElement(element);
		    // salvo la particion del nodo recien creada
		    childNodeReference.saveNode(child);
		    // este nodo (this) se modifico porque agregue una nueva referencia
		    wasThisNodeModified = true;
		    
		}
		
	    // devuelvo si este nodo fue modificado (solo si tuve que crear un subnodo)
	    return wasThisNodeModified;		
	}

	@Override
	public E findElement(K key) {
		E element; // elemento a devolver (si se encuentra en el Trie)
		
		// Este algoritmo es generico por eso trabaja con particiones de nodo
		// en realidad los unicos nodos particionados son los del ultimo nivel, 
		// con lo cual los nodos anteriores no tendran mas que una sola particion
		Node<E, K, A> nodePartition;
		
		NodeReference<E,K,A> childNodeReference = this.findChildNodeReferenceFor(key);
		if (childNodeReference != null){
			Iterator<Node<E,K,A>> it = childNodeReference.iterator();
			while(it.hasNext()){
				nodePartition = it.next();
				element = nodePartition.findElement(key);
				if (element != null){
					return element;
				}
			}	
		}
		return null;
	}
	
	private NodeReference<E,K,A> findChildNodeReferenceFor(K key) {
		NodeReference<E,K,A> nodeRef;
		Iterator<NodeReference<E,K,A>> it = this.childNodesReferences.iterator();
		while(it.hasNext()){
			nodeRef = it.next();
			if (nodeRef.getKeyAtom() == key.getKeyAtom(this.level+1)){
				return nodeRef;
			}
		}
		return null;
	}
}
