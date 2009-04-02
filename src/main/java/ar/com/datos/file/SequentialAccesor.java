package ar.com.datos.file;

import ar.com.datos.serializer.Serializable;


public interface SequentialAccesor<T extends Serializable<T>> extends Iterable<T>{

	public Address<Long, Short> addEntity(T campos);
	
}
