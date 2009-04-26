package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
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
import ar.com.datos.test.btree.sharp.mock.TestElement;
import ar.com.datos.test.btree.sharp.mock.TestKey;
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
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
	 */
	@Override
	protected List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
		List<KeyNodeReference<E, K>> thirdPart = new LinkedList<KeyNodeReference<E, K>>();
		
		int cantRemove = Math.round(((float)this.keysNodes.size()) / 3F);

		if (left) {
			thirdPart.add(new KeyNodeReference<E, K>(null, this.firstChild));
		}
		int keysNodesInitialSize = this.keysNodes.size();
		if (left) {
			keysNodesInitialSize--;
		}
		int removeIndex = cantRemove * 2;
		if (this.keysNodes.size() % 3 == 2) {
			removeIndex--;
		}
		int removePosition = (left) ? 0 : removeIndex;

		for (int i = removeIndex; i < keysNodesInitialSize; i++) {
			thirdPart.add(this.keysNodes.remove(removePosition));
		}
		if (left) {
			KeyNodeReference<E, K> tempKeyNodeReference = this.keysNodes.remove(0);
			this.firstChild = tempKeyNodeReference.getNodeReference();
			thirdPart.add(new KeyNodeReference<E, K>(tempKeyNodeReference.getKey(), null));
		}
		
		return thirdPart;
	}
	
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
		List<KeyNodeReference<TestElement, TestKey>> keyNodes = new ArrayList<KeyNodeReference<TestElement,TestKey>>();
		
		short size = 3;
		
		TestKey key = new TestKey(1);
		NodeReference<TestElement, TestKey> nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "a")); 
		KeyNodeReference<TestElement, TestKey> keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(2);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "b")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(3);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "c")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);
		
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
		
		InternalNodeMemory<TestElement, TestKey> realNode = new InternalNodeMemory<TestElement, TestKey>(new BTreeSharpConfigurationMemory<TestElement, TestKey>(size, size));
		realNode.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElement, TestKey>(new TestNode<TestElement, TestKey>(null, "x")));
		
		// ----------------------------------------------------------
		
		keyNodes = new ArrayList<KeyNodeReference<TestElement,TestKey>>();
		
		key = new TestKey(10);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "j")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(11);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "k")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(12);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new InternalNodeMemory.TestNode<TestElement, TestKey>(null, "l")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);
		
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
		
		InternalNodeMemory<TestElement, TestKey> realNodeBrother = new InternalNodeMemory<TestElement, TestKey>(new BTreeSharpConfigurationMemory<TestElement, TestKey>(size, size));
		realNodeBrother.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElement, TestKey>(new TestNode<TestElement, TestKey>(null, "z")));

		// -------------------------------------------------------------------------------
		
		realNode.split(realNodeBrother, false, new WrappedParam<TestKey>(new TestKey(20)));
	}
}
