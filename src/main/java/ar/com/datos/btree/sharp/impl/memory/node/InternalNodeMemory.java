package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.ChainedNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.test.btree.sharp.mock.memory.TestElementMemory;
import ar.com.datos.test.btree.sharp.mock.memory.TestKeyMemory;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo interno en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class InternalNodeMemory<E extends Element<K>, K extends Key> extends AbstractInternalNode<E, K> {
	/**
	 * Permite construir un nodo interno en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public InternalNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
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
		return this.keysNodes.size();
	}

// FIXME: Temporal. Seguramente va a quedar la versión de más abajo.
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
//		List<KeyNodeReference<E, K>> thirdPart = new LinkedList<KeyNodeReference<E, K>>();
//		
//		int cantRemove = this.keysNodes.size() / 3;
//
//		if (left) {
//			thirdPart.add(new KeyNodeReference<E, K>(null, this.firstChild));
//		}
//		int keysNodesInitialSize = this.keysNodes.size();
//		int removePosition = (left) ? 0 : cantRemove * 3;
//		for (int i = cantRemove * 3; i < keysNodesInitialSize; i++) {
//			thirdPart.add(this.keysNodes.remove(removePosition));
//		}
//		if (left) {
//			KeyNodeReference<E, K> tempKeyNodeReference = this.keysNodes.remove(0);
//			this.firstChild = tempKeyNodeReference.getNodeReference();
//			thirdPart.add(new KeyNodeReference<E, K>(tempKeyNodeReference.getKey(), null));
//		}
//		
//		return thirdPart;
//	}

//	FIXME: Sacar esto
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
//		List<KeyNodeReference<E, K>> thirdPart = new LinkedList<KeyNodeReference<E, K>>();
//		
//		int cantRemove = Math.round(((float)this.keysNodes.size()) / 3F);
//
//		if (left) {
//			thirdPart.add(new KeyNodeReference<E, K>(null, this.firstChild));
//		}
//		int keysNodesInitialSize = this.keysNodes.size();
//		if (left) {
//			keysNodesInitialSize--;
//		}
//		int removeIndex = cantRemove * 2;
//		if (this.keysNodes.size() % 3 == 2) {
//			removeIndex--;
//		}
//		int removePosition = (left) ? 0 : removeIndex;
//
//		for (int i = removeIndex; i < keysNodesInitialSize; i++) {
//			thirdPart.add(this.keysNodes.remove(removePosition));
//		}
//		if (left) {
//			KeyNodeReference<E, K> tempKeyNodeReference = this.keysNodes.remove(0);
//			this.firstChild = tempKeyNodeReference.getNodeReference();
//			thirdPart.add(new KeyNodeReference<E, K>(tempKeyNodeReference.getKey(), null));
//		}
//		
//		return thirdPart;
//	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.util.WrappedParam, ar.com.datos.util.WrappedParam)
	 */
	@Override
	protected void getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode, AbstractInternalNode<E, K> leftNode, AbstractInternalNode<E, K> centerNode, AbstractInternalNode<E, K> rightNode, WrappedParam<K> overflowKeyCenter, WrappedParam<K> overflowKeyRight) {
		// Creo una lista de NodeReferences y otra de Keys
		List<NodeReference<E, K>> sourceNodeReferences = new LinkedList<NodeReference<E, K>>();
		List<K> sourceKeys = new LinkedList<K>();
		
		sourceNodeReferences.add(this.firstChild); // Agrego la primer referencia de este nodo.
		Iterator<KeyNodeReference<E, K>> keyNodeReferenceIt;
		KeyNodeReference<E, K> currentKeyNodeReference;
		
		// Agrego claves de este nodo y resto de las referencias.
		keyNodeReferenceIt = this.keysNodes.iterator();
		while (keyNodeReferenceIt.hasNext()) {
			currentKeyNodeReference = keyNodeReferenceIt.next();
			sourceNodeReferences.add(currentKeyNodeReference.getNodeReference());
			sourceKeys.add(currentKeyNodeReference.getKey());
		}
		// Agrego la primer referencia del nodo derecho y la clave del padre del nodo derecho. 
		sourceNodeReferences.add(firstChildRightNode);
		sourceKeys.add(fatherKeyRigthNode);
		// Agrego claves del nodo derecho y resto de las referencias.
		keyNodeReferenceIt = keysNodesRightNode.iterator();
		while (keyNodeReferenceIt.hasNext()) {
			currentKeyNodeReference = keyNodeReferenceIt.next();
			sourceNodeReferences.add(currentKeyNodeReference.getNodeReference());
			sourceKeys.add(currentKeyNodeReference.getKey());
		}		
		
		// Divido la lista de Keys.
		List<List<K>> keyParts = ThirdPartHelper.divideInThreePartsEspecial(sourceKeys);
		
		// Recombino las listas divididas de keys con las KeyNodeReferences armando los nodos.
		ThirdPartHelper.combineKeysAndNodeReferences(sourceNodeReferences, keyParts, leftNode, centerNode, rightNode, overflowKeyCenter, overflowKeyRight);
	}

