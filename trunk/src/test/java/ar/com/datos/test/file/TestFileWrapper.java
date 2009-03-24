package ar.com.datos.test.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Random;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;
import ar.com.datos.file.exception.InvalidBlockException;

public class TestFileWrapper extends TestCase {

	private static final Integer blockSize = 16;
	private Mockery context;
	private File fileMock;
	private Boolean writeEnabled = true;
	private Boolean readEnabled = true;
	private Long fileSize = 0L;
	private RandomAccessFile accessMock;
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
		context.setImposteriser(ClassImposteriser.INSTANCE);
		fileMock = context.mock(File.class); 
		accessMock = context.mock(RandomAccessFile.class); 
		super.setUp();
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		context.assertIsSatisfied();
	}
	/**
	 * Creo un blockFile en el cual voy a intentar grabar 2 veces. La primera con
	 * un bloque de longitud inválida.
	 * La segunda con un bloque válido, en esta espero que utilice el accesMock 
	 * para posicionarse al final del archivo y luego grabe la tira de bytes
	 * @throws Exception
	 */
	public void testAgregadoBloque() throws Exception {
		fileSize = blockSize * new Random().nextLong();
		context.checking(new Expectations() {{
			one(fileMock).length();
			will(returnValue(fileSize));
			one(accessMock).seek(fileSize);
			one(accessMock).write(with(any(byte[].class)));
		}});
		BlockFile bf = getNewBlockFile();
		try {
			bf.appendBlock(new byte[2]);
			fail("Agrego un bloque de tamaño inválido");
		} catch (InvalidBlockException e) {
			assertEquals("Se esperaba un bloque de tamaño " + blockSize, e.getMessage());
			bf.appendBlock(new byte[blockSize]);
		}
	}
	public void testLecturaDeBloque() throws Exception {
		Long cantidadDeBloques = Math.abs(new Random().nextLong());
		fileSize = blockSize * cantidadDeBloques;
		Long otroNum = Math.abs(new Random().nextLong());
		final Long bloqueALeer = otroNum > cantidadDeBloques? cantidadDeBloques : otroNum;
		context.checking(new Expectations() {{
			one(accessMock).seek(bloqueALeer);
			one(accessMock).read(with(any(byte[].class)));
			will(returnValue(blockSize));
		}});
		BlockFile bf = getNewBlockFile();
		byte[] bloqueLeido = bf.readBlock(bloqueALeer);
		assertNotNull(bloqueLeido);
		assertEquals(blockSize.longValue(), bloqueLeido.length);
		
	}
	private BlockFile getNewBlockFile() throws Exception {
		context.checking(new Expectations(){{
			
			one(fileMock).exists();
			will(returnValue(false));
			one(fileMock).createNewFile();
			will(returnValue(true));
			one(fileMock).canRead();
			will(returnValue(readEnabled));
			one(fileMock).canWrite();
			will(returnValue(writeEnabled));
			one(fileMock).length();
			will(returnValue(fileSize));
			
		}});
		return new BlockFileImpl("direccion",blockSize){ 
			@Override
			protected File constructFile(String string) {
				return fileMock;
			}
			@Override
			protected RandomAccessFile constructAccesor(File archivo) {
				return accessMock;
			}
		};
	}
}
