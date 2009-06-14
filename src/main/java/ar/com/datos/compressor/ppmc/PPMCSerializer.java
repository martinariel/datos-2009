package ar.com.datos.compressor.ppmc;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.documentlibrary.Document;
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
		// creo el descompresor PPMC y descomprimo del input buffer
		PPMCDecompressor decompressor = new PPMCDecompressor();
		return decompressor.decompress(input);
	}
	
	@Override
	public long getDehydrateSize(Document object) {
		// TODO Auto-generated method stub
		return 0;
	}
}
