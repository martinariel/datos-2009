package ar.com.datos.btree.sharp.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
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
	 *
	 * @return
	 * Un {@link KeyNodeReference} cuya clave será la correspondiente al primer
	 * {@link Element} del nodo creado, y el NodeReference apuntará al nodo creado.
	 */
	// FIXME: Este método debe ser private. Está como público para el desarrollo.
	public KeyNodeReference<E, K> split(AbstractInternalNode<E, K> brother,
										boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Trabajo a los nodos como left, center y rigth, donde center es el nuevo nodo.
		AbstractInternalNode<E, K> left = (leftBrother) ? brother : this;
		AbstractInternalNode<E, K> rigth = (leftBrother) ? this : brother;
		// Creo un nuevo nodo.
		AbstractInternalNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpFactory().createInternalNode(this.bTreeSharpConfiguration);
		
		// Extraigo el último tercio de left y el primer tercio de rigth y con ellos armo los elements
		// del nuevo nodo.
		List<KeyNodeReference<E, K>> leftParts = left.getThirdPart(false); // Método template.
		List<KeyNodeReference<E, K>> rigthParts = rigth.getThirdPart(true); // Método template.
		KeyNodeReference<E, K> tempKeyNodeReference = leftParts.remove(0);
		K overflowKey = tempKeyNodeReference.getKey();
		center.firstChild = tempKeyNodeReference.getNodeReference();
		center.keysNodes.addAll(leftParts);
		tempKeyNodeReference = rigthParts.remove(0);
		center.keysNodes.add(new KeyNodeReference<E, K>(fatherKey.getValue(), tempKeyNodeReference.getNodeReference()));
		center.keysNodes.addAll(rigthParts);

		fatherKey.setValue(center.keysNodes.remove(center.keysNodes.size() - 1).getKey());
		
		// Método template
		center.postAddElement();
		
		return new KeyNodeReference<E, K>(overflowKey, center.myNodeReference);
	}
	
	/**
	 * Agrega el elemento pasado en el nodo hijo que corresponda delegando el agregado en el metodo
	 * add del nodo hijo correspondiente.
	 * @return
	 * Lo que devuelva el agregado en el hijo correspondiente. 
	 */
	private KeyNodeReference<E, K> addElementInChild(E element, 
								WrappedParam<Integer> rigthNodePosition, 
								WrappedParam<K> rigthNodeKey) throws BTreeException {
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
			rigthNodePosition.setValue(0);
		} else {
			rigthNodePosition.setValue(position);
		}
		
		if (position != -1) {
			rigthNodeKey.setValue(this.keysNodes.get(position).getKey());
		} else {
			rigthNodeKey.setValue(this.keysNodes.get(0).getKey());
		}
		KeyNodeReference<E, K> returnValue = elementNode.addElement(element, elementNodeBrotherReference, leftBrother, rigthNodeKey); 
		
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
		if (calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityLeafNode()) {
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
		if (brother.calculateNodeSize() > this.bTreeSharpConfiguration.getMaxCapacityLeafNode()) {
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
	protected short getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityInternalNode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#addElement(ar.com.datos.btree.elements.Element, ar.com.datos.btree.sharp.node.NodeReference, boolean, ar.com.datos.util.WrappedParam)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KeyNodeReference<E, K> addElement(E element, NodeReference<E, K> brother, boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		WrappedParam<Integer> rigthNodePositionParam = new WrappedParam<Integer>();
		
		// Delego el agregado del elemento en el nodo hijo que debe tenerlo.
		WrappedParam<K> rigthNodeKey = new WrappedParam<K>(fatherKey.getValue());
		KeyNodeReference<E, K> keyNodeReferenceOverflow = addElementInChild(element, rigthNodePositionParam, rigthNodeKey); 
		
		KeyNodeReference<E, K> returnValue = null;

		
		int rigthNodePosition = rigthNodePositionParam.getValue();
		KeyNodeReference<E, K> oldKeyNodeReference, newKeyNodeReference;

		// Si hubo split al agregar en el hijo...
		if (keyNodeReferenceOverflow != null) {
			// Si hubo Split

			// Entonces keyNodeReferenceOverflow contiene la clave de la primer clave del nuevo nodo
			// y una referencia al nuevo nodo.
			// Debo agregar esta clave y referencia y actualizar la clave de la referencia al nodo que
			// estaba a la derecha.
				
			// Actualizo la clave de la referencia al nodo que estaba a la derecha.
			oldKeyNodeReference = this.keysNodes.get(rigthNodePosition);
			newKeyNodeReference = new KeyNodeReference<E, K>(rigthNodeKey.getValue(), oldKeyNodeReference.getNodeReference());
			this.keysNodes.set(rigthNodePosition, newKeyNodeReference);
				
			// Y ahora agrego el nuevo nodo a mis referencias junto con su clave.
			this.keysNodes.add(rigthNodePosition, keyNodeReferenceOverflow);
			
			if (calculateNodeSize() > this.getNodeMaxCapacity()) {
				// Si mi nodo quedo con overflow
				returnValue = overflow((AbstractInternalNode)brother.getNode(), leftBrother, fatherKey);
			}
		} else {
			if (rigthNodeKey.getValue() != null) {
				// La key correspondiente a la referencia al nodo de la derecha, debe
				// cambiar por la que subieron, sin tocar ninguna NodeReference.
				oldKeyNodeReference = this.keysNodes.get(rigthNodePosition);
				newKeyNodeReference = new KeyNodeReference<E, K>(rigthNodeKey.getValue(), oldKeyNodeReference.getNodeReference());
				this.keysNodes.set(rigthNodePosition, newKeyNodeReference);
			}
		}
		
		if (rigthNodeKey.getValue() != null) {
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

	/**
	 * Obtiene la tercera parte del nodo de la izquierda o de la derecha
	 * según el valor de left sea true o no.
	 * La list de {@link KeyNodeReference} del nodo quedará sin esa tercera parte.
	 * Si left es true, la primer KeyNodeReference de la lista no tendrá Key
	 * (será null) y se corresponderá con firstChild; y la última KeyNodeReference
	 * no tendrá NodeReference (será null) y se corresponderá con la clave que
	 * debe apuntar a lo que resta del nodo.
	 * 
	 * Patrón de diseño Template.
	 */
	protected abstract List<KeyNodeReference<E, K>> getThirdPart(boolean left);
	
//	@Override
//	public String toString() {
//		String returnValue = "<<" + this.firstChild.toString() + "||";
//		Iterator<KeyNodeReference<E, K>> it = this.keysNodes.iterator();
//		while (it.hasNext()) {
//			returnValue += it.next().toString();
//			if (it.hasNext()) {
//				returnValue += "||";
//			}
//		}
//		returnValue += ">>";
//
//		return returnValue;
//	}

	// FIXME: Este toString() debe ser reemplazado por el de arriba. Solo está para desarrollo.
	@Override
	public String toString() {
		String returnValue;
		if (this.firstChild == null) {
			returnValue = "<<" + "((N:null))" + "||";
		} else {
			returnValue = "<<" + this.firstChild.toString() + "||";
		}
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
