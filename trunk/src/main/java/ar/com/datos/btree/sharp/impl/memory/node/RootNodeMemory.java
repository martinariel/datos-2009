package ar.com.datos.btree.sharp.impl.memory.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.memory.BTreeSharpConfigurationMemory;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.ChainedNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.test.btree.sharp.mock.TestElement;
import ar.com.datos.test.btree.sharp.mock.TestKey;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo raiz en memoria. No existe persistencia en esta implementación.
 *
 * @author fvalido
 */
public final class RootNodeMemory<E extends Element<K>, K extends Key> extends AbstractRootNode<E, K> {
	/**
	 * Permite construir un nodo raiz en memoria.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public RootNodeMemory(BTreeSharpConfigurationMemory<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		this.myNodeReference = new NodeReferenceMemory<E, K>(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected int calculateNodeSize() {
		return this.keysNodes.size();
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
//	 * @see ar.com.datos.btree.sharp.node.AbstractRootNode#getParts()
//	 */
//	@Override
//	protected List<List<KeyNodeReference<E, K>>> getParts() {
//		int partSize = calculateNodeSize() / 3;
//		
//		List<KeyNodeReference<E, K>> part = new LinkedList<KeyNodeReference<E,K>>();;
//		part.add(new KeyNodeReference<E, K>(null, this.firstChild));
//		List<List<KeyNodeReference<E, K>>> returnValue = new LinkedList<List<KeyNodeReference<E,K>>>();
//		for (int i = 0; i < 3; i++) {
//			for (int j = 0; j < partSize && this.keysNodes.size() > 0; j++) {
//				part.add(this.keysNodes.remove(0));
//			}
//			returnValue.add(part);
//			part = new LinkedList<KeyNodeReference<E,K>>();
//		}
//		
//		return returnValue;
//	}	
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractRootNode#getParts()
	 */
	@Override
	protected List<List<KeyNodeReference<E, K>>> getParts() {
		int partSize =  Math.round(((float)this.keysNodes.size()) / 3F);;
		if (this.keysNodes.size() % 3 == 1) {
			partSize++;
		}
		
		List<KeyNodeReference<E, K>> part = new LinkedList<KeyNodeReference<E,K>>();;
		List<List<KeyNodeReference<E, K>>> returnValue = new LinkedList<List<KeyNodeReference<E,K>>>();
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<KeyNodeReference<E,K>>();
			for (int j = 0; j < partSize - ((i == 0) ? 1 : 0) && this.keysNodes.size() > 0; j++) {
				part.add(this.keysNodes.remove(0));
			}
			returnValue.add(part);

		}
		returnValue.get(0).add(0, new KeyNodeReference<E, K>(null, this.firstChild));
		while (this.keysNodes.size() > 0) {
			part.add(this.keysNodes.remove(0));
		}
		
		return returnValue;
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected int calculateNodeSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public E findElement(K key) throws BTreeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ChainedNode<E, K> findNode(K key) throws BTreeException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void postAddElement() throws BTreeException {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void main(String[] args) {
		List<KeyNodeReference<TestElement, TestKey>> keyNodes = new ArrayList<KeyNodeReference<TestElement,TestKey>>();
		
		short size = 3;
		
		TestKey key = new TestKey(1);
		NodeReference<TestElement, TestKey> nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "a")); 
		KeyNodeReference<TestElement, TestKey> keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(2);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "b")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);

		key = new TestKey(3);
		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "c")); 
		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
		keyNodes.add(keyNodeReference);
		
//		key = new TestKey(4);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "d")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(5);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "e")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(6);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "f")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKey(7);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "g")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(8);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "h")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKey(9);
//		nodeRef = new NodeReferenceMemory<TestElement, TestKey>(new RootNodeMemory.TestNode<TestElement, TestKey>(null, "i")); 
//		keyNodeReference = new KeyNodeReference<TestElement, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
		
		RootNodeMemory<TestElement, TestKey> realNode = new RootNodeMemory<TestElement, TestKey>(new BTreeSharpConfigurationMemory<TestElement, TestKey>(size, size));
		realNode.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElement, TestKey>(new TestNode<TestElement, TestKey>(null, "x")));

		realNode.overflow(null, false, null);
	}
}
