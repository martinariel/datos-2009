package ar.com.datos.buffer;


public interface EntityOutputBuffer extends OutputBuffer {

	/**
	 * Marca el fin de entidad/registro y avisa si hay exceso
	 */
	public void closeEntity();

	/**
	 * Devuelve la cantidad de Entidades que fueron agregadas (cantidad de closeEntity)
	 * @return
	 */
	public Short getEntitiesCount();

}