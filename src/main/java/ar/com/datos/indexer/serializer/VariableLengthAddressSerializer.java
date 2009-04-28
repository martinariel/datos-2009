package ar.com.datos.indexer.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;

public class VariableLengthAddressSerializer implements Serializer<BlockAddress<Long, Short>> {

	private LongSerializer longserializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	private ShortSerializer shortserializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	@Override
	public void dehydrate(OutputBuffer output, BlockAddress<Long, Short> object) {
		longserializer.dehydrate(output, object.getBlockNumber());
		shortserializer.dehydrate(output, object.getObjectNumber());
	}

	@Override
	public long getDehydrateSize(BlockAddress<Long, Short> object) {
		if (object == null) return longserializer.getDehydrateSize(null) + shortserializer.getDehydrateSize(null);
		return longserializer.getDehydrateSize(object.getBlockNumber()) + shortserializer.getDehydrateSize(object.getObjectNumber());
	}

	@Override
	public BlockAddress<Long, Short> hydrate(InputBuffer input) {
		Long blockNumber = longserializer.hydrate(input);
		Short objectNumber = shortserializer.hydrate(input);
		return new VariableLengthAddress(blockNumber, objectNumber);
	}

}
