package ar.com.datos.buffer;

/**
 * Interfacez utilizada para cargar datos a manera de buffer
 * @author Juan Manuel Barreneche
 *
 */
public interface OutputBuffer {

	/**
	 * agrega los <b>datos</b> al final de la entidad/registro actual  
	 * @param data
	 */
	public void write(byte[] data);
	
	/**
	 * Agrega el <b>dato</b> al final de la entidad/registro actual.
	 * @param data
	 */
	public void write(byte data);
}
