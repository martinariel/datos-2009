package ar.com.datos.compressor.lzp.file;

import java.io.PrintStream;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.CompressorException;
import ar.com.datos.compressor.FileDeCompressor;
import ar.com.datos.compressor.lzp.LzpDeCompressor;
import ar.com.datos.documentlibrary.Document;

/**
 * {@link FileDeCompressor} que descomprime usando un lzp.
 * 
 * @author fvalido
 */
public class LzpFileDeCompressor extends LzpDeCompressor implements FileDeCompressor {
	/**
	 * Constructor.
	 */
	public LzpFileDeCompressor() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param tracer
	 * Establece donde enviar los datos correspondientes al trace (p/DEBUG).
	 */
	public LzpFileDeCompressor(PrintStream tracer) {
		super(tracer);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.FileDeCompressor#decompress(ar.com.datos.buffer.InputBuffer, ar.com.datos.documentlibrary.Document)
	 */
	@Override
	public void decompress(InputBuffer input, Document document) throws CompressorException {
		document.addLine(decompress(input));
	}

	public String getCompressorName() {
		return "LZP";
	}
}
