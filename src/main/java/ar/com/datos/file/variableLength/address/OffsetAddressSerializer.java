package ar.com.datos.file.variableLength.address;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;

public class OffsetAddressSerializer implements NullableSerializer<OffsetAddress> {

	private static LongSerializer longSerializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	@Override
	public void dehydrate(OutputBuffer output, OffsetAddress object) {
		Long offset = object == null? -1L : object.getOffset();
		longSerializer.dehydrate(output, offset);
	}

	@Override
	public long getDehydrateSize(OffsetAddress object) {
		if (object == null) return longSerializer.getDehydrateSize(-1L);
		return longSerializer.getDehydrateSize(object.getOffset());
	}

	@Override
	public OffsetAddress hydrate(InputBuffer input) {
		Long hydrate = longSerializer.hydrate(input);
		return (hydrate.equals(-1L))? null : new OffsetAddress(hydrate);
	}

	@Override
	public void dehydrateNull(OutputBuffer buffer) {
		this.dehydrate(buffer, null);
	}

}
