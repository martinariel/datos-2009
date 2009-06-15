package ar.com.datos.btree.sharp.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo interno genérico. Debe ser extendido para que sea usable.
 *
 * @author fvalido
 */
public abstract class AbstractInternalNode<E extends Element<K>, K extends Key> extends Node<E, K> {
	/** Primera referencia a nodo. */
	protected NodeReference<E, K> firstChild;
	
	/** Pares Clave-Referencia a Nodo. */
	protected List<KeyNodeReference<E, K>> keysNodes;

	/**
	 * Permite crear un nodo interno.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public AbstractInternalNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		this.keysNodes = new LinkedList<KeyNodeReference<E, K>>();
	}

	/**
	 * Permite obtener la posición del subnodo correspondiente a la key pasada.
	 * Se devuelve -1 para el firstChild o un valor superior a cero correspondiente a la
	 * posición dentro de keysNodes.
	 */
	private int findNodeReferencePositionForKey(K key) {
		Iterator<KeyNodeReference<E, K>> it = this.keysNodes.iterator();
		KeyNodeReference<E, K> currentKeyNodeReference = null;
		int elementNodeReferencePosition = -2;
		int i = 0;
		while (it.hasNext() && elementNodeReferencePosition == -2) {
			currentKeyNodeReference = it.next();
			if (key.compareTo(currentKeyNodeReference.getKey()) < 0) {
				// Si el elemento actual es mayor o igual entonces encontré la posición.
				elementNodeReferencePosition = i - 1;
			}

			i++;
		}
		if (elementNodeReferencePosition == -2) {
			// Si element node era null entonces está en el nodo correspondiente al
			// último nodeReference
			elementNodeReferencePosition = this.keysNodes.size() - 1;
		}

		return elementNodeReferencePosition;
		
	}

	/**
	 * Permite obtener el subnodo correspondiente a la key pasada.
	 */
	private NodeReference<E, K> findNodeReferenceForKey(K key) {
		int position = findNodeReferencePositionForKey(key);
		NodeReference<E, K> elementNodeReference;
		if (position == -1) {
			elementNodeReference = this.firstChild;
		} else {
			elementNodeReference = this.keysNodes.get(position).getNodeReference();
		}
		
		return elementNodeReference;
	}

	/**
	 * Divide un nodo y un hermano que están llenos.
	 * Se creará una nodo nuevo. Los elementos quedarán repartidos entre los tres
	 * nodos (este, el hermano, y el creado), en 2/3 en cada uno.
	 *
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 * 
	 * @param fatherKey
	 * Clave que apunta al nodo de la derecha. Debe ser actualizada con la nueva
	 * clave que lo apunta luego del split.
	 * 
	 * @return
	 * Un {@link KeyNodeReference} cuya clave será la correspondiente al primer
	 * {@link Element} del nodo creado, y el NodeReference apuntará al nodo creado.
	 */
	private KeyNodeReference<E, K> split(AbstractInternalNode<E, K> brother,
										boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Trabajo a los nodos como left, center y right, donde center es el nuevo nodo.
		AbstractInternalNode<E, K> left = (leftBrother) ? brother : this;
		AbstractInternalNode<E, K> right = (leftBrother) ? this : brother;
		// Creo un nuevo nodo.
		AbstractInternalNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createInternalNode(this.bTreeSharpConfiguration);

		// Extraigo los tercios
		WrappedParam<K> overflowKey = new WrappedParam<K>();
		left.getParts(right.firstChild, right.keysNodes, fatherKey.getValue(), left, center, right, overflowKey, fatherKey); // Método template.
		
		// Método template
		center.postAddElement();
		
		return new KeyNodeReference<E, K>(overflowKey.getValue(), center.myNodeReference);
	}
	
