package ar.com.datos.test.file.variableLength;

import junit.framework.TestCase;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.persistencia.variableLength.BlockWriter;

public class TestBlockWriterWithStub extends TestCase {

	private static final int BLOCK_SIZE = 512;
	private BlockFileStub mockFileBlock;
	private BlockWriter blockWriter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mockFileBlock = new BlockFileStub(BLOCK_SIZE);
		this.blockWriter = new BlockWriter(this.mockFileBlock);
	}
	/**
	 * Voy a grabar primero una entidad de varios bloques y a
	 * ella preguntarle verificar los datos en que la escribió.
	 * Luego voy a grabar otro y verificar que los datos también sean correctos
	 * 
	 * @throws Exception
	 */
	public void testWrite() throws Exception {
		this.blockWriter.write(new byte[this.blockWriter.getMultipleBlockDataSize() * 2]);
		BlockAddress<Long, Short> address = this.blockWriter.closeEntity();
		assertEquals(0, address.getObjectNumber().intValue());
		assertEquals(0, address.getBlockNumber().intValue());
		this.blockWriter.write(new byte[this.blockWriter.getSimpleDataSize()]);
		address = this.blockWriter.closeEntity();
		assertEquals(2, address.getBlockNumber().intValue());
		assertEquals(0, address.getObjectNumber().intValue());
	}
}
