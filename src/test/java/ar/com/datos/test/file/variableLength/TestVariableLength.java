package ar.com.datos.test.file.variableLength;

import org.jmock.integration.junit3.MockObjectTestCase;

import ar.com.datos.file.SequentialAccesor;
import ar.com.datos.file.variableLength.VariableLengthFileManager;

public class TestVariableLength extends MockObjectTestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}
	public void testCreacion() throws Exception {
		SequentialAccesor unSequentialAccesor = new VariableLengthFileManager();
	}
}
