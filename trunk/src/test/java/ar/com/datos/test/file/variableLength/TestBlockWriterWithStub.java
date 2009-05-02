package ar.com.datos.test.file.variableLength;

import java.util.List;

import junit.framework.TestCase;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.persistencia.variableLength.BlockMetaData;
import ar.com.datos.persistencia.variableLength.BlockReader;
import ar.com.datos.persistencia.variableLength.BlockWriter;

public class TestBlockWriterWithStub extends TestCase {

	private static final int BLOCK_SIZE = 512;
	private BlockFileStub stubFileBlock;
	private BlockWriter blockWriter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.stubFileBlock = new BlockFileStub(BLOCK_SIZE);
		this.blockWriter = new BlockWriter(this.stubFileBlock);
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
		this.blockWriter.flush();
		assertWrittens(0L,1L,2L);
	}
	/**
	 * Voy a grabar primero una entidad de varios bloques y luego sobreescribir dicha entidad
	 * Esto debería dejar vinculados los bloques y regrabar.
	 * Luego voy a grabar otro y verificar que los datos también sean correctos
	 * 
	 * @throws Exception
	 */
	public void testWriteReWrite() throws Exception {
		this.blockWriter.write(new byte[this.blockWriter.getMultipleBlockDataSize() * 2]);
		BlockAddress<Long, Short> address = this.blockWriter.closeEntity();
		assertEquals(0, address.getObjectNumber().intValue());
		assertEquals(0, address.getBlockNumber().intValue());
		BlockReader blockReader = new BlockReader(this.stubFileBlock);
		blockReader.readBlock(0L);
		for (BlockMetaData bmd :blockReader.getMetaData()) {
			blockWriter.addAvailableBlock(bmd.getBlockNumber());
		}
		blockWriter.write(new byte[this.blockWriter.getMultipleBlockDataSize()]);
		address = this.blockWriter.closeEntity();
		blockWriter.flush();
		assertWrittens(0L,1L,0L);
		blockReader.readBlock(0L);
		List<BlockMetaData> metaData = blockReader.getMetaData();
		assertEquals(0L, metaData.get(0).getBlockNumber().longValue());
		assertEquals(1L, metaData.get(1).getBlockNumber().longValue());
	}
	private void assertWrittens(Long...l) {
		List<Long> writtenBlocks = this.stubFileBlock.getWrittenBlocks();
		if (writtenBlocks.size() > l.length) fail("Se escribieron mas bloques de los esperados" + writtenBlocks.toString());
		if (writtenBlocks.size() < l.length) fail("Se escribieron menos bloques de los esperados" + writtenBlocks.toString());
		
		for (Integer i = 0; i < l.length; i++) {
			assertEquals(l[i], writtenBlocks.get(i));
		}
	}
}
