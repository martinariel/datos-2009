package ar.com.datos.compressor.lzp.file;

import java.io.PrintStream;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.FileCompressor;
import ar.com.datos.compressor.lzp.LzpCompressor;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.compressor.lzp.text.impl.DocumentTextEmisor;
import ar.com.datos.documentlibrary.Document;

/**
 * {@link FileCompressor} que comprime usando un lzp.
 * 
 * @author fvalido
 */
public class LzpFileCompressor extends LzpCompressor implements FileCompressor {
	/**
	 * Constructor.
	 */
	public LzpFileCompressor() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param tracer
	 * Establece donde enviar los datos correspondientes al trace (p/DEBUG).
	 */
	public LzpFileCompressor(PrintStream tracer) {
		super(tracer);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.FileCompressor#compress(ar.com.datos.documentlibrary.Document, ar.com.datos.buffer.OutputBuffer)
	 */
	@Override
	public void compress(Document documento, OutputBuffer output) {
		TextEmisor textEmisor = new DocumentTextEmisor(documento);
		compress(textEmisor, output);		
	}

}
