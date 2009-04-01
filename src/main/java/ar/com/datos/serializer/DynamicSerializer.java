package ar.com.datos.serializer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;

/**
 * {@link Serializer} que permite serializar una cola de objetos.
 * Un {@link DynamicSerializer} esta compuesto por un serializador
 * que se encargara de realizar la serializacion del primer elemento
 * devuelto por la cola y un segundo serializador que se encargara
 * de la serializacion del siguiente elemento de la cola. Este
 * segundo serializador sera a su vez un DynamicSerializer con lo
 * cual el ciclo se continua hasta que no hay mas un siguiente
 * serializador.
 * 
 * @author fvalido
 * @deprecated Se reemplaza con la interface Serializable. En los proximos días la borro.
 */
public class DynamicSerializer implements Serializer<Queue<Object>>, QueueSerializer {
	/**
	 * Atributo para alojar el serializador a usar para para el siguiente
	 * elemento (luego del primero) de la cola.
	 */
	private DynamicSerializer nextSerializer;
	/**
	 * Atributo para alojar el serializador a usar para el primer elemento
	 * de la cola.
	 */
	private Serializer realSerializer;

	/**
	 * Construye un {@link DynamicSerializer} cuya serializacion se
	 * realizara mediante el realSerializer pasado.
	 */
	public DynamicSerializer(Serializer realSerializer) {
		this.realSerializer = realSerializer;
	}

	/**
	 * Permite establecer el siguiente {@link Serializer}. Puede pasarse un
	 * {@link DynamicSerializer} o no. Se devolvera ese mismo {@link Serializer}
	 * si se paso un {@link DynamicSerializer} o un {@link DynamicSerializer}
	 * construido con el nextSerializer (un wrapper) si originalmente no era un
	 * {@link DynamicSerializer}.
	 */
	public DynamicSerializer setNextSerializer(Serializer nextSerializer) {
		if (!getClass().isAssignableFrom(nextSerializer.getClass())) {
			this.nextSerializer = new DynamicSerializer(nextSerializer);
		} else {
			this.nextSerializer = (DynamicSerializer)nextSerializer;
		}
		
		return this.nextSerializer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void dehydrate(OutputBuffer output, Queue<Object> object) {
		Object value = object.poll();
		this.realSerializer.dehydrate(output, value);
		
		if (this.nextSerializer != null) {
			this.nextSerializer.dehydrate(output, object);
		}
	}

	/**
	 * Deja en queue los objetos hidratados en el orden que se va especificando
	 * en nextSerializer.
	 */
	private void hydrate(InputBuffer input, Queue<Object> queue) {
		queue.add(this.realSerializer.hydrate(input));
		if (this.nextSerializer != null) {
			this.nextSerializer.hydrate(input, queue);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public Queue<Object> hydrate(InputBuffer input) {
		Queue<Object> queue = new LinkedList<Object>();
		hydrate(input,queue);
		
		return queue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public long getDehydrateSize(Queue<Object> object) {
		long size = 0;
		Iterator<Object> itQueue = object.iterator();
		Object value;
		DynamicSerializer currentSerializer = this;
		while (itQueue.hasNext()) {
			value = itQueue.next();
			size += currentSerializer.realSerializer.getDehydrateSize(value);
			currentSerializer = currentSerializer.nextSerializer;
		}
		
		return size;
	}
}
