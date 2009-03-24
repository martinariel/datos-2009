package ar.com.datos.file;

import java.util.Collection;

public interface SequentialAccesor extends Iterable<Collection<Object>>{

	public Address addEntity(Collection<Object> campos);
	
}
