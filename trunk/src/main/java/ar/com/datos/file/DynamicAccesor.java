package ar.com.datos.file;

import java.util.Collection;


public interface DynamicAccesor extends SequentialAccesor {

	public Collection<Object> get(Address direccion);
}
