package ar.com.datos.compressor.lzp;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.lzp.text.impl.DocumentTextEmisor;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.MemoryDocument;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de {@link Document} que usa un {@link LzpCompressor} para
 * realizar la serialización.
 * 
 * @author fvalido.
 */
public class LzpSerializer implements Serializer<Document> {
	/** Compresor lzp a usar para la hidratación */
	private LzpCompressor lzpCompressor;
	/** Descompresor lzp a usar para la deshidratación */
	private LzpDeCompressor lzpDeCompressor;
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, Document object) throws SerializerException {
		this.lzpCompressor.setArithmeticCompressor(new ArithmeticEmissor(output));
		this.lzpCompressor.compress(new DocumentTextEmisor(object));
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public Document hydrate(InputBuffer input) throws SerializerException {
		this.lzpDeCompressor.setArithmeticCompressor(new ArithmeticInterpreter(input));
		
		MemoryDocument memoryDocument = new MemoryDocument();
		memoryDocument.addLine(this.lzpDeCompressor.decompress());
		
		return memoryDocument;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(Document object) {
		throw new UnsupportedOperationException();
	}
}
