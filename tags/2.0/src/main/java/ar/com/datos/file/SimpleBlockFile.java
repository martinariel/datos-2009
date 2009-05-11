package ar.com.datos.file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.exception.ValidacionIncorrectaException;
import ar.com.datos.file.exception.InvalidBlockException;


/**
 * Wrapper para la clase File para archivos por bloque
 * @author dev
 *
 */
public class SimpleBlockFile extends StandardFileWrapper implements BlockFile {

	private Integer blockSize;
	/**
	 * Construye un SimpleBlockFile que usa un archivo temporal de Java
	 * <code>File.createTempFile()</code>
	 */
	public SimpleBlockFile(Integer blockSize) {
		super();
		setBlockSize(blockSize);
		verifyFile();
	}

	/**
	 * Construye un SimpleBlockFile que usa el archivo en la ruta especificada
	 */
	public SimpleBlockFile(String string, Integer blockSize) {
		super(string);
		setBlockSize(blockSize);
		verifyFile();
	}
	/**
	 * Verifica la integridad del archivo y los permisos sobre el mismo.
	 * En caso de no existir también lo crea
	 */
	protected void verifyFile() {
		super.verifyFile();
		
		if (this.getFile().length() % this.getBlockSize() != 0)
			throw new ValidacionIncorrectaException("Cantidad de bloques inconsistente");
	}
	public Integer getBlockSize() {
		return blockSize;
	}
	protected void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}
	@Override
	public Long getTotalBlocks() {
		return this.getFile().length() / this.getBlockSize();
	}
	@Override
	public void writeBlock(Long blockNumber, byte[] block) {
		ArrayList<ArrayByte> blockAsCollection = new ArrayList<ArrayByte>(1);
		blockAsCollection.add(new SimpleArrayByte(block));
		this.writeBlock(blockNumber, blockAsCollection);
	}
	@Override
	public void writeBlock(Long blockNumber, Collection<ArrayByte> partes) {
		Integer sumaDeLasPartes = 0;
		for (ArrayByte ab : partes) sumaDeLasPartes += ab.getLength();
		if (!this.getBlockSize().equals(sumaDeLasPartes)) {
			throw new InvalidBlockException("Se esperaba un bloque de tamaño " + getBlockSize());
		}
		write(getOffsetFor(blockNumber), partes);
	}
	
	@Override
	public void appendBlock(byte[] block) {
		this.writeBlock(getTotalBlocks(), block);
	}
	@Override
	public byte[] readBlock(Long blockNumber) {
		return read(getOffsetFor(blockNumber), getBlockSize());
	}

	protected Long getOffsetFor(Long blockNumber) {
		return blockNumber * getBlockSize();
	}

	@Override
	public Iterator<byte[]> iterator() {
		return new SimpleBlockFileIterator(this);
	}
	private class SimpleBlockFileIterator implements Iterator<byte[]> {

		private Long currentBlock = 0L;
		private SimpleBlockFile sbf;
		public SimpleBlockFileIterator(SimpleBlockFile sbf) {
			this.sbf = sbf;
		}
		@Override
		public boolean hasNext() {
			return sbf.getTotalBlocks() > currentBlock;
		}

		@Override
		public byte[] next() {
			return sbf.readBlock(currentBlock++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
