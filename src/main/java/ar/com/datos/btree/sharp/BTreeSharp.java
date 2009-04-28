package ar.com.datos.btree.sharp;

import java.util.NoSuchElementException;

import ar.com.datos.btree.BTree;
import ar.com.datos.btree.BTreeIterator;
import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.btree.sharp.impl.memory.node.NodeReferenceMemory;
import ar.com.datos.btree.sharp.node.ChainedNode;
import ar.com.datos.btree.sharp.node.Node;
import ar.com.datos.util.WrappedParam;

/**
 * �rbol B#
 *
 * @author fvalido
 */
public class BTreeSharp<E extends Element<K>, K extends Key> implements BTree<E, K> {
	/** Configuraciones del �rbol. */
	private BTreeSharpConfiguration<E, K> bTreeSharpConfiguration;
	
	/** Nodo principal del �rbol. */
	private Node<E, K> rootNode;
	
	/** Permite saber si la instancia es utilizable. */	
	private boolean destroyed;
	
	/**
	 * Permite crear un {@link BTreeSharp}. El �rbol creado ser� totalmente nuevo.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol.
	 */
	public BTreeSharp(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		this.destroyed = false;
		this.rootNode = null;
	}

	/**
	 * Permite crear un {@link BTreeSharp}.
	 * 
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol.
	 * 
	 * @param rootNode
	 * Nodo raiz.
	 */
	public BTreeSharp(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration, Node<E, K> rootNode) {
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
		this.destroyed = false;
		this.rootNode = rootNode;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.BTree#addElement(ar.com.datos.btree.elements.Element)
	 */
	@Override
	public void addElement(E element) throws BTreeException {
		if (this.destroyed) {
			throw new BTreeException();
		}
		
		try {
			if (this.rootNode == null) {
				this.rootNode = this.bTreeSharpConfiguration.getBTreeSharpNodeFactory().createEspecialRootNode(this.bTreeSharpConfiguration, this);
			} 
			this.rootNode.addElement(element, new NodeReferenceMemory<E, K>(null), false, new WrappedParam<K>());
		} catch (BTreeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.BTree#findElement(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public E findElement(K key) throws BTreeException {
		if (this.destroyed) {
			throw new BTreeException();
		}
		
		E returnValue = null;

		if (this.rootNode != null) {
			returnValue = this.rootNode.findElement(key);
		}

		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.BTree#iterator(ar.com.datos.btree.elements.Key)
	 */
	@Override
	public BTreeIterator<E> iterator(K key) throws BTreeException {
		if (this.destroyed) {
			throw new BTreeException("El �rbol ya fue cerrado.");
		}
		
		ChainedNode<E, K> node = null;
		if (this.rootNode != null) {
			node = this.rootNode.findNode(key);
		}

		return new BTreeSharpIterator(node, key);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.BTree#destroy()
	 */
	@Override
	public void destroy() {
		if (!this.destroyed) {
			try {
				this.bTreeSharpConfiguration.closeTree();
				this.destroyed = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		destroy();
	}
	
	/**
	 * Permite cambiar la raiz por otra.
	 * Solo debe ser usado por la implementaci�n, no por el usuario.
	 */
	public void setRootNode(Node<E, K> rootNode) {
		this.rootNode = rootNode;
	}
	
	/**
	 * {@link BTreeIterator} del {@link BTreeSharp}
	 * 
	 * @author fvalido
	 */
	private class BTreeSharpIterator implements BTreeIterator<E> {
		/** {@link Key} de referencia para saber cual es el pr�ximo o anterior elemento. */
		private K currentKey;
		/** Nodo actual en el que intentar buscar el pr�ximo o el anterior. */
		private ChainedNode<E, K> currentNode;
		/**
		 * Indica si es la primera vez que se llama a next o a previous.
		 * De ser as�, {@link #next()} debe devolver el Element correspondiente a
		 * currentKey si es que existe y no el siguiente.
		 */
		private boolean isFirstCall;

		public BTreeSharpIterator(ChainedNode<E, K> currentNode, K currentKey) {
			this.currentNode = currentNode;
			this.currentKey = currentKey;
			this.isFirstCall = true;
		}

		/**
		 * Busca el siguiente {@link Element} (teniendo en cuenta la CurrentKey) en el
		 * currentNode recibido. Si no lo encuentra, lo busca en el siguiente nodo
		 * (y as�... [pero en la pr�ctica la busqueda en el siguiente se dar� una sola vez]).
		 * 
		 * @param isRealNext
		 * Si es false el this.currentNode no se ver� modificado nunca, pero si es true,
		 * y se encuentra el next en un nodo subsiguiente, se reemplazar� el currentNode
		 * por el nodo en el que se encontr�.
		 * Lo mismo corre para this.currentKey.
		 */
		private E next(ChainedNode<E, K> currentNode, boolean isRealNext) throws BTreeException {
			// Nota todo este m�todo es igual que previous, deber�a usar un Strategy ac�.
			
			if (currentNode == null) {
				if (isRealNext) {
					throw new NoSuchElementException();
				}
				return null;
			}
			E element = currentNode.findNextElement(this.currentKey);
			
			if (element == null) {
				currentNode = this.currentNode.getNextNode();
				element = next(currentNode, isRealNext);
			}
			
			if (isRealNext) {
				this.currentNode = currentNode;
				this.currentKey = element.getKey();
			}
				
			return element;
		}
		
		/**
		 * Busca el anterior {@link Element} (teniendo en cuenta la CurrentKey) en el
		 * currentNode recibido. Si no lo encuentra, lo busca en el anterior nodo
		 * (y as�... [pero en la pr�ctica la busqueda en el anterior se dar� una sola vez]).
		 * 
		 * @param isRealPrevious
		 * Si es false el this.currentNode no se ver� modificado nunca, pero si es true,
		 * y se encuentra el next en un nodo anterior, se reemplazar� el currentNode
		 * por el nodo en el que se encontr�.
		 * Lo mismo corre para this.currentKey.
		 */
		private E previous(ChainedNode<E, K> currentNode, boolean isRealPrevious) throws BTreeException {
			// Nota todo este m�todo es igual que next, deber�a usar un Strategy ac�.
			
			if (currentNode == null) {
				if (isRealPrevious) {
					throw new NoSuchElementException();
				}
				return null;
			}
			E element = currentNode.findPreviousElement(this.currentKey);
			
			if (element == null) {
				currentNode = this.currentNode.getPreviousNode();
				element = previous(currentNode, isRealPrevious);
			}
			
			if (isRealPrevious) {
				this.currentNode = currentNode;
				this.currentKey = element.getKey();
			}
				
			return element;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() throws BTreeException {
			boolean returnValue;
			
			if (this.isFirstCall) {
				returnValue = this.currentNode.findElement(this.currentKey) != null || 
							  next(this.currentNode, false) != null; 
			} else {
				returnValue = next(this.currentNode, false) != null;
			}
			
			return returnValue;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public E next() throws BTreeException {
			E returnValue;
			
			if (this.isFirstCall) {
				returnValue = this.currentNode.findElement(this.currentKey);
				if (returnValue == null) {
					returnValue = next(this.currentNode, true);
				}
				this.isFirstCall = false;
			} else {
				returnValue = next(this.currentNode, true);
			}

			
			return returnValue;
		}		
		
		/*
		 * (non-Javadoc)
		 * @see ar.com.datos.btree.BTreeIterator#hasPrevious()
		 */
		@Override
		public boolean hasPrevious() throws BTreeException {
			return previous(this.currentNode, false) != null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see ar.com.datos.btree.BTreeIterator#previous()
		 */
		@Override
		public E previous() throws BTreeException {
			this.isFirstCall = false;
			
			return previous(this.currentNode, true);
		}
	}

}
