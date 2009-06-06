package ar.com.datos.compressor.ppmc;

import java.util.Iterator;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

public class PPMCSerializer implements Serializer<Document> {

	private PPMCCompressor ppmccompressor;
	
	public PPMCSerializer(PPMCCompressor ppmccompressor) {
		this.ppmccompressor = ppmccompressor;
	}

	@Override
	public void dehydrate(OutputBuffer output, Document document)
			throws SerializerException {
		Iterator<Character> it = document.getCharacterIterator();
		while (it.hasNext()){
			Character ch = it.next();
			this.ppmccompressor.compress(new SimpleSuperChar(ch));
		}
	}

	@Override
	public long getDehydrateSize(Document object) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Document hydrate(InputBuffer input) throws SerializerException {
		//this.ppmccompressor
		return null;
	}

	/**
	 * Encuentra el contexto adecuado para un determinado caracter
	 * @param ch
	 * @return
	 */
	private Context findContextFor(Character ch) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Construye la estructura de contextos en base al orden del PPMC.
	 * Debe llamarse al crear un {@link PPMCSerializer} y dejar todo listo para que se 
	 * pueda comprimir/descomprimir. 
	 */
	private void constructContexts() {
		
		
	}	
}
