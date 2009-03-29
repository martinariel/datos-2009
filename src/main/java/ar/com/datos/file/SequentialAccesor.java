package ar.com.datos.file;

import java.util.Queue;

public interface SequentialAccesor extends Iterable<Queue<Object>>{

	public Address<Long, Short> addEntity(Queue<Object> campos);
	
}
