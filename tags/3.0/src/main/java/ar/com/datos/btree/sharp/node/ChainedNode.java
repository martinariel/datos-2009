package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;

/**
 * Nodo que permite recorrer sus elementos y pasar al siguiente o anterior nodo.
 *
 * @author fvalido
 */
public interface ChainedNode<E extends Element<K>, K extends Key> {
	/**
	 * Obtiene el {@link Element} correspondiente a la clave pasada si es que existe.
	 * De otro modo devuelve null.
	 *
	 * @throws BTreeException
	 * Si hay algún problema encontrando el {@link Element}.
	 */
	public E findElement(K key) throws BTreeException;

	/**
	 * Permite obtener el siguiente elemento DEL NODO a la clave pasada, exista la clave o no
	 * en el nodo. Si la clave fuera mayor o igual que el último elemento se devolverá null.
	 */
	public E findNextElement(K key);

	/**
	 * Permite obtener el anterior elemento DEL NODO a la clave pasada, exista la clave o no
	 * en el nodo. Si la clave fuera menor o igual que el primer elemento se devolverá null.
	 */
	public E findPreviousElement(K key);

	/**
	 * Permite obtener el siguiente nodo. Devuelve null si no hay siguiente nodo.
	 *
	 * @throws BTreeException
	 * Si hay algún problema levantando el nodo siguiente.
	 */
	public ChainedNode<E, K> getNextNode() throws BTreeException;

	/**
	 * Permite obtener el nodo anterior. Devuelve null si no hay anterior nodo.
	 *
	 * @throws BTreeException
	 * Si hay algún problema levantando el nodo anterior.
	 */
	public ChainedNode<E, K> getPreviousNode() throws BTreeException;
}