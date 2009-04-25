package ar.com.datos.file;

import ar.com.datos.file.address.BlockAddress;

public interface BlockAccessor<A extends BlockAddress<Long, Short>, T> extends DynamicAccesor<A, T> {

	/**
	 * Replaces entity stored in <code>address</code> with <code>object</code>
	 * @param address
	 * @param object
	 * @return
	 */
	public A updateEntity(A address, T object);

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
