package ar.com.datos.file;

import ar.com.datos.serializer.Serializable;


public interface SequentialAccesor<T extends Serializable<T>> extends Iterable<T>{

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
