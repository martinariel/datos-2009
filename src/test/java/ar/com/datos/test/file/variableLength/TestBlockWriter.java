package ar.com.datos.test.file.variableLength;

import java.util.ArrayList;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.EntityOutputBuffer;
import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.persistencia.variableLength.BlockWriter;

public class TestBlockWriter extends MockObjectTestCase {

	private static final int BLOCK_SIZE = 512;
	private BlockFile mockFileBlock;
	private BlockWriter blockWriter;
	private Long cantidadDeBloquesEnFileMock;
	private CustomAction writeAction; 

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.mockFileBlock = this.mock(BlockFile.class);
		this.cantidadDeBloquesEnFileMock = 0L;
		this.writeAction = getWriteAction();
		checking(new Expectations(){{
			allowing(mockFileBlock).getBlockSize();
			will(returnValue(BLOCK_SIZE));
			allowing(mockFileBlock).getTotalBlocks();
			will(new CustomAction("totalBlocks") {
				@Override
				public Object invoke(Invocation invocation) throws Throwable {
					return cantidadDeBloquesEnFileMock;
				}
			});
		}});
		this.blockWriter = new BlockWriter(this.mockFileBlock);
	}
	public void testWrite() throws Exception {
		EntityOutputBuffer outputBuffer = this.blockWriter.getOutputBuffer();
		assertNotNull(outputBuffer);
		byte[] data = new byte[] {1,2,3,4,5};
		byte[] data2 = new byte[] {6,5,4,3,2,1};
		outputBuffer.write(data);
		outputBuffer.closeEntity();
		final Collection<ArrayByte> block = new ArrayList<ArrayByte>();
		block.add(new SimpleArrayByte(data));
		block.add(new SimpleArrayByte(new byte[510 - 5]));
		block.add(new SimpleArrayByte(new byte[]{0,1}));
		
		checking(new Expectations(){{
			one(mockFileBlock).writeBlock(cantidadDeBloquesEnFileMock, block);
			will(writeAction);
		}});
		assertEquals(1, this.blockWriter.getCurrentWrittingEntityNumber().intValue());
		assertEquals(0L, this.blockWriter.getCurrentWrittingBlock().longValue());
		outputBuffer.write(data2);
		outputBuffer.closeEntity();
		block.clear();
		block.add(new SimpleArrayByte(data));
		block.add(new SimpleArrayByte(data2));
		block.add(new SimpleArrayByte(new byte[510 - 11]));
		block.add(new SimpleArrayByte(new byte[]{0,2}));
		checking(new Expectations(){{
			one(mockFileBlock).writeBlock(0L, block);
			will(writeAction);
		}});
		assertEquals(2, this.blockWriter.getCurrentWrittingEntityNumber().intValue());
		assertEquals(0L, this.blockWriter.getCurrentWrittingBlock().longValue());
		this.blockWriter.flush();
	}
	private CustomAction getWriteAction() {
		return new CustomAction("writeBlock1") {
			@Override
			public Object invoke(Invocation invocation) throws Throwable {
				cantidadDeBloquesEnFileMock += 1;
				return null;
			}
		};
	}
}
