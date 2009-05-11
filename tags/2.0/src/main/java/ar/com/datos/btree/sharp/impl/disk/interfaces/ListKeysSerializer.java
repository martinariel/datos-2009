package ar.com.datos.btree.sharp.impl.disk.interfaces;

import java.util.List;

import ar.com.datos.btree.elements.Key;
import ar.com.datos.serializer.Serializer;

/**
 * Serializador de una lista de {@link Key}.
 * 
 * @author fvalido
 */
public interface ListKeysSerializer<K extends Key> extends Serializer<List<K>> {

}
