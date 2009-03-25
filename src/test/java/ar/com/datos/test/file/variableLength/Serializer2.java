package ar.com.datos.test.file.variableLength;

import java.util.Collection;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
/**
 * Interfacez creada para hacer algunas pruebas
 * debería ser reemplazada por la de Federico
 * @author Juan Manuel Barreneche
 *
 */
public interface Serializer2 {
	public void dehydrate(Collection<Object> co, OutputBuffer ob);
	public Collection<Object> hydrate(InputBuffer ib);
}
