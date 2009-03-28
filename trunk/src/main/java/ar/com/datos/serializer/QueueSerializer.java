package ar.com.datos.serializer;

import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;

public interface QueueSerializer {

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void dehydrate(OutputBuffer output, Queue<Object> object);

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public Queue<Object> hydrate(InputBuffer input);

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public long getDehydrateSize(Queue<Object> object);

}