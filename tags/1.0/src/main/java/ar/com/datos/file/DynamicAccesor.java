package ar.com.datos.file;




public interface DynamicAccesor<T> extends SequentialAccesor<T> {

	public T get(Address<Long, Short> direccion);
}
