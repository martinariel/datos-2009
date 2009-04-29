package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.StateInternalNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.EspecialListForThirdPart;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo interno en disco.
 *
 * @author fvalido
 */
public final class InternalNodeDisk<E extends Element<K>, K extends Key> extends AbstractInternalNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del árbol (que incluirán la configuración del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un nodo interno en disco.
	 * 
	 * Nota: El nodo no se grabará en disco inmediatamente cuando se crea. El
	 *       grabado se hará cuando se llame por primera vez a postAddElement.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public InternalNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo interno en disco.
	 * Nota: El nodo ya existe previamente en disco.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 *
	 * @param firstChild
	 * NodeReference al primer nodo hijo.
	 *
	 * @param keysNodes
	 * Claves y referencias a hijos.
	 */
	public InternalNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration,
							NodeReference<E, K> firstChild, List<KeyNodeReference<E, K>> keysNodes) {
		super(bTreeSharpConfiguration);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		
		this.firstChild = firstChild;
		this.keysNodes = keysNodes;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		if (this.myNodeReference == null) {
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getInternalNodesFileManager(), NodeType.INTERNAL);
		}
		NodeReferenceDisk<E, K> myNodeReference = (NodeReferenceDisk<E, K>)this.myNodeReference;
		myNodeReference.saveNode(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected long calculateNodeSize() {
		return this.bTreeSharpConfiguration.getStateInternalNodeSerializer().getDehydrateSize(this);
	}
	
// FIXME
//	/**
//	 * Obtiene la tercera parte pero considerando a las keys como de un tamaño fijo.
//	 * La lista de keys original se verá modificada sacando las keys que se
//	 * devuelvan.
//	 * 
//	 * @see #getThirdPart(boolean)
//	 */
//	private List<KeyNodeReference<E, K>> getThirdPartAsIfElementsWhereFixedLength(boolean left) {
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
	
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
//		// TODO
//		return null;
//	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.btree.sharp.node.AbstractInternalNode, ar.com.datos.util.WrappedParam, ar.com.datos.util.WrappedParam)
	 */
	@Override
	protected void getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode, AbstractInternalNode<E, K> leftNode, AbstractInternalNode<E, K> centerNode, AbstractInternalNode<E, K> rightNode, WrappedParam<K> overflowKeyCenter, WrappedParam<K> overflowKeyRight) {
		ListKeysSerializer<K> serializer = this.bTreeSharpConfiguration.getListKeysSerializer(); 
		
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
		
		List<K> left = keyParts.get(0);
		List<K> center = keyParts.get(1);
		List<K> right = keyParts.get(2);
		
		EspecialListForThirdPart<K> eLeft = new EspecialListForThirdPart<K>(left, serializer, false);
		EspecialListForThirdPart<K> eCenterForLeft = new EspecialListForThirdPart<K>(center, serializer, true);
		EspecialListForThirdPart<K> eCenterForRight = new EspecialListForThirdPart<K>(center, serializer, false);
		EspecialListForThirdPart<K> eRight = new EspecialListForThirdPart<K>(right, serializer, true);
		
		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
		ThirdPartHelper.balanceThirdPart(eLeft, eCenterForLeft, eRight.size(), 1, 2);
		ThirdPartHelper.balanceThirdPart(eCenterForRight, eRight, eLeft.size(), 2, 2);
		
		// Recombino las listas divididas de keys con las KeyNodeReferences armando los nodos.
		ThirdPartHelper.combineKeysAndNodeReferences(sourceNodeReferences, keyParts, leftNode, centerNode, rightNode, overflowKeyCenter, overflowKeyRight);
		
		// Como el balanceo es hacia la derecha, puede pasar (difícil, pero puede) que el nodo derecho
		// quede en overflow. Si es así, trato de compensarlo hacia la izquierda.
		StateInternalNodeSerializer<E, K> stateInternalNodeSerializer = this.bTreeSharpConfiguration.getStateInternalNodeSerializer();
		KeyNodeReference<E, K> tempKeyNodeReference;
		while (stateInternalNodeSerializer.getDehydrateSize((InternalNodeDisk<E, K>)rightNode) > this.bTreeSharpConfiguration.getMaxCapacityInternalNode() 
				&& rightNode.getKeysNodes().size() > 1) {
			centerNode.getKeysNodes().add(new KeyNodeReference<E, K>(overflowKeyRight.getValue(), rightNode.getFirstChild()));
			tempKeyNodeReference = rightNode.getKeysNodes().remove(0);
			rightNode.setFirstChild(tempKeyNodeReference.getNodeReference());
			overflowKeyRight.setValue(tempKeyNodeReference.getKey());
		}
		
		// Ahora me puede haber quedado overflow en center (más difícil aún).
		while (stateInternalNodeSerializer.getDehydrateSize((InternalNodeDisk<E, K>)centerNode) > this.bTreeSharpConfiguration.getMaxCapacityInternalNode()
				&& centerNode.getKeysNodes().size() > 1) {
			leftNode.getKeysNodes().add(new KeyNodeReference<E, K>(overflowKeyCenter.getValue(), centerNode.getFirstChild()));
			tempKeyNodeReference = centerNode.getKeysNodes().remove(0);
			centerNode.setFirstChild(tempKeyNodeReference.getNodeReference());
			overflowKeyCenter.setValue(tempKeyNodeReference.getKey());
		}
		
		// Si left quedó también en overflow es porque el tamaño de los nodos fue mal definido
		// para los elementos que se quieren guardar. Se tirará una excepción en el serializer
		// correspondiente (no se trata el caso aquí).
	}

