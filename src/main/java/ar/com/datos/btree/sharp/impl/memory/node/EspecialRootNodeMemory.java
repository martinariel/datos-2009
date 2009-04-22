package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.test.btree.sharp.mock.TestElement;
import ar.com.datos.test.btree.sharp.mock.TestKey;

/**
 * Nodo raiz especial en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class EspecialRootNodeMemory<E extends Element<K>, K extends Key> extends AbstractEspecialRootNode<E, K> {
	/**
	 * Permite construir un nodo raiz especial en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public EspecialRootNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		super(bTreeSharpConfiguration, btree);
		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected int calculateNodeSize() {
		return this.elements.size();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		// Nada para hacer.
	}

//	 FIXME: Temporal. Seguramente va a quedar la versión de más abajo.	
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractEspecialRootNode#getParts()
//	 */
//	@Override
//	protected List<List<E>> getParts() {
//		int partSize = calculateNodeSize() / 3;
//		
//		List<E> part = new LinkedList<E>();;
//		List<List<E>> returnValue = new LinkedList<List<E>>();
//		for (int i = 0; i < 3; i++) {
//			part = new LinkedList<E>();
//			for (int j = 0; j < partSize && this.elements.size() > 0; j++) {
//				part.add(this.elements.remove(0));
//			}
//			returnValue.add(part);
//		}
//		
//		return returnValue;
//	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractEspecialRootNode#getParts()
	 */
	@Override
	protected List<List<E>> getParts() {
		int partSize =  Math.round(((float)this.elements.size()) / 3F);;
		
		List<E> part = new LinkedList<E>();;
		List<List<E>> returnValue = new LinkedList<List<E>>();
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<E>();
			for (int j = 0; j < partSize && this.elements.size() > 0; j++) {
				part.add(this.elements.remove(0));
			}
			returnValue.add(part);
		}
		while (this.elements.size() > 0) {
			part.add(this.elements.remove(0));
		}
		
		return returnValue;
	}
	
	// FIXME: Temporal. Todo lo que está abajo es para pruebas de desarrollo.
	public void setElements(List<E> elements) {
		this.elements = elements;
	}
	
	public static void main(String[] args) {
		List<TestElement> elements = new ArrayList<TestElement>();
		
		short size = 3;
		
		elements.add(new TestElement(1, "1"));
		elements.add(new TestElement(2, "2"));
		elements.add(new TestElement(3, "3"));
//		elements.add(new TestElement(4, "4"));
//		elements.add(new TestElement(5, "5"));
//		elements.add(new TestElement(6, "6"));
//		elements.add(new TestElement(7, "7"));
//		elements.add(new TestElement(8, "8"));
//		elements.add(new TestElement(9, "9"));
		
		EspecialRootNodeMemory<TestElement, TestKey> realNode = new EspecialRootNodeMemory<TestElement, TestKey>(new BTreeSharpConfigurationMemory<TestElement, TestKey>(size, size), null);
		realNode.setElements(elements);
		
		realNode.overflow(null, false, null);
	}
	
	
}