//	FIXME No se usa más
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key)
//	 */
//	@Override
//	protected List<List<KeyNodeReference<E, K>>> getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode) {
//		// Creo una sola lista.
//		List<KeyNodeReference<E, K>> sourceKeyNodeReference = new LinkedList<KeyNodeReference<E,K>>();
//		sourceKeyNodeReference.add(new KeyNodeReference<E, K>(null, this.firstChild));
//		sourceKeyNodeReference.addAll(this.keysNodes);
//		sourceKeyNodeReference.add(new KeyNodeReference<E, K>(fatherKeyRigthNode, firstChildRightNode));
//		sourceKeyNodeReference.addAll(keysNodesRightNode);
//		
//		// Extraigo una lista de Keys.
//		Iterator<KeyNodeReference<E, K>> itKeyNodeReference = sourceKeyNodeReference.iterator();
//		List<K> sourceKeys = new LinkedList<K>();
//		itKeyNodeReference.next();
//		while (itKeyNodeReference.hasNext()) {
//			sourceKeys.add(itKeyNodeReference.next().getKey());
//		}
//		
//		// Divido la lista de Keys.
//		List<List<K>> keyParts = ThirdPartHelper.divideInThreePartsEspecial(sourceKeys);
//		
//		// Recombino las listas divididas de keys con las KeyNodeReferences
//		return ThirdPartHelper.combineKeysAndNodeReferences(sourceKeyNodeReference, keyParts);
//	}

	// FIXME: Temporal. Todo lo que está abajo es para pruebas de desarrollo.
	public void setKeyNodes(List<KeyNodeReference<E, K>> keysNodes, NodeReference<E, K> firstChild) {
		this.keysNodes = keysNodes;
		this.firstChild = firstChild;
	}
	
	public static class TestNode<E extends Element<K>, K extends Key> extends Node<E, K> {
		String a;
		
		public TestNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, String a) {
			super(bTreeSharpConfiguration);
			this.a = a;
		}
		@Override
		public String toString() {
			return a;
		}
		
		@Override
		public KeyNodeReference<E, K> addElement(E element, NodeReference<E, K> brother, boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
			return null;
		}

		@Override
		protected long calculateNodeSize() {
			return 0;
		}

		@Override
		public E findElement(K key) throws BTreeException {
			return null;
		}

		@Override
		public ChainedNode<E, K> findNode(K key) throws BTreeException {
			return null;
		}

		@Override
		protected void postAddElement() throws BTreeException {
		}

		@Override
		public NodeType getNodeType() {
			return null;
		}
	}
	
	public static void main(String[] args) {
		List<KeyNodeReference<TestElementMemory, TestKeyMemory>> keyNodes = new ArrayList<KeyNodeReference<TestElementMemory,TestKeyMemory>>();
		
		short size = 3;
		
		TestKeyMemory key = new TestKeyMemory(1);
		NodeReference<TestElementMemory, TestKeyMemory> nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "a")); 
		KeyNodeReference<TestElementMemory, TestKeyMemory> keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKeyMemory(2);
		nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "b")); 
		keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKeyMemory(3);
		nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "c")); 
		keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(4);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "d")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(5);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "e")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(6);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "f")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKey(7);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "g")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(8);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "h")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(9);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "i")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
		
		InternalNodeMemory<TestElementMemory, TestKeyMemory> realNode = new InternalNodeMemory<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size));
		realNode.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new TestNode<TestElementMemory, TestKeyMemory>(null, "x")));
		
		// ----------------------------------------------------------
		
		keyNodes = new ArrayList<KeyNodeReference<TestElementMemory,TestKeyMemory>>();
		
		key = new TestKeyMemory(10);
		nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "j")); 
		keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKeyMemory(11);
		nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "k")); 
		keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKeyMemory(12);
		nodeRef = new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new InternalNodeMemory.TestNode<TestElementMemory, TestKeyMemory>(null, "l")); 
		keyNodeReference = new KeyNodeReference<TestElementMemory, TestKeyMemory>(key, nodeRef);
		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(13);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "m")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(14);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "n")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKey(15);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "o")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKey(16);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "p")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(17);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "q")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(18);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "r")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
		
		InternalNodeMemory<TestElementMemory, TestKeyMemory> realNodeBrother = new InternalNodeMemory<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size));
		realNodeBrother.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new TestNode<TestElementMemory, TestKeyMemory>(null, "z")));

		// -------------------------------------------------------------------------------
		
		realNode.split(realNodeBrother, false, new WrappedParam<TestKeyMemory>(new TestKeyMemory(20)));
	}
}
