package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.test.btree.sharp.mock.memory.TestElementMemory;
import ar.com.datos.test.btree.sharp.mock.memory.TestKeyMemory;

/**
 * Nodo hoja en memoria. No existe persistencia en esta implementaci�n.
 *
 * @author fvalido
 */
public final class LeafNodeMemory<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> {
	/**
	 * Permite construir un nodo hoja en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuracion del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 */
	public LeafNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		super(bTreeSharpConfiguration, previous, next);

		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() {
		// Nada para hacer
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected long calculateNodeSize() {
		return this.elements.size();
	}

//	 FIXME: Temporal. Seguramente va a quedar la versi�n de m�s abajo.	
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<E> getThirdPart(boolean left) {
//		List<E> thirdPart = new LinkedList<E>();
//		
//		int cantRemove = this.elements.size() / 3;
//		int removePosition = (left) ? 0 : cantRemove * 3;
//		int elementsInitialSize = this.elements.size();
//		for (int i = cantRemove * 3; i < elementsInitialSize; i++) {
//			thirdPart.add(this.elements.remove(removePosition));
//		}
//
//		return thirdPart;
//	}
	
//	FIXME: Esto no va m�s.
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<E> getThirdPart(boolean left) {
//		List<E> thirdPart = new LinkedList<E>();
//		
//		int cantRemove = Math.round(((float)this.elements.size()) / 3F);
//		int removeIndex = cantRemove * 2;
//		if (this.elements.size() % 3 == 2) {
//			removeIndex--;
//		} else {
//			if (this.elements.size() % 3 == 1) {
//				removeIndex++;
//			}
//		}
//		int removePosition = (left) ? 0 : removeIndex;
//		int elementsInitialSize = this.elements.size();
//		for (int i = removeIndex; i < elementsInitialSize; i++) {
//			thirdPart.add(this.elements.remove(removePosition));
//		}
//
//		return thirdPart;
//	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getParts(java.util.List)
	 */
	@Override
	protected List<List<E>> getParts(List<E> rightNodeElements) {
		// Uno las listas
		List<E> source = new LinkedList<E>(this.elements);
		source.addAll(rightNodeElements);
		
		// Extraigo las listas separadas.
		return ThirdPartHelper.divideInThreeParts(source);
	}
	
	// FIXME: Temporal. Todo lo que est� abajo es para pruebas de desarrollo.
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
		
		LeafNodeMemory<TestElementMemory, TestKeyMemory> realNode = new LeafNodeMemory<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size), null, null);
		realNode.setElements(elements);
		
		elements = new ArrayList<TestElementMemory>();
		
		elements.add(new TestElementMemory(10, "10"));
		elements.add(new TestElementMemory(11, "11"));
		elements.add(new TestElementMemory(12, "12"));
//		elements.add(new TestElement(13, "13"));
//		elements.add(new TestElement(14, "14"));
//		elements.add(new TestElement(15, "15"));
//		elements.add(new TestElement(16, "16"));
//		elements.add(new TestElement(17, "17"));
//		elements.add(new TestElement(18, "18"));
		
		LeafNodeMemory<TestElementMemory, TestKeyMemory> realNodeBrother = new LeafNodeMemory<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size), null, null);
		realNodeBrother.setElements(elements);
		
		realNode.split(realNodeBrother, false, null);
		

	}
}
