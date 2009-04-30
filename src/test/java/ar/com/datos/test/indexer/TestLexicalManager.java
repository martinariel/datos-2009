package ar.com.datos.test.indexer;

import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.indexer.LexicalData;
import ar.com.datos.indexer.LexicalManager;
import junit.framework.TestCase;

public class TestLexicalManager extends TestCase {
	public void testSimple() throws Exception {
		LexicalManager lm = new LexicalManager("Dale!") {
			@Override
			protected StraightVariableLengthFile<LexicalData> constructFile(String fileName) {
				return new StraightVariableLengthFile<LexicalData>(getSerializer());
			}
		};
		assertEquals(0L, lm.getNumberOfTerms().longValue());
		OffsetAddress addressPepe = lm.add("pepe");
		OffsetAddress addressJorge = lm.add("Jorgelín");
		OffsetAddress addressJose = lm.add("jose...");
		assertEquals(3L, lm.getNumberOfTerms().longValue());
		assertEquals("jose...", lm.get(addressJose));
		assertEquals("Jorgelín", lm.get(addressJorge));
		assertEquals("pepe", lm.get(addressPepe));
		assertEquals(3L, lm.getNumberOfTerms().longValue());
		assertEquals("Jorgelín", lm.get(addressJorge));
	}

}
