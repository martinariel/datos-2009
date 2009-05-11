package ar.com.datos.file;

import ar.com.datos.file.address.Address;


public interface DynamicAccesor<A extends Address, T> extends SequentialAccesor<A, T> {

	/**
	 * Recupera la entidad almacenada en la direccion recibida.
	 * Si no existe arroja <code>InvalidAddressException</code>
	 * @param address dirección de búsqueda
	 * @return objeto en la dirección <code>address</code>
	 */
	public T get(A address);

}
