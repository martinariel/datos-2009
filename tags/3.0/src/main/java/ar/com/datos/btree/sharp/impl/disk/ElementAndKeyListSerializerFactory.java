package ar.com.datos.btree.sharp.impl.disk;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;

/**
 * Permite especificar los serializadores a usar para {@link Element} y {@link Key}
 * para el árbol b# en disco.
 * Debe poseer un constructor sin parámetros !
 *
 * @author fvalido
 */
public interface ElementAndKeyListSerializerFactory<E extends Element<K>, K extends Key> {
	/**
	 * Permite crear una instancia del serializador correspondiente a la lista de {@link Key}
	 */
	public ListKeysSerializer<K> createListKeySerializer();
	/**
	 * Permite crear una instancia del serializador correspondiente a la lista de {@link Element}
	 */
	public ListElementsSerializer<E, K> createListElementSerializer();
}
