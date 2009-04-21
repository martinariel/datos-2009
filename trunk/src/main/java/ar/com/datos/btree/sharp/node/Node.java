package ar.com.datos.btree.sharp.node;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.BTreeSharpConfiguration;
import ar.com.datos.util.WrappedParam;

/**
 * Nodo Abstracto (Gen�rico usado para Internos y Hojas)
 *
 * @author fvalido
 */
public abstract class Node<E extends Element<K>, K extends Key> {
	/** Configuraciones que debe utilizar el nodo. */
	protected BTreeSharpConfiguration<E, K> bTreeSharpConfiguration;

	/** NodeReference que apunta a mi. */
	protected NodeReference<E, K> myNodeReference;

	/**
	 * Permite construir una instancia.
	 *
	 * @param bTreeSharpConfiguration
	 * Configuraciones del �rbol que incluir�n la configuraci�n del nodo.
	 */
	public Node(BTreeSharpConfiguration<E, K> bTreeSharpConfiguration) {
		this.bTreeSharpConfiguration = bTreeSharpConfiguration;
	}
	
	/**
	 * Permite agregar un {@link Element} al nodo.
	 * 
	 * @param brother
	 * En caso de no entrar el {@link Element} en el nodo utilizar� al hermano pasado para
	 * agregar el {@link Element} que sobre en este nodo, o para hacer el split correspondiente
	 * si no entra en ninguno de los dos.
	 * 
	 * @param leftBrother
	 * Indica si el hermano pasado es el izquierdo o el derecho.
	 *
	 * @param fatherKey
	 * Clave dentro del nodo padre por la cual se est� entrando en este nodo para agregar a element.
	 * Debe ser actualizada de esta manera:
	 * - Si no hay overflow debe ponerse su valor en null.
	 * - Si hay overflow (haya habido split o no) debe ponerse el valor de la Key que apunte al
	 *   nodo de la derecha (el que sea el de la derecha... el hermano o este).
	 *
	 * @return
	 * En caso de haber split la {@link Key} correspondera a la clave a subir (la primera
	 * del nodo del centro, es decir el generado) y el {@link NodeReference} corresponder�
	 * al nodo generado. La nueva primer clave del nodo de la derecha (que tambi�n cambiar�)
	 * debe ser obtenida de �l mediante {@link #getFirstKey()}.
	 * Si no se produjo split al agregar el {@link Element}, se devolver� null.
	 *
	 * @throws BTreePlusException
	 * Si hay alg�n problema agregando el {@link Element} pasado.
	 */
	public abstract KeyNodeReference<E, K> addElement(E element, 
							NodeReference<E, K> brother, boolean leftBrother,
							WrappedParam<K> fatherKey) throws BTreeException;
	
	/**
	 * Obtiene el {@link Element} correspondiente a la {@link Key} pasada si es que existe.
	 * De otro modo devuelve null.
	 *
	 * @throws BTreeException
	 * Si hay alg�n problema encontrando la {@link Key}.
	 */
	public abstract E findElement(K key) throws BTreeException;

	/**
	 * Obtiene el {@link ChainedNode} en la que deber�a estar guardada la key pasada
	 * si es que existe.
	 *
	 * @throws BTreeException
	 * Si hay alg�n problema encontrando el {@link ChainedNode}.
	 */
	public abstract ChainedNode<E, K> findNode(K key) throws BTreeException;

	/**
	 * M�todo llamado luego de agregar un {@link Element} al nodo.
	 * Generalmente ser� usado para para que el nodo se guarde en disco, en implementaciones
	 * que lo requieran.
	 * Adem�s deber�a actualizar myNodeReference en implementaciones que lo requieran.
	 *
	 * (Patr�n de dise�o Template)
	 */
	protected abstract void postAddElement() throws BTreeException;
	
	/**
	 * Calcula el tama�o del nodo.
	 */
	protected abstract int calculateNodeSize();
}
