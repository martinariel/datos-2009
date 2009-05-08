package ar.com.datos.documentlibrary;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

public class LibraryCounterDataSerializer implements Serializer<LibraryData> {

	private LongSerializer serializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	@Override
	public void dehydrate(OutputBuffer output, LibraryData object) throws SerializerException {
		serializer.dehydrate(output, ((LibraryCounterData)object).getCurrentCount());
	}

	@Override
	public long getDehydrateSize(LibraryData object) {
		return serializer.getDehydrateSize(((LibraryCounterData)object).getCurrentCount());
	}

	@Override
	public LibraryData hydrate(InputBuffer input) throws SerializerException {
		return new LibraryCounterData(serializer.hydrate(input));
	}

}
