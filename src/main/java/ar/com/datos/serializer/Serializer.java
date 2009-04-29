package ar.com.datos.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.exception.SerializerException;


/**
 * Permite deshidratar en una tira de byte[] un objeto del tipo {@link T} e hidratarlo
 * de nuevo. La tira de byte[] contendra la informacion de control necesaria para poder
 * realizar la hidratacion y reconocer el objeto.
 *
 * @author fvalido
 *
 * @param <T>
 * Clase sobre la que actua el Serializer
 */
public interface Serializer<T> {

	/**
	 * Deshidrata un objeto del tipo {@link T} en una tira de byte[]. Dicha tira incluira
	 * la informacion de control necesaria para realizar la hidratacion de dicho objeto.
	 * El objeto deshidratado sera agregado al {@link OutputBuffer} recibido.
	 * 
	 * @throws SerializerException
	 * Si hay algún problema deshidratando el objeto.
	 */
	public void dehydrate(OutputBuffer output, T object) throws SerializerException;

	/**
	 * Rehidrata un objeto del tipo {@link T} a partir de una tira de byte[] que fue generada
	 * previamente mediante el metodo {@link #dehydrate(Object)}. Esta tira de bytes es obtenida
	 * desde el {@link InputBuffer} recibido, solo se piden los bytes estrictamente necesarios a
	 * este.
	 * 
	 * @throws SerializerException
	 * Si hay algún problema hidratando el objeto.
	 */
	public T hydrate(InputBuffer input) throws SerializerException;

	/**
	 * Obtiene la cantidad de bytes (tamanio del array) necesaria para deshidratar el
	 * objeto pasado.
	 */
	public long getDehydrateSize(T object);
}
