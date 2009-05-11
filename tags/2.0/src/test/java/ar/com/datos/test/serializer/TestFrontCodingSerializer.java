package ar.com.datos.test.serializer;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.indexer.serializer.FrontCodingSerializer;
import ar.com.datos.test.serializer.mock.OutputBufferTest;

public class TestFrontCodingSerializer extends TestCase {
	private FrontCodingSerializer frontCodingSerializer;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.frontCodingSerializer = new FrontCodingSerializer();
	}
	
	public void testFrontCodingAlgorithm(){
		List<String> words = new LinkedList<String>();
		words.add("codazo");
		words.add("codearse");
		words.add("codera");
		words.add("codicia");
		words.add("codiciar");
		words.add("codiciosa");
		words.add("codicioso");
		words.add("codificar");
		words.add("codigo");
		words.add("sinCoincidenciaAnteriores");
		
		OutputBufferTest o = new OutputBufferTest();
		this.frontCodingSerializer.dehydrate(o, words);
		
		InputBuffer i = o.getAsInputBuffer();
		List<String> hydratedWords = this.frontCodingSerializer.hydrate(i);
		
		assertEquals(words, hydratedWords);
		assertEquals(150, this.frontCodingSerializer.getDehydrateSize(words));
	}
}
