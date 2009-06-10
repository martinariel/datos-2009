package ar.com.datos.test.compressor.lzp;

import junit.framework.TestCase;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.lzp.LzpSerializer;
import ar.com.datos.documentlibrary.Document;
import ar.com.datos.documentlibrary.MemoryDocument;
import ar.com.datos.test.serializer.mock.OutputBufferTest;

public class TestLzp extends TestCase {

	
	public void testLzp() {
		OutputBufferTest o = new OutputBufferTest();
		MemoryDocument document = new MemoryDocument();
		document.addLine("hola");
		LzpSerializer lzpSerializer = new LzpSerializer();
		lzpSerializer.dehydrate(o, document);
		InputBuffer i = o.getAsInputBuffer();
		Document hydratedDocument = lzpSerializer.hydrate(i);
		assertEquals(document, hydratedDocument);
	}
}
