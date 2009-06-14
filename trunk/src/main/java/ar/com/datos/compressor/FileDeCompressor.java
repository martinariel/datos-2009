package ar.com.datos.compressor;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.documentlibrary.Document;

/**
 * Interfaz con la que debe cumplir un descompresor de archivos.
 */
public interface FileDeCompressor {
	/**
	 * A partir de los datos brindados por el {@link InputBuffer} arma el 
	 * {@link Document} pasado.
	 */
	public void decompress(InputBuffer input, Document document) throws CompressorException;
	
	public String getCompressorName();
}
