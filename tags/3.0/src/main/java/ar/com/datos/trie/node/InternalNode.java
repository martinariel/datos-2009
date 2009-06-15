/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.ArrayList;
import java.util.Iterator;
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
		this.childNodesReferences = new ArrayList<NodeReference<E,K,A>>();
	}
	
	@Override
	public boolean addElement(E element) {
		boolean wasChildModified = false;
		boolean wasThisNodeModified = false;
		Node<E,K,A> child;
		
		// en el caso que la key termine en este nodo me guardo el elemento
		if (element.getKey().getRestOfKey(this.level+1).size() == 0){
			this.element = element;
			// indico que este nodo fue modificado
			return true;
		}
		
		// obtengo la NodeReference al nodo siguiente
		NodeReference<E,K,A> childNodeReference = this.findChildNodeReferenceFor(element.getKey());
		
		// si hay una NodeReference le pido el nodo al que apunta
		if (childNodeReference != null) {
			// (en el caso del anteultimo nivel, me devuelve el la ultima particion del nodo hoja)
			child = childNodeReference.getNode();
			
			// si no hay un nodo hijo, necesito crearlo
			if (child==null){
				child = this.nodeFactory.createNode(this.level+1);
			}
		} else {
			// si no habia una NodeReference para esta key
			// creo una nueva NodeReference y la guardo en este nodo
			childNodeReference = this.nodeFactory.createNodeReference(this.level+1, 
					element.getKey().getKeyAtom(this.level+1));
			this.childNodesReferences.add(childNodeReference);
			
			// creo el nodo hijo
			child = this.nodeFactory.createNode(this.level+1);
			
			// indico que este nodo fue modificado
			wasThisNodeModified = true;
		}
		
		try{
			// agrego el elemento y me fijo si el child fue modificado.
		    wasChildModified = child.addElement(element);
		    // si el child fue modificado guardo primero el child
		    if (wasChildModified){ 
		    	wasThisNodeModified |= childNodeReference.saveNode(child);
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
		
		// si la key termina en este nodo devuelvo el elemento que tenga
		if (key.getRestOfKey(this.level+1).size() == 0){
			return this.element;
		}
		
		// Este algoritmo es generico por eso trabaja con particiones de nodo
		// en realidad los unicos nodos particionados son los del ultimo nivel, 
		// con lo cual los nodos anteriores no tendran mas que una sola particion
		Node<E, K, A> nodePartition;
		
		// el iterador me permite aplicar el mismo algoritmo para los nodos
		// internos y para los nodos hojas. 
		// En el caso de nodos internos, siempre vamos a iterar una sola vez.
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
	
	/**
	 * Devuelve la NodeReference correspondiente a una key, para este nodo.
	 * Por ejemplo, si este es el nivel de la "A" en "MARCOS", estoy buscando
	 * la NodeReference correspondiente a la "R".
	 */
	private NodeReference<E,K,A> findChildNodeReferenceFor(K key) {
		NodeReference<E,K,A> nodeRef;
		Iterator<NodeReference<E,K,A>> it = this.childNodesReferences.iterator();
		while(it.hasNext()){
			nodeRef = it.next();
			if (nodeRef.getKeyAtom().equals(key.getKeyAtom(this.level+1))){
				return nodeRef;
			}
		}
		return null;
	}

	public List<NodeReference<E, K, A>> getChildNodesReferences() {
		return childNodesReferences;
	}

	public void setChildNodesReferences(
			List<NodeReference<E, K, A>> childNodesReferences) {
		this.childNodesReferences = childNodesReferences;
	}

	public E getElement() {
		return element;
	}

	public void setElement(E element) {
		this.element = element;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getLevel()).append(" : ").append(this.getElement()).append(" {");

		for (NodeReference<E,K,A> nr : this.childNodesReferences)
			sb.append("( ").append(nr.getKeyAtom()).append(" => ").append(nr.getAddresses()).append(" )");
		
		return sb.append("}").toString();
	}
}
