package ar.com.datos.file.address;
/**
 * Interfaz para todos los objetos que representan una dirección en un archivo
 * 
 * @author dev
 *
 */
public interface Address {

	/**
	 * @return abstracción, de longitud fija para mismo tipo de address, de la dirección
	 * representada por este address
	 */
	public String getUnifiedAddress();
}