// FIXME
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key)
//	 */
//	@Override
//	protected List<List<KeyNodeReference<E, K>>> getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode) {
//		ListKeysSerializer<K> serializer = this.bTreeSharpConfiguration.getListKeysSerializer(); 
//		
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
//		List<K> left = keyParts.get(0);
//		List<K> center = keyParts.get(1);
//		List<K> right = keyParts.get(2);
//		
//		EspecialListForThirdPart<K> eLeft = new EspecialListForThirdPart<K>(left, serializer, false);
//		EspecialListForThirdPart<K> eCenterForLeft = new EspecialListForThirdPart<K>(center, serializer, true);
//		EspecialListForThirdPart<K> eCenterForRight = new EspecialListForThirdPart<K>(center, serializer, false);
//		EspecialListForThirdPart<K> eRight = new EspecialListForThirdPart<K>(right, serializer, true);
//		
//		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
//		ThirdPartHelper.balanceThirdPart(eLeft, eCenterForLeft, eRight.size());
//		ThirdPartHelper.balanceThirdPart(eCenterForRight, eRight, eLeft.size());
//		
//		// Como el balanceo es hacia la derecha, puede pasar (difícil, pero puede) que el nodo derecho
//		// quede en overflow. Si es así, trato de compensarlo hacia la izquierda.
//		while (eRight.size() + 1 > this.bTreeSharpConfiguration.getMaxCapacityInternalNode()) {
//			eRight.giveOneElementTo(eCenterForRight);
//		}
//		
//		// Ahora me puede haber quedado overflow en center (más dificil aún).
//		while (eCenterForLeft.size() + 1 > this.bTreeSharpConfiguration.getMaxCapacityInternalNode()) {
//			eCenterForLeft.giveOneElementTo(eLeft);
//		}
//		
//		// Si left quedó también en overflow es porque el tamaño de los nodos fue mal definido
//		// para los elementos que se quieren guardar. Se tirará una excepción en el serializer
//		// correspondiente (no se trata el caso aquí).
//		
//		// Recombino las listas divididas de keys con las KeyNodeReferences
//		return ThirdPartHelper.combineKeysAndNodeReferences(sourceKeyNodeReference, keyParts);
//	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.node.DiskNode#setNodeReference(ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk)
	 */
	@Override
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference) {
		this.myNodeReference = nodeReference;
	}

