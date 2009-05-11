package ar.com.datos.serializer;

/**
 * Permite que una clase pueda indicar el {@link Serializer} que permite serializarla.
 * Normalmente el metodo getSerializer() sera implementado devolviendo un objeto de una clase anonima
 * (aunque esto no es obligatorio).
 * 
 * @author fvalido
 *
 * @param <T>
 * Debe ser la misma clase que implementa esta interface.
 */
public interface Serializable<T> {
    /**
     * Permite obtener el {@link Serializer} correspondiente a la clase que la implementa.
     */
    public Serializer<T> getSerializer();
}
