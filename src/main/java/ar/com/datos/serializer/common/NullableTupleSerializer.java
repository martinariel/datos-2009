package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.util.Tuple;

public class NullableTupleSerializer<K,E> extends TupleSerializer<K, E> implements
		NullableSerializer<Tuple<K,E>> {

	private BooleanSerializer nullMarkerSerializer = SerializerCache.getInstance().getSerializer(BooleanSerializer.class);
	public NullableTupleSerializer(Serializer<K> serializerFirst,
			Serializer<E> serializerSecond) {
		super(serializerFirst, serializerSecond);
	}

	public void dehydrateNull(OutputBuffer buffer) {
		this.dehydrate(buffer, null);
	}

	@Override
	public void dehydrate(OutputBuffer output, Tuple<K, E> object) {
		boolean isNull = object == null;
		nullMarkerSerializer.dehydrate(output, isNull);
		if (!isNull) {
			super.dehydrate(output, object);
		} else {
			System.out.println("pero que pedazo...");
		}
	}
	@Override
	public Tuple<K, E> hydrate(InputBuffer input) {
		Boolean isNull = nullMarkerSerializer.hydrate(input);
		return isNull? null : super.hydrate(input);
	}
	@Override
	public long getDehydrateSize(Tuple<K, E> object) {
		long delta = nullMarkerSerializer.getDehydrateSize(true);
		return object== null? delta : super.getDehydrateSize(object) + delta;
	}
}
