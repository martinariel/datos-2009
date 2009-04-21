package ar.com.datos.btree.sharp.node;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo raiz gen�rico. Debe ser extendido para que sea usable.
 * Tiene vida despu�s de que se produce el primer split. 
 *
 * @author fvalido
 */
public abstract class AbstractRootNode<E extends Element<K>, K extends Key> extends AbstractInternalNode<E, K> {
	/**
	 * Permite crear un nodo raiz.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 */
	public AbstractRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#overflow(ar.com.datos.btree.sharp.node.AbstractInternalNode, boolean, ar.com.datos.util.WrappedParam)
	 */
	protected KeyNodeReference<E, K> overflow(AbstractInternalNode<E, K> brother,
			 								boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Aca no hay hermano, ni clave del padre... Solo debo crear 3 hijos, poner
		// 1/3 del nodo (2/3 de un nodo normal) en cada uno de esos hijos y reescribir
		// esta raiz.
		
		// Trabajo a los nodos como left, center y rigth.
		AbstractInternalNode<E, K> left = this.bTreeSharpConfiguration.getBTreeSharpFactory().createInternalNode(this.bTreeSharpConfiguration);
		AbstractInternalNode<E, K> rigth = this.bTreeSharpConfiguration.getBTreeSharpFactory().createInternalNode(this.bTreeSharpConfiguration);
		AbstractInternalNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpFactory().createInternalNode(this.bTreeSharpConfiguration);
		
		// Extraigo los tercios 
		List<List<KeyNodeReference<E, K>>> listParts = getParts(); // M�todo template.
		List<KeyNodeReference<E, K>> leftParts = listParts.get(0);
		List<KeyNodeReference<E, K>> centerParts = listParts.get(1);
		List<KeyNodeReference<E, K>> rigthParts = listParts.get(2);
		
		// Los meto en los nodos. 
		K key1, key2;
		KeyNodeReference<E, K> tempKeyNodeReference = leftParts.remove(0);
		left.firstChild = tempKeyNodeReference.getNodeReference();
		left.keysNodes.addAll(leftParts);
		tempKeyNodeReference = centerParts.remove(0);
		center.firstChild = tempKeyNodeReference.getNodeReference();
		key1 = tempKeyNodeReference.getKey();
		center.keysNodes.addAll(centerParts);
		tempKeyNodeReference = rigthParts.remove(0);
		rigth.firstChild = tempKeyNodeReference.getNodeReference();
		key2 = tempKeyNodeReference.getKey();
		rigth.keysNodes.addAll(rigthParts);
		
		// M�todo template
		left.postAddElement();
		center.postAddElement();
		rigth.postAddElement();
		
		// Reconfiguro esta raiz para que apunte a los nuevos nodos.
		this.firstChild = left.myNodeReference;
		this.keysNodes.clear();
		this.keysNodes.add(new KeyNodeReference<E, K>(key1, center.myNodeReference));
		this.keysNodes.add(new KeyNodeReference<E, K>(key2, rigth.myNodeReference));
	
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getNodeMaxCapacity()
	 */
	@Override
	protected final short getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityRootNode();
	}

	/**
	 * Permite dividir el nodo en 3 partes.
	 * 
	 * La primer KeyNodeReference de la primer parte contendr� a firstChild como
	 * NodeReference (que ser� el firstChild del nodo creado a partir de �l) y 
	 * ninguna clave (las dem�s son normales).
	 * La primer KeyNodeReference de la segunda y tercer parte contendra el
	 * siguiente KeyNodeReference al del anterior, pero debe interpret�rselo
	 * de esta manera: el NodeReference ser� el firstChild del nuevo nodo (a
	 * crear usando esta parte) y la Key ser� la clave que apuntar� a este
	 * nuevo nodo (los dem�s KeyNodeReference son normales).
	 * 
	 * Es indistinto el estado en que se deja el nodo original (este).
	 * 
	 * Patr�n de dise�o Template.
	 */
	protected abstract List<List<KeyNodeReference<E, K>>> getParts(); 
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getThirdPart(boolean)
	 */
	@Override
	protected final List<KeyNodeReference<E, K>> getThirdPart(boolean left) {
		// No se usa.
		return null;
	}
}