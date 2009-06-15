package ar.com.datos.file;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.exception.ValidacionIncorrectaException;
import ar.com.datos.file.exception.OutOfBoundsException;

public class StandardFileWrapper implements Closeable {

	private File file;
	private RandomAccessFile fileAccessor;

	/**
	 * Construye un SimpleBlockFile que usa un archivo temporal de Java
	 * <code>File.createTempFile()</code>
	 */
	public StandardFileWrapper() {
		super();
		setFile(constructTempFile());
	}

	/**
	 * Construye un SimpleBlockFile que usa el archivo en la ruta especificada
	 */
	public StandardFileWrapper(String string) {
		super();
		setFile(constructFile(string));
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
	
	}

	/**
	 * Método de construcción del randomAccesFile
	 * Puede ser rescrito por las subclases o modificado para que los tests no trabajen con archivos
	 * reales
	 */
	protected RandomAccessFile constructAccesor() {
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
	 */
	protected File constructFile(String string) {
		return new File(string);
	}

	protected File constructTempFile() {
		try {
			return File.createTempFile(new SimpleDateFormat("yyMMdd_HHmmssSSSS").format(new Date()),"simpleBlockFileTemporal");
		} catch (IOException e) {
			// XXX Ver en que caso podrí­a tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
	}

	public File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}

	protected RandomAccessFile getFileAccessor() {
		if (fileAccessor == null) {
			setFileAccessor(constructAccesor());
		}
			
		return fileAccessor;
	}

	protected void setFileAccessor(RandomAccessFile fileAccessor) {
		this.fileAccessor = fileAccessor;
	}

	public byte[] read(Long from, Integer ammount) {
		try {
			getFileAccessor().seek(from);
			byte[] leido = new byte[ammount];
			if (getFileAccessor().read(leido) == -1) throw new OutOfBoundsException(); 
			return leido;
		} catch (IOException e) {
			// XXX Ver en que caso podrí­a tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
		
	}

	public byte[] read(Long from, byte[] target) {
		try {
			getFileAccessor().seek(from);
			if (getFileAccessor().read(target) == -1) throw new OutOfBoundsException(); 
			return target;
		} catch (IOException e) {
			// XXX Ver en que caso podrí­a tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
		
	}

	public void write(Long offset, Collection<ArrayByte> partes) {
		try {
			getFileAccessor().seek(offset);
			for (ArrayByte ab : partes) getFileAccessor().write(ab.getArray());
		} catch (IOException e) {
			// XXX Ver en que caso podrí­a tirar esta excepción y hacer
			// un manejo apropiado de la misma
			throw new RuntimeException(e);
		}
	}

	public void write(Long offset, ArrayByte...partes) {
		this.write(offset, Arrays.asList(partes));
	}
	@Override
	public void close() {
		if (fileAccessor != null) {
			try {
				fileAccessor.close();
			} catch (IOException e) {
				// XXX Ver en que caso podrí­a tirar esta excepción y hacer
				// un manejo apropiado de la misma
				throw new RuntimeException(e);
			}
			fileAccessor = null;
		}
		
	}

	public Boolean isEmpty() {
		return this.getSize().equals(0L);
	}

	public Long getSize() {
		return this.getFile().length();
	}
}