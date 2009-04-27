package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.EspecialRootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.InternalNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.RootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.StateInternalNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.test.btree.sharp.mock.disk.ListTestElementSerializer;
import ar.com.datos.test.btree.sharp.mock.disk.ListTestKeySerializer;
import ar.com.datos.test.btree.sharp.mock.disk.TestElementDisk;
import ar.com.datos.test.btree.sharp.mock.disk.TestKeyDisk;

/**
 * Nodo hoja en disco.
 * 
 * @author fvalido
 */
public class LeafNodeDisk<E extends Element<K>, K extends Key> extends AbstractLeafNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del árbol (que incluirán la configuración del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un nodo hoja en disco.
	 * Nota: El nodo no se grabará en disco inmediatamente cuando se crea. El
	 *       grabado se hará cuando se llame por primera vez a {@link #postAddElement()}.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 * 
	 * @param next
	 * Nodo posterior a este. En caso de ser el último debe ser null.
	 */
	public LeafNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		super(bTreeSharpConfiguration, previous, next);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo hoja en disco.
	 * Nota: El nodo ya existe previamente en disco.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 *
	 * @param previous
	 * Nodo anterior a este. En caso de ser el primero puede ser null.
	 *
	 * @param next
	 * Nodo posterior a este. En caso de ser el último puede ser null.
	 *
	 * @param elements
	 * Elementos precontenidos en este nodo.
	 */
	public LeafNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous,
						NodeReference<E, K> next, List<E> elements) {
		super(bTreeSharpConfiguration, previous, next);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		
		this.elements = elements;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#calculateNodeSize()
	 */
	@Override
	protected long calculateNodeSize() {
		return this.bTreeSharpConfiguration.getLeafNodeSerializer().getDehydrateSize(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		if (this.myNodeReference == null) {
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getLeafNodesFileManager(), NodeType.LEAF);
		}
		NodeReferenceDisk<E, K> myNodeReference = (NodeReferenceDisk<E, K>)this.myNodeReference;
		myNodeReference.saveNode(this);
	}

//	/**
//	 * Obtiene la tercera parte pero considerando a los elements como de un tamaño fijo.
//	 * La lista de elementos original se verá modificada sacando los elementos que se
//	 * devuelvan.
//	 * 
//	 * @see #getThirdPart(boolean)
//	 */
//	private List<E> getThirdPartAsIfElementsWhereFixedLength(boolean left) {
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
		ListElementsSerializer<E, K> serializer = this.bTreeSharpConfiguration.getListElementsSerializer(); 

		// Uno las listas
		List<E> source = new LinkedList<E>(this.elements);
		source.addAll(rightNodeElements);
		
		// Obtengo la tercara parte considerando a todos los elementos como de un tamaño fijo.
		List<List<E>> parts = ThirdPartHelper.divideInThreeParts(source);
		
		List<E> left = parts.get(0);
		List<E> center = parts.get(1);
		List<E> right = parts.get(2);
		
		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
		ThirdPartHelper.balanceThirdPart(left, center, serializer, serializer.getDehydrateSize(right));
		ThirdPartHelper.balanceThirdPart(center, right, serializer, serializer.getDehydrateSize(left));
		
		return parts;
	}

// FIXME: Esto no va más
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getThirdPart(boolean)
//	 */
//	@Override
//	protected List<E> getThirdPart(boolean left) {
//		ListElementsSerializer<E, K> serializer = this.bTreeSharpConfiguration.getListElementsSerializer(); 
//		
//		// Obtengo la tercara parte considerando a todos los elementos como de un tamaño fijo.
//		List<E> thirdPart = getThirdPartAsIfElementsWhereFixedLength(left);
//		
//		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
//		ThirdPartBalancer<E> thirdPartBalancer = new ThirdPartBalancer<E>();
//		thirdPartBalancer.balanceThirdPart(thirdPart, this.elements, left, serializer, 0L);
//		
//		return thirdPart;
//	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.node.DiskNode#setNodeReference(ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk)
	 */
	@Override
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference) {
		this.myNodeReference = nodeReference;
	}
	
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getNextNodeReference() {
		return (NodeReferenceDisk<E, K>)this.next;
	}
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getPreviousNodeReference() {
		return (NodeReferenceDisk<E, K>)this.previous;
	}
	/**
	 * Elemento visible para su uso desde {@link LeafNodeSerializer}
	 */
	public List<E> getElements() {
		return this.elements;
	}
	
	// FIXME: Temporal. Todo lo que está abajo es para pruebas de desarrollo.
	public void setElements(List<E> elements) {
		this.elements = elements;
	}
	
	public static void main(String[] args) {
		List<TestElementDisk> elements = new ArrayList<TestElementDisk>();
		
		BTreeSharpConfigurationDisk<TestElementDisk, TestKeyDisk> bTreeSharpConfigurationDisk = new BTreeSharpConfigurationDisk<TestElementDisk, TestKeyDisk>();
		
		ListKeysSerializer<TestKeyDisk> listKeysSerializer = new ListTestKeySerializer();
		ListElementsSerializer<TestElementDisk, TestKeyDisk> listElementsSerializer = new ListTestElementSerializer();
		
		InternalNodeSerializer<TestElementDisk, TestKeyDisk> internalNodeSerializer = new InternalNodeSerializer<TestElementDisk, TestKeyDisk>(listKeysSerializer, bTreeSharpConfigurationDisk); 
		RootNodeSerializer<TestElementDisk, TestKeyDisk> rootNodeSerializer = new RootNodeSerializer<TestElementDisk, TestKeyDisk>(listKeysSerializer, bTreeSharpConfigurationDisk);
		EspecialRootNodeSerializer<TestElementDisk, TestKeyDisk> especialRootNodeSerializer = new EspecialRootNodeSerializer<TestElementDisk, TestKeyDisk>(listElementsSerializer, bTreeSharpConfigurationDisk);
		StateInternalNodeSerializer<TestElementDisk, TestKeyDisk> stateInternalNodeSerializer = new StateInternalNodeSerializer<TestElementDisk, TestKeyDisk>(internalNodeSerializer, rootNodeSerializer, especialRootNodeSerializer);
		LeafNodeSerializer<TestElementDisk, TestKeyDisk> leafNodeSerializer = new LeafNodeSerializer<TestElementDisk, TestKeyDisk>(listElementsSerializer, bTreeSharpConfigurationDisk);

		short size = 100;
		
		bTreeSharpConfigurationDisk.setMaxCapacityInternalNode(size);
		bTreeSharpConfigurationDisk.setMaxCapacityLeafNode(size);
		bTreeSharpConfigurationDisk.setMaxCapacityRootNode(size);
		bTreeSharpConfigurationDisk.setLeafNodeSerializer(leafNodeSerializer);
		bTreeSharpConfigurationDisk.setStateInternalNodeSerializer(stateInternalNodeSerializer);
		bTreeSharpConfigurationDisk.setListElementsSerializer(listElementsSerializer);
		bTreeSharpConfigurationDisk.setListKeysSerializer(listKeysSerializer);

		elements.add(new TestElementDisk("Ella", 1));
		elements.add(new TestElementDisk("está", 2));
		elements.add(new TestElementDisk("en", 3));
		elements.add(new TestElementDisk("el", 4));
		elements.add(new TestElementDisk("horizonte", 5));
		elements.add(new TestElementDisk("dice", 6));
		elements.add(new TestElementDisk("Fernando", 7));
		elements.add(new TestElementDisk("Birri", 8));
		elements.add(new TestElementDisk("Me", 9));
		
		LeafNodeDisk<TestElementDisk, TestKeyDisk> realNode = new LeafNodeDisk<TestElementDisk, TestKeyDisk>(bTreeSharpConfigurationDisk, null, null);
		realNode.setElements(elements);
		
		elements = new ArrayList<TestElementDisk>();
		
		elements.add(new TestElementDisk("acerco", 10));
		elements.add(new TestElementDisk("dos", 11));
		elements.add(new TestElementDisk("pasos", 12));
		elements.add(new TestElementDisk("ella", 13));
		elements.add(new TestElementDisk("se", 14));
		elements.add(new TestElementDisk("aleja", 15));
		elements.add(new TestElementDisk("dos", 16));
		elements.add(new TestElementDisk("pasos", 17));
		
		LeafNodeDisk<TestElementDisk, TestKeyDisk> realNodeBrother = new LeafNodeDisk<TestElementDisk, TestKeyDisk>(bTreeSharpConfigurationDisk, null, null);
		realNodeBrother.setElements(elements);
		
		realNode.split(realNodeBrother, false, null);
	}
}
