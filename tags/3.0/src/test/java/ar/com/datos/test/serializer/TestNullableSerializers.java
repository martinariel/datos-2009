package ar.com.datos.test.serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.ArrayInputBuffer;
import ar.com.datos.buffer.variableLength.ArrayOutputBuffer;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

public class TestNullableSerializers extends TestCase {
	public void testCollectionSerializer() throws Exception {
		ArrayByte arrayByte = new SimpleArrayByte(new byte[500]);
		ArrayOutputBuffer output = new ArrayOutputBuffer(arrayByte);
		CollectionSerializer<String> cs = new CollectionSerializer<String>(new StringSerializerDelimiter());
		List<String> caso1 = Arrays.asList("hola", "buenos", "dias");
		List<String> caso3 = Arrays.asList("chau", "malas", "noches");
		cs.dehydrate(output, caso1);
		cs.dehydrateNull(output);
		cs.dehydrate(output, caso3);
		
		ArrayInputBuffer ai = new ArrayInputBuffer(arrayByte);
		assertEquals(caso1, cs.hydrate(ai));
		assertEquals(new ArrayList<String>(), cs.hydrate(ai));
		assertEquals(caso3, cs.hydrate(ai));
	}
}