//	// FIXME: Temporal. Todo lo que está abajo es para pruebas de desarrollo.
//	public void setKeyNodes(List<KeyNodeReference<E, K>> keysNodes, NodeReference<E, K> firstChild) {
//		this.keysNodes = keysNodes;
//		this.firstChild = firstChild;
//	}
//	
//	public static class TestNode<E extends Element<K>, K extends Key> extends Node<E, K> {
//		String a;
//		
//		public TestNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, String a) {
//			super(bTreeSharpConfiguration);
//			this.a = a;
//		}
//		@Override
//		public String toString() {
//			return a;
//		}
//		
//		@Override
//		public KeyNodeReference<E, K> addElement(E element, NodeReference<E, K> brother, boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
//			return null;
//		}
//
//		@Override
//		protected long calculateNodeSize() {
//			return 0;
//		}
//
//		@Override
//		public E findElement(K key) throws BTreeException {
//			return null;
//		}
//
//		@Override
//		public ChainedNode<E, K> findNode(K key) throws BTreeException {
//			return null;
//		}
//
//		@Override
//		protected void postAddElement() throws BTreeException {
//		}
//
//		@Override
//		public NodeType getNodeType() {
//			return null;
//		}
//	}
//	
//	public static void main(String[] args) {
//		List<KeyNodeReference<TestElementDisk, TestKeyDisk>> keyNodes = new ArrayList<KeyNodeReference<TestElementDisk,TestKeyDisk>>();
//		
//		BTreeSharpConfigurationDisk<TestElementDisk, TestKeyDisk> bTreeSharpConfigurationDisk = new BTreeSharpConfigurationDisk<TestElementDisk, TestKeyDisk>();
//		
//		ListKeysSerializer<TestKeyDisk> listKeysSerializer = new ListTestKeySerializer();
//		ListElementsSerializer<TestElementDisk, TestKeyDisk> listElementsSerializer = new ListTestElementSerializer();
//		
//		InternalNodeSerializer<TestElementDisk, TestKeyDisk> internalNodeSerializer = new InternalNodeSerializer<TestElementDisk, TestKeyDisk>(listKeysSerializer, bTreeSharpConfigurationDisk); 
//		RootNodeSerializer<TestElementDisk, TestKeyDisk> rootNodeSerializer = new RootNodeSerializer<TestElementDisk, TestKeyDisk>(listKeysSerializer, bTreeSharpConfigurationDisk);
//		EspecialRootNodeSerializer<TestElementDisk, TestKeyDisk> especialRootNodeSerializer = new EspecialRootNodeSerializer<TestElementDisk, TestKeyDisk>(listElementsSerializer, bTreeSharpConfigurationDisk);
//		StateInternalNodeSerializer<TestElementDisk, TestKeyDisk> stateInternalNodeSerializer = new StateInternalNodeSerializer<TestElementDisk, TestKeyDisk>(internalNodeSerializer, rootNodeSerializer, especialRootNodeSerializer);
//		LeafNodeSerializer<TestElementDisk, TestKeyDisk> leafNodeSerializer = new LeafNodeSerializer<TestElementDisk, TestKeyDisk>(listElementsSerializer, bTreeSharpConfigurationDisk);
//
//		short size = 100;
//		
//		bTreeSharpConfigurationDisk.setMaxCapacityInternalNode(size);
//		bTreeSharpConfigurationDisk.setMaxCapacityLeafNode(size);
//		bTreeSharpConfigurationDisk.setMaxCapacityRootNode(size);
//		bTreeSharpConfigurationDisk.setLeafNodeSerializer(leafNodeSerializer);
//		bTreeSharpConfigurationDisk.setStateInternalNodeSerializer(stateInternalNodeSerializer);
//		bTreeSharpConfigurationDisk.setListElementsSerializer(listElementsSerializer);
//		bTreeSharpConfigurationDisk.setListKeysSerializer(listKeysSerializer);
//		
//		TestKeyDisk key = new TestKeyDisk(1);
//		NodeReference<TestElementDisk, TestKeyDisk> nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "a"), ); 
//		KeyNodeReference<TestElementDisk, TestKeyDisk> keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(2);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "b")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(3);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "c")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(4);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "d")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(5);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "e")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(6);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "f")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(7);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "g")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(8);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "h")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(9);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "i")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		InternalNodeMemory<TestElementMemory, TestKeyMemory> realNode = new InternalNodeDisk<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size));
//		realNode.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new TestNode<TestElementMemory, TestKeyMemory>(null, "x")));
//		
//		// ----------------------------------------------------------
//		
//		keyNodes = new ArrayList<KeyNodeReference<TestElementMemory,TestKeyMemory>>();
//		
//		key = new TestKeyDisk(10);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "j")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(11);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new TestElementDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "k")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(12);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "l")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(13);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKey>(null, "m")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKey>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(14);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "n")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(15);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "o")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//
//		key = new TestKeyDisk(16);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "p")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(17);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "q")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		key = new TestKeyDisk(18);
//		nodeRef = new NodeReferenceDisk<TestElementDisk, TestKeyDisk>(new InternalNodeDisk.TestNode<TestElementDisk, TestKeyDisk>(null, "r")); 
//		keyNodeReference = new KeyNodeReference<TestElementDisk, TestKeyDisk>(key, nodeRef);
//		keyNodes.add(keyNodeReference);
//		
//		InternalNodeMemory<TestElementMemory, TestKeyMemory> realNodeBrother = new InternalNodeDisk<TestElementMemory, TestKeyMemory>(new BTreeSharpConfigurationMemory<TestElementMemory, TestKeyMemory>(size, size));
//		realNodeBrother.setKeyNodes(keyNodes, new NodeReferenceMemory<TestElementMemory, TestKeyMemory>(new TestNode<TestElementMemory, TestKeyMemory>(null, "z")));
//
//		// -------------------------------------------------------------------------------
//		
//		realNode.split(realNodeBrother, false, new WrappedParam<TestKeyMemory>(new TestKeyMemory(20)));
//	}
}
