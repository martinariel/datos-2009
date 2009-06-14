package ar.com.datos.compressor;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.documentlibrary.Document;

/**
 * Interfaz con la que debe cumplir un compresor de archivos.
 */
public interface FileCompressor {
	/**
	 * Comprime el {@link Document} recibido dejando el documento comprimido en el
	 * {@link OutputBuffer}.
	 */
	public void compress(Document documento, OutputBuffer output);
}
