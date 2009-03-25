package ar.com.datos.file.variableLength;

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
	 * @param datos
	 */
	public void write(byte[] datos);
	
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
	
}
