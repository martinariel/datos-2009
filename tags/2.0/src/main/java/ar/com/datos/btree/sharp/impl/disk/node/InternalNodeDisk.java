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
		while (stateInternalNodeSerializer.getDehydrateSize((InternalNodeDisk<E, K>)rightNode) > this.bTreeSharpConfiguration.getMaxCapacityNode() 
				&& rightNode.getKeysNodes().size() > 1) {
			centerNode.getKeysNodes().add(new KeyNodeReference<E, K>(overflowKeyRight.getValue(), rightNode.getFirstChild()));
			tempKeyNodeReference = rightNode.getKeysNodes().remove(0);
			rightNode.setFirstChild(tempKeyNodeReference.getNodeReference());
			overflowKeyRight.setValue(tempKeyNodeReference.getKey());
		}
		
		// Ahora me puede haber quedado overflow en center (más difícil aún).
		while (stateInternalNodeSerializer.getDehydrateSize((InternalNodeDisk<E, K>)centerNode) > this.bTreeSharpConfiguration.getMaxCapacityNode()
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
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.node.DiskNode#setNodeReference(ar.com.datos.btree.sharp.impl.disk.node.NodeReferenceDisk)
	 */
	@Override
	public void setNodeReference(NodeReferenceDisk<E, K> nodeReference) {
		this.myNodeReference = nodeReference;
	}
}
