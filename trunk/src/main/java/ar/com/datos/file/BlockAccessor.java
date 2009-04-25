package ar.com.datos.file;

import ar.com.datos.file.address.BlockAddress;

public interface BlockAccessor<A extends BlockAddress<Long, Short>, T> extends DynamicAccesor<A, T> {

	/**
	 * @param address
	 * @return cantidad actual de bloques que esta usando el <code>address</code> 
	 */
	public Short getAmountOfBlocksFor(BlockAddress<Long, Short> address);

	/**
	 * 
	 * @param numberOfChainedBlocks
	 * @return maximum data size 
	 */
	public Integer getDataSizeFor(Short numberOfChainedBlocks);

}
