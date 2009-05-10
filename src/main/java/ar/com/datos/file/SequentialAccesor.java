package ar.com.datos.file;

import java.io.Closeable;

import ar.com.datos.file.address.Address;


public interface SequentialAccesor<A extends Address, T> extends Iterable<T>, Closeable{

	/**
	 * Agrega una entidad al manejador de persistencia
	 */
	public A addEntity(T campos);

	/**
	 * Indica si no hay entidades a las que se pueden acceder
	 */
	public Boolean isEmpty();
	
}
