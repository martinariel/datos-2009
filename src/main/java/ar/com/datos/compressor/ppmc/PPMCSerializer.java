package ar.com.datos.compressor.ppmc;

import java.util.Iterator;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.ppmc.context.Context;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

public class PPMCSerializer implements Serializer<Document> {

	private PPMCCompressor compressor;
	
	public PPMCSerializer() {
		this.compressor = new PPMCCompressor();
	}

	@Override
	public void dehydrate(OutputBuffer output, Document document)
			throws SerializerException {
		
		// creo un nuevo aritmetico para comprimir con el compresor PPMC
		this.compressor.setArithmeticCompressor(new ArithmeticEmissor(output));
		
		// itero sobre los caracteres del documento a comprimir
		Iterator<Character> it = document.getCharacterIterator();
		while (it.hasNext()){
			Character ch = it.next();
			// comprimo con el compresor PPMC
			this.compressor.compress(new SimpleSuperChar(ch));
		}
	}

	@Override
	public Document hydrate(InputBuffer input) throws SerializerException {
		//this.ppmccompressor.decompress()
		//ArithmeticInterpreter arithmetic = new ArithmeticInterpreter(input);
		//arithmetic.
		return null;
	}
	
	@Override
	public long getDehydrateSize(Document object) {
		// TODO Auto-generated method stub
		return 0;
	}
}
