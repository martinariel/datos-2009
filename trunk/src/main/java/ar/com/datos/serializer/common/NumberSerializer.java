package ar.com.datos.serializer.common;

import ar.com.datos.serializer.Serializer;

/**
 * {@link Serializer} para los tipos primitivos relacionados con Numeros..
 * 
 * @author fvalido
 *
 * @param <T>
 * Tipo de numero en particular.
 */
public abstract class NumberSerializer<T extends Number> implements Serializer<T> {

}
