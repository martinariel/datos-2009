package ar.com.datos.persistencia.variableLength;

import ar.com.datos.file.BlockFile;

public class SimpleBlockData {
	private Long blockNumber;
	private byte[] block;
	private BlockFile blockFile;
	public SimpleBlockData(BlockFile blockFile, Long blockNumber) {
		setBlockFile(blockFile);
		setBlockNumber(blockNumber);
	}
	public Long getBlockNumber() {
		return blockNumber;
	}
	protected void setBlockNumber(Long blockNumber) {
		this.blockNumber = blockNumber;
	}
	protected byte[] getBlock() {
		if (block == null) {
			block = getBlockFile().readBlock(getBlockNumber());
		}
		return block;
	}
	protected void setBlock(byte[] block) {
		this.block = block;
	}
	protected BlockFile getBlockFile() {
		return blockFile;
	}
	protected void setBlockFile(BlockFile blockFile) {
		this.blockFile = blockFile;
	}
	
}
