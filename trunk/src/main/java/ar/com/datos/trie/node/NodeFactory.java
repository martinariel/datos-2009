/**
 * 
 */
package ar.com.datos.trie.node;

import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.trie.Element;
import ar.com.datos.trie.Key;
import ar.com.datos.trie.KeyAtom;

/**
 * @author marcos
 *
 */
public class NodeFactory<E extends Element<K, A>, K extends Key<A>,A extends KeyAtom>{

	private int nLevels;
	private VariableLengthFileManager<Node<E,K,A>> leafNodeVLFM;
	private VariableLengthFileManager<Node<E,K,A>> internalNodeVLFM;
	
	public NodeFactory(int nLevels,
			VariableLengthFileManager<Node<E,K,A>> leafNodeVLFM,
			VariableLengthFileManager<Node<E,K,A>> internalNodeVLFM){
		this.nLevels = nLevels;
		this.leafNodeVLFM = leafNodeVLFM;
		this.internalNodeVLFM = internalNodeVLFM;
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
	public Node<E,K,A> createNode(int nodeLevel, A keyAtom){
		if (nodeLevel < this.nLevels-1){
			return this.createInternalNode(nodeLevel, keyAtom);
		} else if (nodeLevel == this.nLevels-1){
			return this.createSpecialInternalNode(nodeLevel, keyAtom);
		} else if(nodeLevel == this.nLevels){
			return this.createLeafNode(nodeLevel, keyAtom);
		} else { 
			// nodeLevel > this.nLevels (numero de nivel mayor que niveles posibles)
			//TODO: Tirar excepcion!
			return null;
		}
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
		if (nodeLevel < this.nLevels-1){
			return this.createNodeReferenceWithVLFM(this.internalNodeVLFM, keyAtom);
		} else if (nodeLevel == this.nLevels-1){
			return this.createNodeReferenceWithVLFM(this.leafNodeVLFM, keyAtom);
		} else { 
			// nodeLevel >= this.nLevels (numero de nivel mayor o igual que niveles posibles)
			//TODO: Tirar excepcion!
			return null;
		}
	}
	
	private NodeReference<E,K,A> createNodeReferenceWithVLFM(
			VariableLengthFileManager<Node<E,K,A>> vlfm, A keyAtom){
		return new NodeReference<E,K,A>(vlfm, keyAtom);
	}
	
	private InternalNode<E,K,A> createInternalNode(int level, A keyAtom){
		return new InternalNode<E, K, A>(level, keyAtom, this);
	}
	
	private SpecialInternalNode<E,K,A> createSpecialInternalNode(int level, A keyAtom){
		return new SpecialInternalNode<E, K, A>(level, keyAtom, this);
	}
	
	private LeafNode<E,K,A> createLeafNode(int level, A keyAtom){
		return new LeafNode<E, K, A>(level, keyAtom);
	}
	
	

}
