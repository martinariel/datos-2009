package ar.com.datos.btree.sharp.impl.memory;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.exception.BTreeException;
import ar.com.datos.btree.sharp.conf.AdministrativeBTreeSharp;
import ar.com.datos.btree.sharp.node.NodeReference;

/**
 * Implementación de {@link AdministrativeBTreeSharp} en Memoria (no hace nada).
 *  
 * @author fvalido
 */
public class AdministrativeBTreeSharpMemory<E extends Element<K>, K extends Key> implements AdministrativeBTreeSharp<E, K> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.AdministrativeBTreeSharp#closeTree()
	 */
	@Override
	public void closeTree() throws BTreeException {
		// No se hace nada.
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.conf.AdministrativeBTreeSharp#updateRoot(ar.com.datos.btree.sharp.node.NodeReference)
	 */
	@Override
	public void updateRoot(NodeReference<E, K> nodeReference) throws BTreeException {
		// No se hace nada.
	}
}
