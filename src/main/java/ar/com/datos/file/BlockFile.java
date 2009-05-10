package ar.com.datos.file;

import java.util.Collection;

import ar.com.datos.buffer.variableLength.ArrayByte;

/**
 * Abstracción de archivo por bloques
 * La numeración de los bloques va de cero a n-1 siendo n la cantidad total de bloques
 * @author dev
 *
 */
public interface BlockFile extends Iterable<byte[]> {

	Long END_BLOCK = -1L;
	/**
	 * Intenta leer el bloque con n�mero blockNumber 
	 */
	public byte[] readBlock(Long blockNumber);
	/**
	 * Intenta escribir el bloque con n�mero blockNumber 
	 */
	public void writeBlock(Long blockNumber, byte[] block);
	/**
	 * Intenta escribir el bloque formado por las partes con n�mero blockNumber 
	 */
	public void writeBlock(Long blockNumber, Collection<ArrayByte> partes);
	/**
	 * Agrega al final el nuevo bloque recibido. La cantidad total de bloques
	 * aumenta en una unidad
	 */
	public void appendBlock(byte[] block);
	/**
	 * Devuelve el tamaño de bloques configurado
	 */
	public Integer getBlockSize();
	/**
	 * Devuelve la cantidad de bloques existentes.
	 */
	public Long getTotalBlocks();
	/**
	 * Libera los recursos de lectura y escritura que fueron tomados.
	 */
	public void close();
}