	/**
	 * Agrega el elemento pasado en el nodo hijo que corresponda delegando el agregado en el metodo
	 * add del nodo hijo correspondiente.
	 * @return
	 * Lo que devuelva el agregado en el hijo correspondiente. 
	 */
	private KeyNodeReference<E, K> addElementInChild(E element, 
								WrappedParam<Integer> rightNodePosition, 
								WrappedParam<K> rightNodeKey) throws BTreeException {
		// Encuentro el nodo en el que debería estar el elemento.
		NodeReference<E, K> elementNodeReference = findNodeReferenceForKey(element.getKey());
		Node<E, K> elementNode = elementNodeReference.getNode();

		// Obtengo el hermano izquierdo (si es el de más a la izquierda, el derecho) del nodo en el que debería
		// estar el elemento.
		int position = findNodeReferencePositionForKey(element.getKey());
		boolean leftBrother = (position != -1);
		int brotherPosition = (!leftBrother) ? 0 : position - 1;
		NodeReference<E, K> elementNodeBrotherReference;
		if (brotherPosition == -1) {
			elementNodeBrotherReference = this.firstChild;
		} else {
			elementNodeBrotherReference = this.keysNodes.get(brotherPosition).getNodeReference();
		}
		
		// Establezco el parametro de devolución.
		if (position == -1) {
			rightNodePosition.setValue(0);
		} else {
			rightNodePosition.setValue(position);
		}
		
		if (position != -1) {
			rightNodeKey.setValue(this.keysNodes.get(position).getKey());
		} else {
			rightNodeKey.setValue(this.keysNodes.get(0).getKey());
		}
		KeyNodeReference<E, K> returnValue = elementNode.addElement(element, elementNodeBrotherReference, leftBrother, rightNodeKey); 
		
		return returnValue; 
	}
	
	/**
	 * Pasa elementos desde this al hermano hasta que no hay overflow en this.
	 * 
	 * @param brother
	 * Hermano usado para balancear.
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 * @param fatherKey
	 * Clave que debe pasarse al hermano al transmitirse una NodeReference.
	 * 
	 * @return
	 * Clave que deberá referenciar al nodo de la derecha.
	 */
	private K giveOverflowToBrother(AbstractInternalNode<E, K> brother,
									   boolean leftBrother, K fatherKey) {
		// Saco el elemento que sobra y lo agrego donde corresponde en el hermano.
		K overflowKey;
		NodeReference<E, K> tempNodeReference;
		KeyNodeReference<E, K> tempKeyNodeReference;
		if (leftBrother) {
			// Quita la primer nodeReference del nodo de la derecha (this)
			tempKeyNodeReference = this.keysNodes.remove(0);
			tempNodeReference = this.firstChild;
			this.firstChild = tempKeyNodeReference.getNodeReference();
			// Esta es la clave que voy a subir
			overflowKey = tempKeyNodeReference.getKey();

			// Agrego en el hermano (de la izquierda) una keyNodeReference formada por la clave
			// del padre por la que entraron aquí y la referencia que saqué de aquí recién.
			tempKeyNodeReference = new KeyNodeReference<E, K>(fatherKey, tempNodeReference);
			brother.keysNodes.add(tempKeyNodeReference);
		} else {
			// Quita la última nodeReference del nodo de la izquierda (this)
			tempKeyNodeReference = this.keysNodes.remove(this.keysNodes.size() - 1);
			// Esta es la clave que voy a subir
			overflowKey = tempKeyNodeReference.getKey();

			// Agrego en el hermano (de la derecha) una keyNodeReference formada por la clave
			// del padre por la que entraron aqui y la referencia que saque de aquí recién
			tempNodeReference = tempKeyNodeReference.getNodeReference();
			tempKeyNodeReference = new KeyNodeReference<E, K>(fatherKey, brother.firstChild);
			brother.firstChild = tempNodeReference;
			brother.keysNodes.add(0, tempKeyNodeReference);
		}
		
		// Si sigue habiendo overflow, sigo pasando contenido.
		if (calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityNode()) {
			overflowKey = giveOverflowToBrother(brother, leftBrother, overflowKey);
		}
		
		return overflowKey;
	}
	
