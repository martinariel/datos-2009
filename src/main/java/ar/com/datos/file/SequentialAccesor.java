package ar.com.datos.file;


public interface SequentialAccesor<T> extends Iterable<T>{

	public Address<Long, Short> addEntity(T campos);
	
}
