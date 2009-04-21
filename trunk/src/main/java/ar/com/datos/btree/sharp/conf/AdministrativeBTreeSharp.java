package ar.com.datos.btree.sharp.conf;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.node.NodeReference;

public interface AdministrativeBTreeSharp<E extends Element<K>, K extends Key> {

	/**
	 * Especifica que el �rbol no ser� usado m�s por ahora.
	 */
	public void closeTree() throws BTreeException;
	
    /**
     * Permite actualizar la informaci�n administrativa sobre la posici�n del nodo root.
     *
     * @param nodeReference
     * {@link NodeReference} apuntando al nodo raiz.
     */
    public void updateRoot(NodeReference<E,K> nodeReference) throws BTreeException;
    
}