	/**
	 * Utiliza el hermano para agregar el elemento que sobra; si también hay overflow
	 * ahi, hace un split.
	 * 
	 * @param brother
	 * Hermano usado para balancear.
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquiero o el derecho.
	 * @param fatherKey
	 * Clave dentro del nodo padre por la cual se está entrando en este nodo para agregar a element.
	 * @return
	 * En caso de necesitar al hermano, pero sin que se produzca un split, la
	 * {@link Key} corresponderá a la clave a subir (la primera del nodo de la derecha)
	 * y el {@link NodeReference} será null, es decir, el invocante tendrá que 
	 * reemplazar la {@link Key} que apuntaba a este nodo por la recibida.
	 * En caso de haber split la {@link Key} correspondera a la clave a subir (la primera
	 * del nodo del centro, es decir el generado) y el {@link NodeReference} corresponderá
	 * al nodo generado.
	 * 
	 * @throws BTreeException
	 * Si hay algun problema manejando el overflow.
	 */
	protected KeyNodeReference<E, K> overflow(AbstractInternalNode<E, K> brother,
			 								boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		KeyNodeReference<E, K> returnValue = null;
		
		// Le paso el overflow al hermano.
		K overflowKey = giveOverflowToBrother(brother, leftBrother, fatherKey.getValue());
		
		// Si hubo overflow en el hermano, esta vez hago un split
		if (brother.calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityNode()) {
			fatherKey.setValue(overflowKey);
			returnValue = split(brother, leftBrother, fatherKey);
		} else {
			// Si no lo hubo, reemplazo fatherKey con la Key correspondiente al primer
			// elemento del nodo de la derecha.
			fatherKey.setValue(overflowKey);
		}
		
		// Método template.
		brother.postAddElement();
		
		return returnValue;
	}

	/**
	 * Obtiene la capacidad máxima del nodo.
	 */
	protected int getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityNode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#addElement(ar.com.datos.btree.elements.Element, ar.com.datos.btree.sharp.node.NodeReference, boolean, ar.com.datos.util.WrappedParam)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KeyNodeReference<E, K> addElement(E element, NodeReference<E, K> brother, boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		WrappedParam<Integer> rightNodePositionParam = new WrappedParam<Integer>();
		
		// Delego el agregado del elemento en el nodo hijo que debe tenerlo.
		WrappedParam<K> rightNodeKey = new WrappedParam<K>(fatherKey.getValue());
		KeyNodeReference<E, K> keyNodeReferenceOverflow = addElementInChild(element, rightNodePositionParam, rightNodeKey); 
		
		KeyNodeReference<E, K> returnValue = null;

		
		int rightNodePosition = rightNodePositionParam.getValue();
		KeyNodeReference<E, K> oldKeyNodeReference, newKeyNodeReference;

