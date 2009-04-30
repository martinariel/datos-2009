package ar.com.datos.indexer.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.indexer.LexicalCounterData;
import ar.com.datos.indexer.LexicalData;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

public class LexicalCounterSerializer implements Serializer<LexicalData>{
	protected static final LongSerializer serializadorCantidad = SerializerCache.getInstance().getSerializer(LongSerializer.class);

	@Override
	public void dehydrate(OutputBuffer output, LexicalData object) throws SerializerException {
		serializadorCantidad.dehydrate(output, ((LexicalCounterData)object).getCurrentCount());
	}

	@Override
	public long getDehydrateSize(LexicalData object) {
		return serializadorCantidad.getDehydrateSize(((LexicalCounterData)object).getCurrentCount());
	}

	@Override
	public LexicalData hydrate(InputBuffer input) throws SerializerException {
		return new LexicalCounterData(serializadorCantidad.hydrate(input));
	}

}
