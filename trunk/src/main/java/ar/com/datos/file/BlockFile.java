package ar.com.datos.file;

public interface BlockFile {

	/**
	 * Intenta leer el bloque cuyo con número blockNumber 
	 * @param blockNumber
	 * @return
	 */
	public byte[] readBlock(Long blockNumber);
	/**
	 * Agrega al final el nuevo bloque recibido. La cantidad total de bloques
	 * aumenta en una unidad
	 * @param block
	 */
	public void appendBlock(byte[] block);
	/**
	 * Devuelve el tamaño de bloques configurado
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
