package ar.com.datos.file;

import ar.com.datos.serializer.Serializable;



public interface DynamicAccesor<T extends Serializable<T>> extends SequentialAccesor<T> {

	public T get(Address<Long, Short> direccion);
}
