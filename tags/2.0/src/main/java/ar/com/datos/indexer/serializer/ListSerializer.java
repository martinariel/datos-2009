package ar.com.datos.indexer.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.NumberSerializer;
import ar.com.datos.serializer.exception.SerializerException;

public class ListSerializer<T> implements Serializer<List<T>> {

	private CollectionSerializer<T> collectionSerializer;

	@SuppressWarnings("unchecked")
	public ListSerializer(Serializer<T> baseSerializer, NumberSerializer cardinalitySerializer) {
		this.collectionSerializer = new CollectionSerializer<T>(baseSerializer, cardinalitySerializer); 
	}

	public ListSerializer(Serializer<T> baseSerializer) {
		this.collectionSerializer = new CollectionSerializer<T>(baseSerializer); 
	}

	@Override
	public void dehydrate(OutputBuffer output, List<T> object) throws SerializerException {
		this.collectionSerializer.dehydrate(output, object);
	}

	@Override
	public long getDehydrateSize(List<T> object) {
		return this.collectionSerializer.getDehydrateSize(object);
	}

	@Override
	public List<T> hydrate(InputBuffer input) throws SerializerException {
		Collection<T> hydrate = this.collectionSerializer.hydrate(input);
		return new ArrayList<T>(hydrate);
	}

}
