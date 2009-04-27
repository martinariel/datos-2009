package ar.com.datos.indexer.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.TupleSerializer;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.KeyCount;
/**
 * Serializador de una tupla de tipo {@link KeyCount}
 * @author jbarreneche
 *
 * @param <T>
 */
public class KeyCountSerializer<T> implements Serializer<KeyCount<T>> {

	private TupleSerializer<T, Integer> tupleSerializer;
	/**
	 * Constructor que recibe el serializador para el tipo de la clave
	 * de la tupla
	 * @param dataSerializer
	 */
	public KeyCountSerializer(Serializer<T> dataSerializer) {
		// Se genera un TupleSerializer que reemplaza la construcción de una tupla normal
		// por una tupla de tipo KeyCount
		this.tupleSerializer = new TupleSerializer<T, Integer>(dataSerializer, SerializerCache.getInstance().getSerializer(IntegerSerializer.class)) {
			@Override
			protected Tuple<T,Integer> constructTuple(T hydrateFirst, Integer hydrateSecond) {
				return new KeyCount<T>(hydrateFirst, hydrateSecond);
			};
		};
	}
	@Override
	public void dehydrate(OutputBuffer output, KeyCount<T> object) {
		this.tupleSerializer.dehydrate(output, object);
	}
	@Override
	public long getDehydrateSize(KeyCount<T> object) {
		return this.tupleSerializer.getDehydrateSize(object);
	}
	@Override
	public KeyCount<T> hydrate(InputBuffer input) {
		return (KeyCount<T>) this.tupleSerializer.hydrate(input);
	}

}
