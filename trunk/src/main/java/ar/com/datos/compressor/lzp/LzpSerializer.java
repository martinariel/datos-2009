package ar.com.datos.compressor.lzp;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.text.impl.DocumentTextEmisor;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.MemoryDocument;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

/**
 * Serializador de {@link Document} que usa un {@link LzpCompressor}/{@link LzpDeCompressor} para
 * realizar la serialización.
 * 
 * @author fvalido.
 */
public class LzpSerializer implements Serializer<Document> {
	/** Compresor lzp a usar para la hidratación */
	private LzpCompressor lzpCompressor;
	/** Descompresor lzp a usar para la deshidratación */
	private LzpDeCompressor lzpDeCompressor;
	
	/**
	 * Constructor.
	 */
	public LzpSerializer() {
		this.lzpCompressor = new LzpCompressor();
		this.lzpDeCompressor = new LzpDeCompressor();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, Document object) throws SerializerException {
		this.lzpCompressor.compress(new DocumentTextEmisor(object), output);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public Document hydrate(InputBuffer input) throws SerializerException {
		MemoryDocument memoryDocument = new MemoryDocument();
		memoryDocument.addLine(this.lzpDeCompressor.decompress(input));
		
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
