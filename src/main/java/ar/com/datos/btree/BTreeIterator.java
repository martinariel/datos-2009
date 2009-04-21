package ar.com.datos.btree;

import java.util.NoSuchElementException;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.exception.BTreeException;

/**
 * Nota: Todos los métodos pueden arrojar {@link BTreeException}
 * 
 * @author fvalido
 * @see BTree#iterator(ar.com.datos.btree.elements.Key)
 */
public interface BTreeIterator<E extends Element> {
	// Copiado de Iterator y ListIterator.
	
    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    boolean hasNext() throws BTreeException;

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    E next() throws BTreeException;
	
	/**
     * Returns <tt>true</tt> if this list iterator has more elements when
     * traversing the list in the reverse direction.  (In other words, returns
     * <tt>true</tt> if <tt>previous</tt> would return an element rather than
     * throwing an exception.)
     *
     * @return <tt>true</tt> if the list iterator has more elements when
     *	       traversing the list in the reverse direction.
     */
    boolean hasPrevious() throws BTreeException;

    /**
     * Returns the previous element in the list.  This method may be called
     * repeatedly to iterate through the list backwards, or intermixed with
     * calls to <tt>next</tt> to go back and forth.  (Note that alternating
     * calls to <tt>next</tt> and <tt>previous</tt> will return the same
     * element repeatedly.)
     *
     * @return the previous element in the list.
     *
     * @exception NoSuchElementException if the iteration has no previous
     *            element.
     */
    E previous() throws BTreeException;
}
