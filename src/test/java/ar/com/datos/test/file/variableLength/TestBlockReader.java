package ar.com.datos.test.file.variableLength;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

import ar.com.datos.file.BlockFile;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.serializer.PrimitiveTypeSerializer;

public class TestBlockReader extends MockObjectTestCase {

	private static final int BLOCK_SIZE = 512;
	private BlockFile mockFileBlock;
	private BlockReader blockReader;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mockFileBlock = this.mock(BlockFile.class);
		checking(new Expectations(){{
			allowing(mockFileBlock).getBlockSize();
			will(returnValue(BLOCK_SIZE));
			allowing(mockFileBlock).getTotalBlocks();
			will(returnValue(1020L));
		}});
		this.blockReader = new BlockReader(this.mockFileBlock);
	}
	/**
	 * Voy a tener un bloque con n registros, que por tanto se considera como bloque
	 * de cabecera. Y que si se piden los datos los va a devolver. Y si se le pregunta la cantidad
	 * de registros que hay indica n.
	 * @throws Exception
	 */
	public void testSimpleRead() throws Exception {
		final byte[] bloque5 = new byte[BLOCK_SIZE];
		for (Integer i = 0; i < bloque5.length; i++) bloque5[i] = i.byteValue();
		byte[] cantRegistros = PrimitiveTypeSerializer.toByte((short)5);
		byte[] bloque5Data = new byte[BLOCK_SIZE - cantRegistros.length];
		byte[] bloque5DataExp = new byte[BLOCK_SIZE - cantRegistros.length];
		System.arraycopy(cantRegistros, 0, bloque5, bloque5.length - cantRegistros.length, cantRegistros.length);
		System.arraycopy(bloque5, 0, bloque5DataExp, 0, bloque5DataExp.length);
		checking(new Expectations(){{
			one(mockFileBlock).readBlock(5L);
			will(returnValue(bloque5));
		}});
		assertEquals(BLOCK_SIZE - cantRegistros.length, this.blockReader.getOneBlockDataSize().intValue());
		this.blockReader.readBlock(5L);
		assertTrue(this.blockReader.isBlockHead());
		assertEquals(5, this.blockReader.getRegistryCount().intValue());
		this.blockReader.getData().read(bloque5Data);
		assertTrue(Arrays.equals(bloque5DataExp, bloque5Data));
		
	}
	/**
	 * Voy a tener un bloque con la cabecera de un registro que se encuentra en varios bloques
	 * 
	 * @throws Exception
	 */
	public void testHeadRead() throws Exception {
		final byte[] bloque5 = new byte[BLOCK_SIZE];
		byte[] cantRegistros = PrimitiveTypeSerializer.toByte((short)-1);
		System.arraycopy(cantRegistros, 0, bloque5, bloque5.length - cantRegistros.length, cantRegistros.length);
		checking(new Expectations(){{
			one(mockFileBlock).readBlock(5L);
			will(returnValue(bloque5));
		}});
		this.blockReader.readBlock(5L);
		assertTrue(this.blockReader.isBlockHead());
		assertEquals(1, this.blockReader.getRegistryCount().intValue());
	}
}
