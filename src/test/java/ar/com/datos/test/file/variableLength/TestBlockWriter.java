package ar.com.datos.test.file.variableLength;

import java.util.ArrayList;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.jmock.lib.action.CustomAction;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.address.BlockAddress;
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
		byte[] data = new byte[] {1,2,3,4,5};
		byte[] data2 = new byte[] {6,5,4,3,2,1};
		this.blockWriter.write(data);
		final Collection<ArrayByte> block = new ArrayList<ArrayByte>();
		block.add(new SimpleArrayByte(data));
		block.add(new SimpleArrayByte(new byte[510 - 5]));
		block.add(new SimpleArrayByte(new byte[]{0,1}));
		
		checking(new Expectations(){{
			one(mockFileBlock).writeBlock(0L, block);
			will(writeAction);
		}});
		BlockAddress<Long, Short> address = this.blockWriter.closeEntity();
		assertEquals(0, address.getObjectNumber().intValue());
		assertEquals(0L, address.getBlockNumber().longValue());
		
		this.blockWriter.write(data2);
		block.clear();
		block.add(new SimpleArrayByte(data));
		block.add(new SimpleArrayByte(data2));
		block.add(new SimpleArrayByte(new byte[510 - 11]));
		block.add(new SimpleArrayByte(new byte[]{0,2}));
		checking(new Expectations(){{
			one(mockFileBlock).writeBlock(0L, block);
			will(writeAction);
		}});
		BlockAddress<Long, Short> address2 = this.blockWriter.closeEntity();
		assertEquals(1, address2.getObjectNumber().intValue());
		assertEquals(0L, address2.getBlockNumber().longValue());
		this.blockWriter.flush();
	}
	private CustomAction getWriteAction() {
		return new CustomAction("writeBlock1") {
			@Override
			public Object invoke(Invocation invocation) throws Throwable {
				Long numeroTarget = (Long) invocation.getParameter(0);
				if (numeroTarget > cantidadDeBloquesEnFileMock)
					cantidadDeBloquesEnFileMock = numeroTarget;
				return null;
			}
		};
	}
}
