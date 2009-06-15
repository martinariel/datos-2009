package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.LeafNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractLeafNode;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.EspecialListForThirdPart;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;

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

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractLeafNode#getParts(java.util.List, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode, ar.com.datos.btree.sharp.node.AbstractLeafNode)
	 */
	@Override
	protected void getParts(List<E> rightNodeElements, AbstractLeafNode<E, K> leftNode, AbstractLeafNode<E, K> centerNode, AbstractLeafNode<E, K> rightNode) {
		ListElementsSerializer<E, K> serializer = this.bTreeSharpConfiguration.getListElementsSerializer(); 

		// Uno las listas
		List<E> source = new LinkedList<E>(this.elements);
		source.addAll(rightNodeElements);
		
		// Obtengo la tercara parte considerando a todos los elementos como de un tamaño fijo.
		List<List<E>> parts = ThirdPartHelper.divideInThreeParts(source);
		
		List<E> left = parts.remove(0);
		List<E> center = parts.remove(0);
		List<E> right = parts.remove(0);
		
		EspecialListForThirdPart<E> eLeft = new EspecialListForThirdPart<E>(left, serializer, false);
		EspecialListForThirdPart<E> eCenterForLeft = new EspecialListForThirdPart<E>(center, serializer, true);
		EspecialListForThirdPart<E> eCenterForRight = new EspecialListForThirdPart<E>(center, serializer, false);
		EspecialListForThirdPart<E> eRight = new EspecialListForThirdPart<E>(right, serializer, true);
		
		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
		ThirdPartHelper.balanceThirdPart(eLeft, eCenterForLeft, eRight.size(), 1, 2);
		ThirdPartHelper.balanceThirdPart(eCenterForRight, eRight, eLeft.size(), 2, 2);
		
		// Configuro los nodos con las partes que obtuve.
		leftNode.getElements().clear();
		leftNode.getElements().addAll(left);
		centerNode.getElements().clear();
		centerNode.getElements().addAll(center);
		rightNode.getElements().clear();
		rightNode.getElements().addAll(right);
		
		// Como el balanceo es hacia la derecha, puede pasar (difícil, pero puede) que el nodo derecho
		// quede en overflow. Si es así, trato de compensarlo hacia la izquierda.
		LeafNodeSerializer<E, K> leafNodeSerializer = this.bTreeSharpConfiguration.getLeafNodeSerializer();
		while (leafNodeSerializer.getDehydrateSize((LeafNodeDisk<E, K>)rightNode) > this.bTreeSharpConfiguration.getMaxCapacityNode() 
				&& rightNode.getElements().size() > 1) {
			centerNode.getElements().add(rightNode.getElements().remove(0));
		}
		
		// Ahora me puede haber quedado overflow en center (más difícil aún).
		while (leafNodeSerializer.getDehydrateSize((LeafNodeDisk<E, K>)centerNode) > this.bTreeSharpConfiguration.getMaxCapacityNode()
				&& centerNode.getElements().size() > 1) {
			leftNode.getElements().add(centerNode.getElements().remove(0));
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
}
