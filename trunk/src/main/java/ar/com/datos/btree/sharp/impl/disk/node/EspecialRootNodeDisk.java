package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.ArrayList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.EspecialRootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.InternalNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.RootNodeSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.StateInternalNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractEspecialRootNode;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;
import ar.com.datos.test.btree.sharp.mock.disk.ListTestElementSerializer;
import ar.com.datos.test.btree.sharp.mock.disk.ListTestKeySerializer;
import ar.com.datos.test.btree.sharp.mock.disk.TestElementDisk;
import ar.com.datos.test.btree.sharp.mock.disk.TestKeyDisk;

/**
 * Nodo raiz especial en disco.
 * 
 * @author fvalido
 */
public class EspecialRootNodeDisk<E extends Element<K>, K extends Key> extends AbstractEspecialRootNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del �rbol (que incluir�n la configuraci�n del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un raiz especial en disco.
	 * Nota: El nodo no se grabar� en disco inmediatamente cuando se crea. El
	 *       grabado se har� cuando se llame por primera vez a {@link #postAddElement()}.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 * 
	 * @param btree
	 * �rbol que contiene a esta raiz.
	 */
	public EspecialRootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree) {
		super(bTreeSharpConfiguration, btree);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo raiz especial en disco.
	 * Nota: El nodo ya existe previamente en disco.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 *
	 * @param btree
	 * �rbol que contiene a esta raiz.
	 * 
	 * @param elements
	 * Elementos precontenidos en este nodo.
	 */
	public EspecialRootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration, BTreeSharp<E, K> btree, List<E> elements) {
		super(bTreeSharpConfiguration, btree);
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
		return this.bTreeSharpConfiguration.getStateInternalNodeSerializer().getDehydrateSize(this);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#postAddElement()
	 */
	@Override
	protected void postAddElement() throws BTreeException {
		if (this.myNodeReference == null) {
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getInternalNodesFileManager(), NodeType.ESPECIALROOT);
		}
		NodeReferenceDisk<E, K> myNodeReference = (NodeReferenceDisk<E, K>)this.myNodeReference;
		myNodeReference.saveNode(this);
	}

// FIXME: Esto no va m�s
//	/**
//	 * Obtiene las terceras partes pero considerando a los elements como de un tama�o fijo.
//	 * La lista de elementos original querar� vacia.
//	 * 
//	 * @see #getParts()
//	 */
//	private List<List<E>> getPartsAsIfElementsWhereFixedLength() {
//		int partSize = Math.round(((float)this.elements.size()) / 3F);
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
//		while (this.elements.size() > 0) {
//			part.add(this.elements.remove(0));
//		}
//		
//		return returnValue;
//	}
	
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractEspecialRootNode#getParts()
//	 */
//	@Override
//	protected List<List<E>> getParts() {
//		ListElementsSerializer<E, K> serializer = this.bTreeSharpConfiguration.getListElementsSerializer();
//		
//		// Obtengo la tercara parte considerando a todos los elementos como de un tama�o fijo.
//		List<List<E>> thirdParts = getPartsAsIfElementsWhereFixedLength();
//
//		// Obtengo cada una de las partes
//		List<E> left = thirdParts.get(0);
//		List<E> center = thirdParts.get(1);
//		List<E> right = thirdParts.get(2);
//		
//		// Reacomodo lo obtenido pero ahora calculando los tama�os (sin considerar tama�o fijo).
//		ThirdPartBalancer<E> thirdPartBalancer = new ThirdPartBalancer<E>();
//		thirdPartBalancer.balanceThirdPart(left, center, true, serializer, serializer.getDehydrateSize(right));
//		thirdPartBalancer.balanceThirdPart(center, right, true, serializer, serializer.getDehydrateSize(left));
//		
//		return thirdParts;
//	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getParts(java.util.List)
	 */
	@Override
	protected List<List<E>> getParts(List<E> rightNodeElements) {
		ListElementsSerializer<E, K> serializer = this.bTreeSharpConfiguration.getListElementsSerializer(); 

		// Obtengo la tercara parte considerando a todos los elementos como de un tama�o fijo.
		List<List<E>> parts = ThirdPartHelper.divideInThreeParts(this.elements);
		
		List<E> left = parts.get(0);
		List<E> center = parts.get(1);
		List<E> right = parts.get(2);
		
		// Reacomodo lo obtenido pero ahora calculando los tama�os (sin considerar tama�o fijo).
		ThirdPartHelper.balanceThirdPart(left, center, serializer, serializer.getDehydrateSize(right));
		ThirdPartHelper.balanceThirdPart(center, right, serializer, serializer.getDehydrateSize(left));
		
		return parts;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.node.DiskNode#setNodeReference(ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk)
	 */
	@Override
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference) {
		this.myNodeReference = nodeReference;
	}
	
	/**
	 * Elemento visible para su uso desde {@link InternalNodeSerializer}
	 */
	public List<E> getElements() {
		return this.elements;
	}

	// FIXME: Temporal. Todo lo que est� abajo es para pruebas de desarrollo.
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
		elements.add(new TestElementDisk("est�", 2));
		elements.add(new TestElementDisk("en", 3));
		elements.add(new TestElementDisk("el", 4));
		elements.add(new TestElementDisk("horizonte", 5));
		elements.add(new TestElementDisk("dice", 6));
		elements.add(new TestElementDisk("Fernando", 7));
		elements.add(new TestElementDisk("Birri", 8));
		elements.add(new TestElementDisk("Me", 9));
		
		EspecialRootNodeDisk<TestElementDisk, TestKeyDisk> realNode = new EspecialRootNodeDisk<TestElementDisk, TestKeyDisk>(bTreeSharpConfigurationDisk, null);
		realNode.setElements(elements);
		
		realNode.overflow(null, false, null);
	}
}
