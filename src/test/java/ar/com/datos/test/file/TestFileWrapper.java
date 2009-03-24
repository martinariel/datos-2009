package ar.com.datos.test.file;

import java.io.File;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;

public class TestFileWrapper extends TestCase {

	private static final Integer blockSize = 512;
	private Mockery context;
	private File fileMock;
	private Boolean writeEnabled = true;
	private Boolean readEnabled = true;
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
		context.setImposteriser(ClassImposteriser.INSTANCE);
		fileMock = context.mock(File.class); 
		super.setUp();
	}
	public void testCreacion() throws Exception {
		context.checking(new Expectations(){{
			one(fileMock).canRead();
			will(returnValue(readEnabled));
			one(fileMock).canWrite();
			will(returnValue(writeEnabled));
			one(fileMock).getTotalSpace();
			will(returnValue(0L));
			one(fileMock).exists();
			will(returnValue(false));
			one(fileMock).createNewFile();
			will(returnValue(true));
		}});
		getNewBlockFile();
	}
	private BlockFile getNewBlockFile() {
		return new BlockFileImpl("direccion",blockSize){ 
			@Override
			protected File constructFile(String string) {
				return fileMock;
			}
		};
	}
}
