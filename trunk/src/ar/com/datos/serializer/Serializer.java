package ar.com.datos.serializer;


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
	 */
	public byte[] dehydrate(T object);

	/**
	 * Rehidrata un objeto del tipo {@link T} a partir de una tira de byte[] obtenida
	 * mediante el metodo {@link #dehydrate(Object)}.
	 * Puede haber informacion informacion extra al final de la tira de byte[] pero debe
	 * garantizarse que la informacion del principio coincida con la informacion que
	 * devolvio anteriormente el metodo {@link #dehydrate(Object)}.
	 *
	 * @return
	 * Una instancia de {@link HydrateInfo} con la informacion correspondiente a la
	 * hidratacion actual. Esta incluye el objeto hidratado y una NUEVA tira de byte[]
	 * tomada de la tira de byte[] pasada a la que se le quitaron los bytes correspondientes
	 * al objeto hidratado.
	 *
	 * @see HydrateInfo
	 */
	public HydrateInfo<T> hydrate(byte[] preObject);

	/**
	 * Obtiene la cantidad de bytes (tamanio del array) necesaria para deshidratar el
	 * objeto pasado.
	 * La implementacion debe ser de tal forma que:
	 * (dehydrate(myObject).length == getDehydrateSize(myObject)) sea true.
	 */
	public int getDehydrateSize(T object);
}
