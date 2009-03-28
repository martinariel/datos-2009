package ar.com.datos.file;

import java.util.Collection;
import java.util.Queue;

public interface SequentialAccesor extends Iterable<Collection<Object>>{

	public Address<Long, Short> addEntity(Queue<Object> campos);
	
}
