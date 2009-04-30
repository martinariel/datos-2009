package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo raiz genérico. Debe ser extendido para que sea usable.
 * Tiene vida después de que se produce el primer split. 
 *
 * @author fvalido
 */
public abstract class AbstractRootNode<E extends Element<K>, K extends Key> extends AbstractInternalNode<E, K> {
	/**
	 * Permite crear un nodo raiz.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public AbstractRootNode(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.Node#getNodeType()
	 */
	@Override
	public NodeType getNodeType() {
		return NodeType.ROOT;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#overflow(ar.com.datos.btree.sharp.node.AbstractInternalNode, boolean, ar.com.datos.util.WrappedParam)
	 */
	@Override
	protected KeyNodeReference<E, K> overflow(AbstractInternalNode<E, K> brother,
			 								boolean leftBrother, WrappedParam<K> fatherKey) throws BTreeException {
		// Aca no hay hermano, ni clave del padre... Solo debo crear 3 hijos, poner
		// 1/3 del nodo (2/3 de un nodo normal) en cada uno de esos hijos y reescribir
		// esta raiz.
		
		// Trabajo a los nodos como left, center y right.
		AbstractInternalNode<E, K> left = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createInternalNode(this.bTreeSharpConfiguration);
		AbstractInternalNode<E, K> right = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createInternalNode(this.bTreeSharpConfiguration);
		AbstractInternalNode<E, K> center = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createInternalNode(this.bTreeSharpConfiguration);
		
		// Extraigo los tercios
		WrappedParam<K> overflowKey1 = new WrappedParam<K>();
		WrappedParam<K> overflowKey2 = new WrappedParam<K>();
		getParts(null, null, null, left, center, right, overflowKey1, overflowKey2); // Método template.
		
		// Método template
		left.postAddElement();
		center.postAddElement();
		right.postAddElement();
		
		// Reconfiguro esta raiz para que apunte a los nuevos nodos.
		this.firstChild = left.myNodeReference;
		this.keysNodes.clear();
		this.keysNodes.add(new KeyNodeReference<E, K>(overflowKey1.getValue(), center.myNodeReference));
		this.keysNodes.add(new KeyNodeReference<E, K>(overflowKey2.getValue(), right.myNodeReference));
	
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getNodeMaxCapacity()
	 */
	@Override
	protected final int getNodeMaxCapacity() {
		return this.bTreeSharpConfiguration.getMaxCapacityRootNode();
	}
}
