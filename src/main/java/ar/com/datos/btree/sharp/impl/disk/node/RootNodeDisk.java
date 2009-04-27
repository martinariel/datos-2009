package ar.com.datos.btree.sharp.impl.disk.node;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.impl.disk.BTreeSharpConfigurationDisk;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.btree.sharp.impl.disk.serializer.RootNodeSerializer;
import ar.com.datos.btree.sharp.node.AbstractRootNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.btree.sharp.util.ThirdPartHelper;

/**
 * Nodo raiz en disco.
 *
 * @author fvalido
 */
public final class RootNodeDisk<E extends Element<K>, K extends Key> extends AbstractRootNode<E, K> implements DiskNode<E, K> {
	/** 
	 * Configuraciones del árbol (que incluirán la configuración del nodo). Se 
	 * pisa el atributo bTreeSharpConfiguration heredado.
	 */
	protected BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration;
	
	/**
	 * Permite construir un nodo raiz en disco.
	 * 
	 * Nota: El nodo no se grabará en disco inmediatamente cuando se crea. El
	 *       grabado se hará cuando se llame por primera vez a postAddElement.
	 *       Este constructor se usa cuando el nodo no existe previamente (es
	 *       decir no existe en disco)
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del árbol que incluirán la configuración del nodo.
	 */
	public RootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration) {
		super(bTreeSharpConfiguration);
		// Se pisa el atributo bTreeSharpConfiguration heredado.
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}

	/**
	 * Permite construir un nodo raiz en disco.
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
	public RootNodeDisk(BTreeSharpConfigurationDisk<E, K> bTreeSharpConfiguration,
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
			this.myNodeReference = new NodeReferenceDisk<E, K>(this.bTreeSharpConfiguration.getInternalNodesFileManager(), NodeType.ROOT);
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
//	/*
//	 * (non-Javadoc)
//	 * @see ar.com.datos.btree.sharp.node.AbstractRootNode#getParts()
//	 */
//	@Override
//	protected List<List<KeyNodeReference<E, K>>> getParts() {
//		// TODO
//		return null;
//	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.node.AbstractInternalNode#getParts(ar.com.datos.btree.sharp.node.NodeReference, java.util.List, ar.com.datos.btree.elements.Key)
	 */
	@Override
	protected List<List<KeyNodeReference<E, K>>> getParts(NodeReference<E, K> firstChildRightNode, List<KeyNodeReference<E, K>> keysNodesRightNode, K fatherKeyRigthNode) {
		ListKeysSerializer<K> serializer = this.bTreeSharpConfiguration.getListKeysSerializer(); 
		
		// Creo una lista que incluya a firstChild.
		List<KeyNodeReference<E, K>> sourceKeyNodeReference = new LinkedList<KeyNodeReference<E,K>>();
		sourceKeyNodeReference.add(new KeyNodeReference<E, K>(null, this.firstChild));
		sourceKeyNodeReference.addAll(this.keysNodes);
		
		// Extraigo una lista de Keys.
		Iterator<KeyNodeReference<E, K>> itKeyNodeReference = sourceKeyNodeReference.iterator();
		List<K> sourceKeys = new LinkedList<K>();
		itKeyNodeReference.next();
		while (itKeyNodeReference.hasNext()) {
			sourceKeys.add(itKeyNodeReference.next().getKey());
		}

		// Divido la lista de Keys.
		List<List<K>> keyParts = ThirdPartHelper.divideInThreePartsEspecial(sourceKeys);

		List<K> left = keyParts.get(0);
		List<K> center = keyParts.get(1);
		List<K> right = keyParts.get(2);

		// Reacomodo lo obtenido pero ahora calculando los tamaños (sin considerar tamaño fijo).
		ThirdPartHelper.balanceThirdPart(left, center, serializer, serializer.getDehydrateSize(right));
		ThirdPartHelper.balanceThirdPart(center, right, serializer, serializer.getDehydrateSize(left));
		
		// Recombino las listas divididas de keys con las KeyNodeReferences
		return ThirdPartHelper.combineKeysAndNodeReferences(sourceKeyNodeReference, keyParts);
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
	 * Elemento visible para su uso desde {@link RootNodeSerializer}
	 */
	public NodeReferenceDisk<E, K> getFirstNodeReference() {
		return (NodeReferenceDisk<E, K>)this.firstChild;
	}

	/**
	 * Elemento visible para su uso desde {@link RootNodeSerializer}
	 */
	public List<KeyNodeReference<E, K>> getKeysNodeReferences() {
		return this.keysNodes;
	}
}
