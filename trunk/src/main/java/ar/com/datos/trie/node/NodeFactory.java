/**
 * 
 */
package ar.com.datos.trie.node;

import java.util.List;

import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class NodeFactory<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{

	private int nLevels;
	private int maxLeafPartitionItems;
	
	private BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> leafPartitionNodeVLFM;
	private BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> internalNodeVLFM;
	
	@SuppressWarnings("unchecked")
	public NodeFactory(int nLevels, int maxLeafPartitionItems,
			BlockAccessor<BlockAddress<Long,Short>, ? extends Node<E,K,A>> leafNodeVLFM,
			BlockAccessor<BlockAddress<Long,Short>, ? extends Node<E,K,A>> internalNodeVLFM){
		this.nLevels = nLevels;
		this.maxLeafPartitionItems = maxLeafPartitionItems;
		this.leafPartitionNodeVLFM = (BlockAccessor<BlockAddress<Long, Short>, Node<E, K, A>>) leafNodeVLFM;
		this.internalNodeVLFM = (BlockAccessor<BlockAddress<Long, Short>, Node<E, K, A>>) internalNodeVLFM;
	}
	
	/**
	 * Crea un nodo a partir de un nivel y una porcion de clave.
	 * 
	 * Esto se debe a que hay distintos tipos de nodo en en funcion del nivel
	 * en el que se vaya a crear un nodo.
	 * 
	 * Esto libera a los nodos de la logica de que tipo de nodo crear.
	 * 
	 * @param nodeLevel el nivel donde se creara el nodo
	 * @param keyAtom la porcion de clave que contendra el nodo a crear
	 * @return el nodo ya creado (puede ser un nodo hoja o un nodo interno)
	 */
	public Node<E,K,A> createNode(int nodeLevel){
		if (nodeLevel < this.nLevels){
			return this.createInternalNode(nodeLevel);
		} else if(nodeLevel == this.nLevels){
			return this.createLeafNode(nodeLevel);
		} else { 
			// nodeLevel > this.nLevels (numero de nivel mayor que niveles posibles)
			//TODO: Tirar excepcion!
			return null;
		}
	}
	
	/**
	 * Crea un nodo a partir de un nivel y del valor para el elemento y del valor para la lista de nodeReferences
	 * 
	 * Esto se debe a que hay distintos tipos de nodo en en funcion del nivel
	 * en el que se vaya a crear un nodo.
	 * 
	 * Esto libera a los nodos de la logica de que tipo de nodo crear.
	 * 
	 * @return el nodo ya creado (puede ser un nodo hoja o un nodo interno)
	 */
	public Node<E,K,A> createNode(int nodeLevel, E element, List<NodeReference<E, K, A>> nodeReferences){
		InternalNode<E, K, A> node = this.createInternalNode(nodeLevel);
		if(node != null){
			node.childNodesReferences = nodeReferences;
			node.element = element;
		}
		return node; 
	}
	
	
	/**
	 * Crea una referencia a un nodo a partir de un nivel y una porcion de clave.
	 *  
	 * Se debe a que hay distintos tipos de referencia, ya que utilizamos dos
	 * archivos (uno para los nodos internos y otro para los nodos hojas).
	 * 
	 * Si se esta en el nivel anteultimo, se hace referencia al archivo de hojas
	 * y si esta en algun nivel anterior se hace referencia al archivo de nodos
	 * internos.
	 * 
	 * Esto libera a los nodos de la logica de que tipo de referencia a nodo crear.
	 * 
	 * @param nodeLevel el nivel donde se creara el nodo
	 * @param keyAtom la porcion de clave que contendra el nodo a crear
	 * @return el nodo ya creado (puede ser un nodo hoja o un nodo interno)
	 */
	public NodeReference<E,K,A> createNodeReference(int nodeLevel, A keyAtom){
		if (nodeLevel < this.nLevels){
			return this.createNodeReferenceWithVLFM(this.internalNodeVLFM, keyAtom);
		} else if (nodeLevel == this.nLevels){
			return this.createNodeReferenceWithVLFM(this.leafPartitionNodeVLFM, keyAtom);
		} else { 
			// nodeLevel >= this.nLevels (numero de nivel mayor o igual que niveles posibles)
			//TODO: Tirar excepcion!
			return null;
		}
	}
	
	private NodeReference<E,K,A> createNodeReferenceWithVLFM(
			BlockAccessor<BlockAddress<Long,Short>, Node<E,K,A>> vlfm, A keyAtom){
		return new NodeReference<E,K,A>(vlfm, keyAtom);
	}
	
	private InternalNode<E,K,A> createInternalNode(int level){
		return new InternalNode<E, K, A>(level, this);
	}
	
	private LeafPartitionNode<E,K,A> createLeafNode(int level){
		return new LeafPartitionNode<E, K, A>(level, this.maxLeafPartitionItems);
	}
	
	

}
