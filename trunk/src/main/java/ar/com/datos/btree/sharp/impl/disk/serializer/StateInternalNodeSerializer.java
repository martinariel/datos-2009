package ar.com.datos.btree.sharp.impl.disk.serializer;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.btree.sharp.impl.disk.node.NodeType;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.ByteSerializer;
import ar.com.datos.serializer.common.SerializerCache;

/**
 * Serializador que permite serializar diferentes tipos de nodos Internos, es decir nodos que
 * se ubican dentro del archivo de nodos internos (InternalNodeDisk, EspecialRootNodeDisk y
 * RootNodeDisk).
 * Simplemente se establece el Serializer<Node<E, K>> a usar y se delegará la acción a ese 
 * serializer.
 * 
 * @author fvalido
 */
public class StateInternalNodeSerializer<E extends Element<K>, K extends Key> implements Serializer<Node<E, K>> {
	/** Serializador para el tipo de nodo */
	private ByteSerializer byteSerializer;
	/** Serializador para nodos internos */
	private InternalNodeSerializer<E, K> internalNodeSerializer;
	/** Serializador para nodos raiz */
	private RootNodeSerializer<E, K> rootNodeSerializer;
	/** Serializador para nodos raiz especial */
	private EspecialRootNodeSerializer<E, K> especialRootNodeSerializer;	

	/**
	 * Constructor
	 * 
	 * @param internalNodeSerializer
	 * Serializador a aplicar sobre nodos internos normales.
	 * @param rootNodeSerializer
	 * Serializador a aplicar sobre nodos raiz normales.
	 * @param especialRootNodeSerializer
	 * Serializador a aplicar sobre nodos raiz especiales.
	 * 
	 * NOTA: Para terminar la construcción debe llamarse al método {@link #setBTree(BTreeSharp)}
	 */
	public StateInternalNodeSerializer(InternalNodeSerializer<E, K> internalNodeSerializer,
										RootNodeSerializer<E, K> rootNodeSerializer,
										EspecialRootNodeSerializer<E, K> especialRootNodeSerializer) {
		this.internalNodeSerializer = internalNodeSerializer;
		this.rootNodeSerializer = rootNodeSerializer;
		this.especialRootNodeSerializer= especialRootNodeSerializer;
		this.byteSerializer = SerializerCache.getInstance().getSerializer(ByteSerializer.class);
	}
	
	/**
	 * Permite establecer el BTree que usará este Serializer.
	 */
	public void setBTree(BTreeSharp<E, K> btree) {
		this.especialRootNodeSerializer.setBTree(btree);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, Node<E, K> object) {
		// Primero pongo el tipo de nodo.
		this.byteSerializer.dehydrate(output, object.getNodeType().getType());
		// Y ahora serializo el nodo usando el serializador correspondiente al tipo.
		Serializer<Node<E, K>> currentSerializer = getSerializerFor(object.getNodeType());
		currentSerializer.dehydrate(output, object);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public Node<E, K> hydrate(InputBuffer input) {
		// Primero veo que tipo de nodo es.
		NodeType nodeType = NodeType.getNodeType(this.byteSerializer.hydrate(input));
		// Y ahora hidrato el nodo usando el serializador correspondiente al tipo.
		Serializer<Node<E, K>> currentSerializer = getSerializerFor(nodeType);
		return currentSerializer.hydrate(input);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(Node<E, K> object) {
		// Obtengo el tamaño usando el serializador correspondiente al tipo.
		Serializer<Node<E, K>> currentSerializer = getSerializerFor(object.getNodeType());
		return this.byteSerializer.getDehydrateSize(null) + currentSerializer.getDehydrateSize(object);
	}
	
	/**
	 * Permite establecer el serializador que debe usarse.
	 */
	@SuppressWarnings("unchecked")
	private Serializer<Node<E, K>> getSerializerFor(NodeType nodeType) {
		Serializer returnValue = null;
		switch (nodeType) {
			case INTERNAL: returnValue = this.internalNodeSerializer; break;
			case ROOT: returnValue = this.rootNodeSerializer; break;
			case ESPECIALROOT: returnValue = this.especialRootNodeSerializer; break;
		}
		
		return returnValue;
	}
}
