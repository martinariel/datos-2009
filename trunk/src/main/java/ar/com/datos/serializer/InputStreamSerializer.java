package ar.com.datos.serializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.NumberSerializer;
import ar.com.datos.serializer.common.SerializerCache;

/**
 * {@link Serializer} de InputStreams.
 * Los objetos seran serializados como CantBytesByte1...ByteN
 * CantBytes se serializa por defecto como un Integer, pero esto puede ser cambiado usando
 * el cardinalitySerializer.
 * La cantidad de bytes se serializa como unsigned (la conversion se hace de manera
 * automatica) por lo que se dispondra del doble de la capacidad definida por el
 * cardinalitySerializer establecido.
 * La cantidad de bytes serializados quedara delimitada por el cardinalitySerializer
 * (ejemplo: si usamos un IntegerSerializer como cardinalitySerializer se serializara
 * un maximo de 2^32 bytes, luego de esto se corta la serializacion). No se hara
 * ningun warning ni nada en este caso, solo se "truncara".
 * 
 * @author fvalido
 */
public class InputStreamSerializer implements Serializer<InputStream> {
	/**
	 * {@link Serializer} a usar para la cantidad de bytes del Stream.
	 */
	private NumberSerializer cardinalitySerializer;
	
	/**
	 * Permite construir una instancia.
	 * Para la cantidad de bytes se usara un {@link IntegerSerializer}.
	 */
	public InputStreamSerializer() {
		this.cardinalitySerializer = (IntegerSerializer)SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
	}
	
	/**
	 * Permite construir una instancia.
	 * 
	 * @param cardinalitySerializer
	 * {@link Serializer} para la cantidad de bytes.
	 */
	public InputStreamSerializer(NumberSerializer cardinalitySerializer) {
		if (LongSerializer.class.isAssignableFrom(cardinalitySerializer.getClass())) {
			throw new UnsupportedOperationException();
		}
		this.cardinalitySerializer = cardinalitySerializer;
	}
	
	/**
	 * Permite cambiar el {@link Serializer} para la cantidad de bytes.
	 */
	public void setCardinalitySerializer(NumberSerializer cardinalitySerializer) {
		if (LongSerializer.class.isAssignableFrom(cardinalitySerializer.getClass())) {
			throw new UnsupportedOperationException();
		}
		this.cardinalitySerializer = cardinalitySerializer;
	}
	
	/**
	 * Permite obtener el tipo parametrizado de una clase. 
	 */
	private Class getReturnClass(Class clazz){
          ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
          return (Class)parameterizedType.getActualTypeArguments()[0];
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public void dehydrate(OutputBuffer output, InputStream object) {
		// Tengo que poner al principio la cantidad de bytes totales... Pero InputStream no
		// tiene un metodo para obtener esa cantidad total de bytes, por tanto debo
		// leer todo en una estructura auxiliar, contar los bytes leidos y recien ahi escribir
		// en output la cantidad de bytes totales y volcar la estructura auxiliar.
		
		int totalBytes = 0;
		Collection<byte[]> streamParts = new LinkedList<byte[]>();
		byte[] read;
		
		try {
			InputStream stream = object;

			int available = stream.available();
			while (available > 0) { 
				read = new byte[available];
				stream.read(read);
				streamParts.add(read);
				totalBytes += available;
			
				available = stream.available();
			}
		} catch (IOException e) {
			// No deberia suceder, pero si lo hace muestro la excepcion (es medio sucio, pero...).
			e.printStackTrace();
		}
		
		// Tengo en streamParts todos los bytes leidos y en totalBytes la cantidad de bytes leidos.
				
		// Obtengo la cantidad de bytes como Unsigned.
		int maxNumber = (int)Math.pow(2, this.cardinalitySerializer.getDehydrateSize(null) * 8);
		// Dejo el maximo posible
		int size = (maxNumber - 1 > totalBytes) ? totalBytes : maxNumber - 1;
		// lo hago unsigned.
		size = size - maxNumber / 2;
		
		// Deshidrato la cantidad de bytes
		try {
			Class numberClass = getReturnClass(cardinalitySerializer.getClass());
			String numberString = new Long(size).toString();
			Method valueOfMethod = numberClass.getMethod("valueOf", new Class[] { String.class });
			valueOfMethod.setAccessible(true);
			Number cardinality = (Number)valueOfMethod.invoke(numberClass, numberString);
			valueOfMethod.setAccessible(false);
			this.cardinalitySerializer.dehydrate(output, cardinality);
		} catch (Exception e) {
			// Excepciones que no ocurriran.
		}
		
		// Vuelco en el buffer los bytes de la estructura auxiliar
		Iterator<byte[]> it = streamParts.iterator();
		int i = 1;
		
		while (it.hasNext() && i < maxNumber) {
			read = it.next();
			if (i + read.length > maxNumber) {
				// Ultimos bytes que entran.
				for (int j = 0; j < maxNumber - i; j++) {
					output.write(read[i + j]);
				}
			} else {
				output.write(read);
			}
			i += read.length;
		}
		
	}

	/**
	 * De ser posible no usar !! Es (casi) tan pesado como dehydrate()
	 * 
	 * @see Serializer#getDehydrateSize(Object)}
	 * @see InputStream#mark(int)
	 * 
	 * @throws UnsupportedOperationException
	 * Si el metodo {@link InputStream#markSupported()} devuelve false
	 */
	public long getDehydrateSize(InputStream object) throws UnsupportedOperationException {
		InputStream stream = object;
		
		if (!stream.markSupported()) {
			throw new UnsupportedOperationException("El stream pasado no permite precalcular el DehydrateSize.");
		}
		// Marco para volver a este lugar del Stream.
		stream.mark(Integer.MAX_VALUE);
		
		// Contabilizo la cantidad total de bytes
		int totalBytes = 0;

		try {
		
			int available = stream.available();
			int oldAvailable = available;
			byte[] read = new byte[available];
			while (available > 0) { 
				if (available != oldAvailable) {
					read = new byte[available];
				}
				stream.read(read);
				totalBytes += available;
			
				oldAvailable = available;
				available = stream.available();
			}

			// Vuelvo a la posicion que marque.
			stream.reset();
		} catch (IOException e) {
			// No deberia suceder, pero si lo hace muestro la excepcion (es medio sucio, pero...).
			e.printStackTrace();
		}
		
		return totalBytes;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@SuppressWarnings("unchecked")
	public InputStream hydrate(InputBuffer input) {
		// Primero recupero la cantidad de bytes
		Number cardinality = (Number)this.cardinalitySerializer.hydrate(input);

		// Obtengo la cantidad de elementos como Unsigned.
		int maxNumber = (int)Math.pow(2, this.cardinalitySerializer.getDehydrateSize(null) * 8);
		int size = cardinality.intValue() + maxNumber / 2;
		
		// Y ahora hidrato un InputStream
		byte[] bytes = new byte[size];
		input.read(bytes);
		return new ByteArrayInputStream(bytes);
	}

}
