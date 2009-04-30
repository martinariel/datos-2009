package ar.com.datos.indexer.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.indexer.LexicalData;
import ar.com.datos.indexer.LexicalTermData;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.serializer.exception.SerializerException;

public class LexicalTermSerializer implements Serializer<LexicalData>{

	private static StringSerializerDelimiter stringSerializer = new StringSerializerDelimiter(); 
	@Override
	public void dehydrate(OutputBuffer output, LexicalData object) throws SerializerException {
		stringSerializer.dehydrate(output, object.toString());
	}

	@Override
	public long getDehydrateSize(LexicalData object) {
		return stringSerializer.getDehydrateSize(object.toString());
	}

	@Override
	public LexicalData hydrate(InputBuffer input) throws SerializerException {
		return new LexicalTermData(stringSerializer.hydrate(input));
	}

}
