package ar.com.datos.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import ar.com.datos.file.exception.InvalidBlockException;
import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.file.exception.ValidacionIncorrectaException;


/**
 * Wrapper para la clase File para archivos por bloque
 * @author dev
 *
 */
public class SimpleBlockFile implements BlockFile {

	private File file;
	private Integer blockSize;
	private RandomAccessFile fileAccessor;
	
	public SimpleBlockFile(String string, Integer blockSize) {
		setBlockSize(blockSize);
		setFile(constructFile(string));
		verifyFile();
	}
	/**
	 * Verifica la integridad del archivo y los permisos sobre el mismo.
	 * En caso de no existir también lo crea
	 */
	protected void verifyFile() {
		if (!this.getFile().exists()) {
			try {
				this.getFile().createNewFile();
			} catch (IOException e) {
				throw new ValidacionIncorrectaException(e);
			}
		}
		
		if (!(this.getFile().canRead() && this.getFile().canWrite()))
			throw new ValidacionIncorrectaException("Problemas de Lectura/Escritura");

		if (this.getFile().length() % this.getBlockSize() != 0)
			throw new ValidacionIncorrectaException("Cantidad de bloques inconsistente");
	}
	/**
	 * Método de construcción del randomAccesFile
	 * Puede ser rescrito por las subclases o modificado para que los tests no trabajen con archivos
	 * reales
	 * @param archivo
	 * @return
	 */
	protected RandomAccessFile constructAccesor(File archivo) {
		try {
			return new RandomAccessFile(this.getFile(),"rw");
		} catch (FileNotFoundException e) {
			throw new ValidacionIncorrectaException(e);
		}
	}
	/**
	 * Método de construcción del File
	 * Puede ser rescrito por las subclases o modificado para que los tests no trabajen con archivos
	 * reales
	 * @param archivo
	 * @return
	 */
	protected File constructFile(String string) {
		return new File(string);
	}
	public Integer getBlockSize() {
		return blockSize;
	}
	protected void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}
	public File getFile() {
		return file;
	}
	private void setFile(File file) {
		this.file = file;
	}
	public RandomAccessFile getFileAccessor() {
		if (fileAccessor == null) {
			setFileAccessor(constructAccesor(getFile()));
		}
			
		return fileAccessor;
	}
	private void setFileAccessor(RandomAccessFile fileAccessor) {
		this.fileAccessor = fileAccessor;
	}
	@Override
	public Long getTotalBlocks() {
		return this.getFile().length() / this.getBlockSize();
	}
	@Override
	public void writeBlock(Long blockNumber, byte[] block) {
		if (!this.getBlockSize().equals(block.length)) {
			throw new InvalidBlockException("Se esperaba un bloque de tamaño " + getBlockSize());
		}
		try {
			seekBlock(blockNumber);
			getFileAccessor().write(block);
		} catch (IOException e) {
			// TODO Ver en que caso podría tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
	}
	private void seekBlock(Long blockNumber) throws IOException {
		getFileAccessor().seek(blockNumber * getBlockSize());
	}
	@Override
	public void appendBlock(byte[] block) {
		this.writeBlock(getTotalBlocks(), block);
	}
	@Override
	public byte[] readBlock(Long blockNumber) {
		try {
			seekBlock(blockNumber);
			byte[] leido = new byte[getBlockSize()];
			if (getFileAccessor().read(leido) == -1) throw new OutOfBoundsException(); 
			return leido;
		} catch (IOException e) {
			// TODO Ver en que caso podría tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
	}
	@Override
	public void close() {
		if (fileAccessor != null) {
			try {
				fileAccessor.close();
			} catch (IOException e) {
				// TODO Ver en que caso podría tirar esta excepción y hacer
				// un manejo apropiado de la misma
				throw new RuntimeException(e);
			}
			fileAccessor = null;
		}
		
	}
}
