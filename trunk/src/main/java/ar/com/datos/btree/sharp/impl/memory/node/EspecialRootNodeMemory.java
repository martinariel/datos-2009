package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.test.btree.sharp.mock.memory.TestElementMemory;
import ar.com.datos.test.btree.sharp.mock.memory.TestKeyMemory;

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
	 * 
	 * @param btree
	 * Árbol que contiene a esta raiz.
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
	protected long calculateNodeSize() {
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
	protected List<List<E>> getParts(List<E> rightNodeElements) {
		// Extraigo las listas separadas.
		return ThirdPartHelper.divideInThreeParts(this.elements);
	}
	
	// FIXME: Temporal. Todo lo que está abajo es para pruebas de desarrollo.
	public void setElements(List<E> elements) {
		this.elements = elements;
	}
	
	public static void main(String[] args) {
		List<TestElementMemory> elements = new ArrayList<TestElementMemory>();
		
		short size = 3;
		
		elements.add(new TestElementMemory(1, "1"));
		elements.add(new TestElementMemory(2, "2"));
		elements.add(new TestElementMemory(3, "3"));
//		elements.add(new TestElement(4, "4"));
//		elements.add(new TestElement(5, "5"));
//		elements.add(new TestElement(6, "6"));
//		elements.add(new TestElement(7, "7"));
//		elements.add(new TestElement(8, "8"));
//		elements.add(new TestElement(9, "9"));
		
		EspecialRootNodeMemory<TestElementMemory, TestKeyMemory> realNode = new EspecialRootNodeMemory<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size), null);
		realNode.setElements(elements);
		
		realNode.overflow(null, false, null);
	}
}
