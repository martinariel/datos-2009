package ar.com.datos.file.variableLength.address;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;

public class OffsetAddressSerializer implements Serializer<OffsetAddress> {

	private static LongSerializer longSerializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	@Override
	public void dehydrate(OutputBuffer output, OffsetAddress object) {
		longSerializer.dehydrate(output, object.getOffset());
	}

	@Override
	public long getDehydrateSize(OffsetAddress object) {
		if (object == null) return longSerializer.getDehydrateSize(null);
		return longSerializer.getDehydrateSize(object.getOffset());
	}

	@Override
	public OffsetAddress hydrate(InputBuffer input) {
		return new OffsetAddress(longSerializer.hydrate(input));
	}

}
