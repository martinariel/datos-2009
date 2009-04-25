package ar.com.datos.file;

import java.io.Closeable;

import ar.com.datos.file.address.Address;


public interface SequentialAccesor<A extends Address, T> extends Iterable<T>, Closeable{

	/**
	 * Agrega una entidad al manejador de persistencia
	 * @param campos
	 * @return
	 */
	public A addEntity(T campos);

	/**
	 * Indica si no hay entidades a las que se pueden acceder
	 * @return
	 */
	public Boolean isEmpty();
	
}
