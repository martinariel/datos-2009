package ar.com.datos.test.file.variableLength;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import ar.com.datos.file.BlockFile;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.VariableLengthFileManager;

public class TestVariableLength extends MockObjectTestCase {
	private Integer blockSize = 512;
	private Serializer2 serializerMock;
	private BlockFile fileMock;
	private DynamicAccesor unDynamicAccesor;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serializerMock = this.mock(Serializer2.class);
		fileMock = this.mock(BlockFile.class);
		unDynamicAccesor = new VariableLengthFileManager("nombreArchivo",blockSize, serializerMock) {
			@Override
			public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
				return fileMock;
			}
		};
	}
	public void testCreacion() throws Exception {
		checking(new Expectations(){{
			
		}});
		unDynamicAccesor.addEntity(null);
	}
}
