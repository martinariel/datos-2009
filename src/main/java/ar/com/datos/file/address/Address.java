package ar.com.datos.file.address;
/**
 * Interfaz para todos los objetos que representan una direcci�n en un archivo
 * 
 * @author dev
 *
 */
public interface Address {

	/**
	 * @return abstracci�n, de longitud fija para mismo tipo de address, de la direcci�n
	 * representada por este address
	 */
	public String getUnifiedAddress();
}
