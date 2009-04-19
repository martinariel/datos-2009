package ar.com.datos.test.file.variableLength;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.file.BlockFile;

public class BlockFileStub implements BlockFile {

	private List<byte[]> blocks;
	private Integer blockSize;
	@Override
	public void appendBlock(byte[] block) {
		verifySize(block);
		blocks.add(block);
	}

	@Override
	public void close() {
	}

	@Override
	public Integer getBlockSize() {
		return this.blockSize;
	}

	@Override
	public Long getTotalBlocks() {
		return new Long(blocks.size());
	}

	@Override
	public byte[] readBlock(Long blockNumber) {
		return this.blocks.get(blockNumber.intValue());
	}

	@Override
	public void writeBlock(Long blockNumber, byte[] block) {
		verifySize(block);
		verifyNotExceed(blockNumber);
		this.blocks.add(blockNumber.intValue(), block);
	}

	private void verifyNotExceed(Long blockNumber) {
		if (blockNumber > this.blocks.size()) throw new InvalidParameterException("Wow! Estás meando fuera del recipiente!");
	}
	private void verifySize(byte[] block) {
		if (block.length != getBlockSize()) throw new InvalidParameterException("Wow! tamaño incorrecto");
	}

	private void verifySize(Collection<ArrayByte> partes) {
		Integer total = 0;
		for (ArrayByte ab: partes) total += ab.getLength();
		if (total != getBlockSize()) throw new InvalidParameterException("Wow! tamaño incorrecto");
	}

	@Override
	public void writeBlock(Long blockNumber, Collection<ArrayByte> partes) {
		verifySize(partes);
		byte[] block = new byte[this.blockSize];
		int index = 0;
		for (ArrayByte ab: partes) {
			for (Integer j = 0; j < ab.getLength(); j++) {
				block[index] = ab.getByte(j);
				index++;
			}
		}
	}

	public void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}

}
