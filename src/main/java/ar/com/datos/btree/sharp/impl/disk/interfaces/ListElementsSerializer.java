package ar.com.datos.btree.sharp.impl.disk.interfaces;

import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.serializer.Serializer;

/**
 * Serializador de una Lista de {@link Element}.
 * 
 * @author fvalido
 */
public interface ListElementsSerializer<E extends Element<K>, K extends Key> extends Serializer<List<E>> {

}
