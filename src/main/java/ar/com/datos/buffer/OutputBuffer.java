package ar.com.datos.buffer;

import java.util.Collection;

import ar.com.datos.buffer.variableLength.ArrayByte;

/**
 * Interfacez creada para hacer algunas pruebas
 * debería ser reemplazada por la de Marcos
 * @author Juan Manuel Barreneche
 *
 */
public interface OutputBuffer {

	/**
	 * indica si el tamaño almacenado por el buffer es mayor
	 * a la capacidad para la cual fue definida
	 * @return
	 */
	public Boolean isOverloaded();
	
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
	
	/**
	 * Marca el fin de entidad/registro y avisa si hay exceso
	 */
	public void closeEntity();
	
	/**
	 * Devuelve todos los byte[] que fueron escritos <b>excepto</b> los pertenecientes
	 * a la última entidad que se cerró
	 */
	public Collection<ArrayByte> extractAllButLast();
	
	/**
	 * Devuelve todos los byte[] que fueron escritos pertenecientes a la última entidad que se cerró
	 */
	public Collection<ArrayByte> extractLast();

	/**
	 * Devuelve la cantidad de Entidades que fueron agregadas (cantidad de closeEntity)
	 * @return
	 */
	public Short getEntitiesCount();
}
