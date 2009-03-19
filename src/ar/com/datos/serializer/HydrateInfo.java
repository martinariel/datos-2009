package ar.com.datos.serializer;



/**
 * Agrupacion de un {@link T} hidratado y una tira de byte[].
 *
 * @see Serializer
 * @author fvalido
 *
 * @param <T>
 * Clase del objeto hidratado.
 */
public class HydrateInfo<T> {
	private T object;
	private byte[] remaining;
	
	public HydrateInfo(T object, byte[] remaining) {
		this.object = object;
		this.remaining = remaining;
	}
	
	/**
	 * Obtiene el objeto hidratado correspondiente a esta hidratacion.
	 */
	public T getHydratedObject() {
		return this.object;
	}

	/**
	 * Obtiene la tira de byte[] restante luego de realizar la hidratacion
	 * @see Serializer#hydrate(byte[])
	 */
	public byte[] getHydrateRemaining() {
		return this.remaining;
	}
}
