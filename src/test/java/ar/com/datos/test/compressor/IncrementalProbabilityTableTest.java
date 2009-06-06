package ar.com.datos.test.compressor;

import java.util.Iterator;

import ar.com.datos.compressor.IncrementalProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.util.Tuple;
import junit.framework.TestCase;

public class IncrementalProbabilityTableTest extends TestCase {

	private IncrementalProbabilityTable table;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		table = new IncrementalProbabilityTable(0,(int)1 <<16);
	}
	/**
	 * Verifico que comience con el primer caracter y que 
	 * todos los subsiguientes tengan la misma probabilidad asignada
	 * @throws Exception
	 */
	public void testEquallyProbability() throws Exception {
		Iterator<Tuple<SuperChar, Double>> iterator = table.iterator();
		Tuple<SuperChar, Double> firstValue = iterator.next();
		assertTrue(firstValue.getFirst().matches(new SimpleSuperChar(0)));
		assertTrue(firstValue.getSecond() > 0.0);
		while(iterator.hasNext()) {
			Tuple<SuperChar, Double> current = iterator.next();
			assertEquals(firstValue.getSecond(), current.getSecond());
		}
	}
}
