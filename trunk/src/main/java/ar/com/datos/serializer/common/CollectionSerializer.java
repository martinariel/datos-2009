package ar.com.datos.serializer.common;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} generico de colecciones.
 * Los objetos seran serializados como CantObjetosObjeto1...ObjetoN
 * CantObjetos se serializa por defecto como un Short, pero esto puede ser cambiado usando
 * el cardinalitySerializer.
 * La cantidad de objetos se serializa como unsigned (la conversion se hace de manera
 * automatica) por lo que se dispondra del doble de la capacidad definida por el
 * cardinalitySerializer establecido.
 * La cantidad de objetos serializados quedara delimitada por el cardinalitySerializer
 * (ejemplo: si usamos un ByteSerializer como cardinalitySerializer se serializara
 * un maximo de 255 objetos, luego de esto se corta la serializacion). No se hara
 * ningun warning ni nada en este caso, solo se "truncara".
 * 
 * @author fvalido
 *
 * @param <T>
 * Tipo de las colecciones a usar.
 */
public class CollectionSerializer<T> implements Serializer<Collection<T>> {
	/**
	 * {@link Serializer} a usar con el tipo T.
	 */
	private Serializer<T> baseSerializer;
	/**
	 * {@link Serializer} a usar para la cantidad de objetos de la coleccion.
	 */
	private NumberSerializer cardinalitySerializer;
	
	/**
	 * Permite construir una instancia para colecciones del tipo T.
	 * Para la cantidad de elementos de la coleccion se usara un {@link ShortSerializer}.
	 * 
	 * @param baseSerializer
	 * {@link Serializer} para el tipo T de la coleccion a usar.
	 */
	public CollectionSerializer(Serializer<T> baseSerializer) {
		this.baseSerializer = baseSerializer;
		this.cardinalitySerializer = (ShortSerializer)SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	}

	/**
	 * Permite construir una instancia para colecciones del tipo T.
	 * 
	 * @param baseSerializer
	 * {@link Serializer} para el tipo T de la coleccion a usar.
	 * 
	 * @param cardinalitySerializer
	 * {@link Serializer} para la cantidad de elementos de la coleccion.
	 */
	public CollectionSerializer(Serializer<T> baseSerializer, NumberSerializer cardinalitySerializer) {
		this.baseSerializer = baseSerializer;
		this.cardinalitySerializer = cardinalitySerializer;
	}
	
	/**
	 * Permite cambiar el {@link Serializer} para el tipo T de la coleccion a usar.
	 */
	public void setBaseSerializer(Serializer<T> baseSerializer) {
		this.baseSerializer = baseSerializer;
	}
	
	/**
	 * Permite cambiar el {@link Serializer} para la cantidad de elementos de la coleccion.
	 */
	public void setCardinalitySerializer(NumberSerializer cardinalitySerializer) {
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
	public void dehydrate(OutputBuffer output, Collection<T> object) {
		Collection<T> value = object;

		// Obtengo la cantidad de elementos de la coleccion como Unsigned.
		long maxNumber = (long)Math.pow(2, this.cardinalitySerializer.getDehydrateSize(null) * 8);
		// Dejo el maximo posible
		long size = (maxNumber - 1 > value.size()) ? value.size() : maxNumber - 1;
		// lo hago unsigned.
		size = size - maxNumber / 2;		
		
		// Deshidrato la cantidad de elementos de la coleccion
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
		
		// A continuacion deshidrato cada elemento
		Iterator<T> it = value.iterator();
		long i = 1;
		while (it.hasNext() && i < maxNumber) {
			this.baseSerializer.dehydrate(output, it.next());
			i++;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> hydrate(InputBuffer input) {
		Collection<T> returnValue = new LinkedList<T>();
		
		// Primero recupero la cantidad de objetos
		Number cardinality = (Number)this.cardinalitySerializer.hydrate(input);

		// Obtengo la cantidad de elementos como Unsigned.
		long maxNumber = (long)Math.pow(2, this.cardinalitySerializer.getDehydrateSize(null) * 8);
		long size = cardinality.longValue() + maxNumber / 2;
		
		// Y ahora hidrato cada objeto.
		for (int i = 0; i < size; i++) {
			returnValue.add(this.baseSerializer.hydrate(input));
		}
		
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public long getDehydrateSize(Collection<T> object) {
		Collection<T> value = object;
		
		long size = this.cardinalitySerializer.getDehydrateSize(null);
		Iterator<T> it = value.iterator();
		T currentObject;
		while (it.hasNext()) {
			currentObject = it.next();
			size += this.baseSerializer.getDehydrateSize(currentObject);
		}
		return size;
	}

	
}
