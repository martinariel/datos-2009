package ar.com.datos.file;

import java.util.Collection;

import ar.com.datos.buffer.variableLength.SimpleArrayByte;

/**
 * AbstracciÃ³n de archivo por bloques
 * La numeraciÃ³n de los bloques va de cero a n-1 siendo n la cantidad total de bloques
 * @author dev
 *
 */
public interface BlockFile {

	Long END_BLOCK = -1L;
	/**
	 * Intenta leer el bloque con número blockNumber 
	 * @param blockNumber
	 * @return
	 */
	public byte[] readBlock(Long blockNumber);
	/**
	 * Intenta escribir el bloque con número blockNumber 
	 * @param blockNumber
	 * @return
	 */
	public void writeBlock(Long blockNumber, byte[] block);
	/**
	 * Intenta escribir el bloque formado por las partes con número blockNumber 
	 * @param blockNumber
	 * @return
	 */
	public void writeBlock(Long blockNumber, Collection<SimpleArrayByte> partes);
	/**
	 * Agrega al final el nuevo bloque recibido. La cantidad total de bloques
	 * aumenta en una unidad
	 * @param block
	 */
	public void appendBlock(byte[] block);
	/**
	 * Devuelve el tamaÃ±o de bloques configurado
	 * @return
	 */
	public Integer getBlockSize();
	/**
	 * Devuelve la cantidad de bloques existentes.
	 * @return
	 */
	public Long getTotalBlocks();
	/**
	 * Libera los recursos de lectura y escritura que fueron tomados.
	 */
	public void close();
}
