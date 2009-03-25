package ar.com.datos.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;


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
	 */
	public void dehydrate(OutputBuffer output, T object);

	/**
	 * Rehidrata un objeto del tipo {@link T} a partir de una tira de byte[] que fue generada
	 * previamente mediante el metodo {@link #dehydrate(Object)}. Esta tira de bytes es obtenida
	 * desde el {@link InputBuffer} recibido, solo se piden los bytes estrictamente necesarios a
	 * este.
	 *
	 * @see HydrateInfo
	 */
	public T hydrate(InputBuffer input);

	/**
	 * Obtiene la cantidad de bytes (tamanio del array) necesaria para deshidratar el
	 * objeto pasado.
	 */
	public long getDehydrateSize(T object);
}
