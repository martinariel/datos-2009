package ar.com.datos.btree;

import java.io.Closeable;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;

/**
 * Árbol
 * 
 * @author fvalido
 */
public interface BTree<E extends Element<K>, K extends Key> extends Closeable {
	/**
	 * Permite agregar un elemento.
	 */
	public void addElement(E element) throws BTreeException;
	
	/**
	 * Obtiene el elemento correspondiente a la clave pasada si es que existe.
	 * De otro modo devuelve null.
	 */
	public E findElement(K key) throws BTreeException;

	/**
	 * Obtiene un {@link BTreeIterator} que estará posicionado de tal manera que si se llama 
	 * a #next() se devolverá el {@link Element} correspondiente a la {@link Key} pasada o
	 * el que "sería" el siguiente si es que no está esa {@link Key} en el árbol; y de tal
	 * manera que si se llama a #previous() se devolverá el {@link Element} correspondiente
	 * a la {@link Key} anterior a la {@link Key} pasada (esté o no esté ella en el árbol).
	 * Obviamente debe chequearse si hay o no un next o un previous desde la primer llamada. 
	 */
	public BTreeIterator<E> iterator(K key) throws BTreeException;

	/**
	 * Permite especificar que el árbol no será usado más.
	 * Deja la instancia no usable, pero si existia alguna forma de persistencia podrá ser recuperado.
	 */
	public void close() throws BTreeException;
}
