package ar.com.datos.serializer.common;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.util.Tuple;

public class TupleSerializer<K, V> implements Serializer<Tuple<K, V>> {

	private Serializer<K> serializerFirst;
	private Serializer<V> serializerSecond;
	
	public TupleSerializer(Serializer<K> serializerFirst, Serializer<V> serializerSecond) {
		super();
		this.serializerFirst = serializerFirst;
		this.serializerSecond = serializerSecond;
	}

	@Override
	public void dehydrate(OutputBuffer output, Tuple<K, V> object) {
		this.serializerFirst.dehydrate(output, object.getFirst());
		this.serializerSecond.dehydrate(output, object.getSecond());
	}

	@Override
	public long getDehydrateSize(Tuple<K, V> object) {
		if (object == null) return this.serializerFirst.getDehydrateSize(null) + this.serializerSecond.getDehydrateSize(null);
		return this.serializerFirst.getDehydrateSize(object.getFirst()) + 
			   this.serializerSecond.getDehydrateSize(object.getSecond());

	}

	@Override
	public Tuple<K, V> hydrate(InputBuffer input) {
		K hydrateFirst = this.serializerFirst.hydrate(input);
		V hydrateSecond = this.serializerSecond.hydrate(input);
		return constructTuple(hydrateFirst, hydrateSecond);
	}

	protected Tuple<K, V> constructTuple(K hydrateFirst, V hydrateSecond) {
		return new Tuple<K, V>(hydrateFirst,hydrateSecond);
	}

}
