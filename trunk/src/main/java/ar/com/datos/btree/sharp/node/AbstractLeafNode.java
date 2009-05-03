package ar.com.datos.btree.sharp.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo hoja genérico. Debe ser extendido para que sea usable.
 *
 * @author fvalido
 */
public abstract class AbstractLeafNode<E extends Element<K>, K extends Key> extends Node<E, K> implements ChainedNode<E, K> {
	/** Elementos que guarda el nodo. */
	protected List<E> elements;

	/** Siguiente hoja. Puede ser null si no la hay. */
	protected NodeReference<E, K> next;

	/** Hoja anterior. Puede ser null si esta es la primer hoja. */
	protected NodeReference<E, K> previous;

	/**
	 * Permite crear un nodo hoja.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuracion del nodo.
	 * @param previous
	 * {@link NodeReference} la hoja anterior a este. Puede ser null si es el primero.
	 */
	public AbstractLeafNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, NodeReference<E, K> previous, NodeReference<E, K> next) {
		super(bTreeSharpConfiguration);
		this.elements = new LinkedList<E>();
		this.previous = previous;
		this.next = next;
	}


	/**
	 * Permite obtener la posición en la que debería estar la {@link Key}
	 * pasada dentro de este nodo (haya o no overflow).
	 */
	private int findInsertPosition(K key) {
		int i = 0;

		Iterator<E> it = this.elements.iterator();
		boolean found = false;
		Key currentKey = null;
		while (it.hasNext() && !found) {
			currentKey = it.next().getKey();
			found = key.compareTo(currentKey) <= 0;
			i++;
		}

		if (this.elements.size() > 0 && key.compareTo(currentKey) <= 0) {
			i--;
		}

		return i;
	}

	/**
	 * Divide una hoja y un hermano que están llenos.
	 * Se creará una hoja nueva. Los elementos quedarán repartidos entre las tres
	 * hojas (esta, el hermano, y la creada), en 2/3 en cada una.
	 *
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 *
	 * @param fatherKey
	 * Debe reemplazarse por la primera clave del nodo de la derecha una vez resuelto el
	 * split.
	 * 
	 * @return
	 * Un {@link KeyNodeReference} cuya clave será la correspondiente al primer
	 * {@link Element} del nodo creado, y el NodeReference apuntará al nodo creado.
	 */
	private KeyNodeReference<E, K> split(AbstractLeafNode<E, K> brother,
										boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Trabajo a los nodos como left, center y right, donde center es el nuevo nodo.
		AbstractLeafNode<E, K> left = (leftBrother) ? brother : this;
		AbstractLeafNode<E, K> right = (leftBrother) ? this : brother;
		// Creo una nueva hoja cuyo nodo anterior será left, y el siguiente será el right.
		AbstractLeafNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createLeafNode(this.bTreeSharpConfiguration, left.myNodeReference, right.myNodeReference);

		// Extraigo los tercios
		left.getParts(right.elements, left, center, right); // Método template.
		
		// Método template.
		center.postAddElement();
		
		// Pongo las referencias a anterior y siguiente donde corresponde.
		left.next = center.myNodeReference;
		right.previous = center.myNodeReference;
		
		fatherKey.setValue(right.elements.get(0).getKey());
		
		return new KeyNodeReference<E, K>(center.elements.get(0).getKey(), center.myNodeReference);
	}


	/**
	 * Pasa elementos desde this al hermano hasta que no hay overflow en this.
	 * 
	 * @param brother
	 * Hermano usado para balancear.
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 */
	private void giveOverflowToBrother(AbstractLeafNode<E, K> brother,
									   boolean leftBrother) {
		// Saco el elemento que sobra.
		int removePosition = (leftBrother) ? 0 : this.elements.size() - 1;
		E overflowElement = this.elements.remove(removePosition);
		
		// Lo agrego en el hermano
		brother.insertElement(overflowElement);
		
		// Si sigue habiendo overflow, sigo pasando contenido.
		if (calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityNode()) {
			giveOverflowToBrother(brother, leftBrother);
		}
	}
	
	/**
	 * Utiliza el hermano para agregar el elemento que sobra; si también hay overflow
	 * ahi, hace un split.
	 * 
	 * @param brother
	 * Hermano usado para balancear.
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 * @param fatherKey
	 * Debe reemplazarse por la primera clave del nodo de la derecha una vez resuelto el
	 * overflow.
	 * 
	 * @return
	 * En caso de haber split la {@link Key} correspondera a la clave a subir (la primera
	 * del nodo del centro, es decir el generado) y el {@link NodeReference} corresponderá
	 * al nodo generado.
	 * Si no hay split será null.
	 * 
	 * @throws BTreeException
	 * Si hay algun problema manejando el overflow.
	 */
	protected KeyNodeReference<E, K> overflow(AbstractLeafNode<E, K> brother,
			 								boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		KeyNodeReference<E, K> returnValue = null;
		
		// Le paso el overflow al hermano.
		giveOverflowToBrother(brother, leftBrother);
		
		// Si hubo overflow en el hermano, esta vez hago un split
		if (brother.calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityNode()) {
			returnValue = split(brother, leftBrother, fatherKey);
		} else {
			// Si no lo hubo, reemplazo fatherKey con la Key correspondiente al primer
			// elemento del nodo de la derecha.
			K overflowKey = (leftBrother) ? this.elements.get(0).getKey() : brother.elements.get(0).getKey(); 
			fatherKey.setValue(overflowKey);
		}
		
		// Método template.
		brother.postAddElement();
		
		return returnValue;
	}
	
	/**
	 * Permite actualizar el originalElement con el newElement.
	 *
	 * @return
	 * true si el elemento sufrio una modificacion que pudo haber modificado
	 * al nodo; false en caso contrario.
	 * Esto queda a criterio del implementador de {@link Element}.
	 */
	private boolean updateElement(E originalElement, E newElement) {
		return originalElement.updateElement(newElement);
	}
	
	/**
	 * Inserta el {@link Element} en la posición adecuada de la colección de elementos,
	 * o lo actualiza si ya existía.
	 * 
	 * @return
	 * Booleano indicando si el nodo fue modificado.
	 */
	protected boolean insertElement(E element) {
		int insertPosition = findInsertPosition(element.getKey());
		boolean modified = true;
		if (this.elements.size() > 0 && this.elements.size() != insertPosition &&
				element.getKey().compareTo(this.elements.get(insertPosition).getKey()) == 0) {
			// El elemento de la posición de inserción tiene la misma clave que el actual.
			modified = updateElement(this.elements.get(insertPosition), element);
		} else {
			// El elemento de la posición de inserción tiene distinta clave que el actual.
			this.elements.add(insertPosition, element);
		}
		
		return modified;
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
	public KeyNodeReference<E, K> addElement(E element, 
											 NodeReference<E, K> brother,
											 boolean leftBrother,
											 WrappedParam<K> fatherKey) throws BTreeException {
		KeyNodeReference<E, K> returnValue = null;
		fatherKey.setValue(null);
		
		boolean modified = insertElement(element);
		if (modified) {
			if (calculateNodeSize() > getNodeMaxCapacity()) {
				// Si mi nodo quedo con overflow
				returnValue = overflow((AbstractLeafNode)brother.getNode(), leftBrother, fatherKey);
			}

			// Método template.
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
		Iterator<E> it = this.elements.iterator();
		boolean ready = false;
		E element = null;

		while (it.hasNext() && !ready) {
			element = it.next();
			ready = (key.compareTo(element.getKey()) <= 0);
		}

		if (key.compareTo(element.getKey()) != 0) {
			element = null;
		}

		return element;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#getNodeType()
	 */
	@Override
	public NodeType getNodeType() {
		return NodeType.LEAF;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#findNode(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public ChainedNode<E, K> findNode(K key) throws BTreeException {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.ChainedNode#findNextElement(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public E findNextElement(K key) {
		Iterator<E> it = this.elements.iterator();
		boolean ready = false;
		E element = null;

		while (it.hasNext() && !ready) {
			element = it.next();
			ready = (key.compareTo(element.getKey()) < 0);
		}

		if (key.compareTo(element.getKey()) >= 0) {
			element = null;
		}

		return element;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.ChainedNode#findPreviousElement(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public E findPreviousElement(K key) {
		ListIterator<E> it = this.elements.listIterator(this.elements.size());
		boolean ready = false;
		E element = null;

		while (it.hasPrevious() && !ready) {
			element = it.previous();
			ready = (key.compareTo(element.getKey()) > 0);
		}

		if (key.compareTo(element.getKey()) <= 0) {
			element = null;
		}

		return element;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.ChainedNode#getNextNode()
	 */
	@Override
	public final ChainedNode<E, K> getNextNode() throws BTreeException {
		Node<E, K> node = null;
		if (this.next != null) {
			node = this.next.getNode();
		}

		return (ChainedNode<E, K>)node;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.ChainedNode#getPreviousNode()
	 */
	@Override
	public final ChainedNode<E, K> getPreviousNode() throws BTreeException {
		Node node = null;
		if (this.previous != null) {
			node = this.previous.getNode();
		}

		return (ChainedNode<E, K>)node;
	}

	/**
	 * Método para ser usado por la implementación de {@link #getParts(List, AbstractLeafNode, AbstractLeafNode, AbstractLeafNode)}
	 */
	public List<E> getElements() {
		return this.elements;
	}
	
	/**
	 * Juntando los elements de este nodo con los del derecho que recibe, obtiene
	 * 3 partes (3 listas) de igual tamaño (o lo más próximo posible). Con ellas
	 * terminará de construir los nodos pasados.
	 * 
	 * Patrón de diseño Template.
	 */
	protected abstract void getParts(List<E> rightNodeElements, AbstractLeafNode<E, K> leftNode,
									AbstractLeafNode<E, K> centerNode, AbstractLeafNode<E, K> rightNode);
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String returnValue = "@@";
		Iterator<E> it = this.elements.iterator();
		while (it.hasNext()) {
			returnValue += it.next().toString();
			if (it.hasNext()) {
				returnValue += "||";
			}
		}
		returnValue += "@@";

		return returnValue;
	}
}
