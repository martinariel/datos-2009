package ar.com.datos.buffer;


/**
 * Las clases que implementan esta interfaz, son las encargadas de
 * liberar desagotar un buffer que excedió su capacidad.
 * @author dev
 *
 */
public interface RestrictedBufferRealeaser {
	/**
	 * Extrae datos del buffer hasta que quede con carga menor a 
	 * su capacidad
	 * @param ob
	 */
	public void release(RestrictedOutputBuffer ob);
}