		// Si hubo split al agregar en el hijo...
		if (keyNodeReferenceOverflow != null) {
			// Si hubo Split

			// Entonces keyNodeReferenceOverflow contiene la clave de la primer clave del nuevo nodo
			// y una referencia al nuevo nodo.
			// Debo agregar esta clave y referencia y actualizar la clave de la referencia al nodo que
			// estaba a la derecha.
				
			// Actualizo la clave de la referencia al nodo que estaba a la derecha.
			oldKeyNodeReference = this.keysNodes.get(rightNodePosition);
			newKeyNodeReference = new KeyNodeReference<E, K>(rightNodeKey.getValue(), oldKeyNodeReference.getNodeReference());
			this.keysNodes.set(rightNodePosition, newKeyNodeReference);
				
			// Y ahora agrego el nuevo nodo a mis referencias junto con su clave.
			this.keysNodes.add(rightNodePosition, keyNodeReferenceOverflow);
			
			if (calculateNodeSize() > this.getNodeMaxCapacity()) {
				// Si mi nodo quedo con overflow
				returnValue = overflow((AbstractInternalNode)brother.getNode(), leftBrother, fatherKey);
			}
		} else {
			if (rightNodeKey.getValue() != null) {
				// La key correspondiente a la referencia al nodo de la derecha, debe
				// cambiar por la que subieron, sin tocar ninguna NodeReference.
				oldKeyNodeReference = this.keysNodes.get(rightNodePosition);
				newKeyNodeReference = new KeyNodeReference<E, K>(rightNodeKey.getValue(), oldKeyNodeReference.getNodeReference());
				this.keysNodes.set(rightNodePosition, newKeyNodeReference);
				// Debo verificar si el reemplazo de esta clave generó overflow.
				if (calculateNodeSize() > this.getNodeMaxCapacity()) {
					returnValue = overflow((AbstractInternalNode)brother.getNode(), leftBrother, fatherKey);
				}
			}
		}
		
		if (rightNodeKey.getValue() != null) {
			// Si hubo cambios en el nodo...			
			postAddElement();
		}

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#findElement(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public E findElement(K key) throws BTreeException {
		NodeReference<E, K> nodeReference = findNodeReferenceForKey(key);

		return nodeReference.getNode().findElement(key);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#findNode(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public ChainedNode<E, K> findNode(K key) throws BTreeException {
		NodeReference<E, K> nodeReference = findNodeReferenceForKey(key);

		return nodeReference.getNode().findNode(key);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#getNodeType()
	 */
	@Override
	public NodeType getNodeType() {
		return NodeType.INTERNAL;
	}
	
	/**
	 * Método para ser usado por la implementación de {@link #getParts(NodeReference, List, Key, AbstractInternalNode, AbstractInternalNode, AbstractInternalNode, WrappedParam, WrappedParam)}
	 */
	public List<KeyNodeReference<E, K>> getKeysNodes() {
		return this.keysNodes;
	}
	
	/**
	 * Método para ser usado por la implementación de {@link #getParts(NodeReference, List, Key, AbstractInternalNode, AbstractInternalNode, AbstractInternalNode, WrappedParam, WrappedParam)}
	 */
	public NodeReference<E, K> getFirstChild() {
		return this.firstChild;
	}
	
	/**
	 * Método para ser usado por la implementación de {@link #getParts(NodeReference, List, Key, AbstractInternalNode, AbstractInternalNode, AbstractInternalNode, WrappedParam, WrappedParam)}
	 */
	public void setFirstChild(NodeReference<E, K> firstChild) {
		this.firstChild = firstChild;
	}
	
	/**
	 * Juntanto las {@link KeyNodeReference} de este nodo con los del derecho que recibe,
	 * obtiene 3 partes (3 listas) de igual tamaño (o lo más próximo posible).
	 * Para hacer la unión junta el firstChild del rightNode con la fatherKeyRigthNode.
	 * Con estas partes terminará de construir los nodos pasados.
	 * Además se deja en overflowKeyCenter la clave que debe usarse para apuntar al dicho
	 * nodo. Idem para overflowKeyRight.
	 *
	 * Patrón de diseño Template.
	 */
	protected abstract void getParts(NodeReference<E, K> firstChildRightNode, 
										List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode, 
										AbstractInternalNode<E, K> leftNode, AbstractInternalNode<E, K> centerNode,
										AbstractInternalNode<E, K> rightNode, WrappedParam<K> overflowKeyCenter,
										WrappedParam<K> overflowKeyRight);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String returnValue = "<<" + this.firstChild.toString() + "||";
		Iterator<KeyNodeReference<E, K>> it = this.keysNodes.iterator();
		while (it.hasNext()) {
			returnValue += it.next().toString();
			if (it.hasNext()) {
				returnValue += "||";
			}
		}
		returnValue += ">>";

		return returnValue;
	}
}
