package ar.com.datos.file;

import java.io.Closeable;


public interface SequentialAccesor<T> extends Iterable<T>, Closeable{

	/**
	 * Agrega una entidad al manejador de persistencia
	 * @param campos
	 * @return
	 */
	public Address<Long, Short> addEntity(T campos);

	/**
	 * Indica si no hay entidades a las que se pueden acceder
	 * @return
	 */
	public Boolean isEmpty();
	
}
