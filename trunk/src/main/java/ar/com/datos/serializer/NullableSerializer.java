package ar.com.datos.serializer;

import ar.com.datos.buffer.OutputBuffer;

public interface NullableSerializer<T> extends Serializer<T> {

	void dehydrateNull(OutputBuffer buffer);

}
