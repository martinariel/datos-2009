package ar.com.datos.file;

public interface DynamicAccesor<T> extends SequentialAccesor<T> {

	public T get(Address<Long, Short> direccion);

	public Address<Long, Short> updateEntity(Address<Long, Short> direccion, T object);

}
