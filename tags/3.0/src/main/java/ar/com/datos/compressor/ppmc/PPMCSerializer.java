package ar.com.datos.compressor.ppmc;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.MemoryDocument;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

public class PPMCSerializer implements Serializer<Document> {
	
	public PPMCSerializer() {}

	@Override
	public void dehydrate(OutputBuffer output, Document document)
			throws SerializerException {
		
		// creo el compresor PPMC y comprimo el documento
		PPMCCompressor compressor = new PPMCCompressor();
		compressor.compress(document, output);
	}

	@Override
	public Document hydrate(InputBuffer input) throws SerializerException {
		MemoryDocument document = new MemoryDocument();
		// creo el descompresor PPMC y descomprimo del input buffer
		PPMCDecompressor decompressor = new PPMCDecompressor();
		document.addLine(decompressor.decompress(input));
		return document;
	}
	
	@Override
	public long getDehydrateSize(Document object) {
		throw new UnsupportedOperationException();
	}
}
