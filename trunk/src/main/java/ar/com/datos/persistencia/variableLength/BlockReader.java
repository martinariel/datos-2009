package ar.com.datos.persistencia.variableLength;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.file.BlockFile;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;

public class BlockReader {

	private ShortSerializer shortSerializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	private BlockFile blockFile;
	private List<SimpleBlockData> fullBlockData = new ArrayList<SimpleBlockData>();
	private List<BlockMetaData> fullBlockMeta = new ArrayList<BlockMetaData>();

	public BlockReader(BlockFile blockFile) {
		super();
		this.blockFile = blockFile;
	}

	protected BlockFile getBlockFile() {
		return blockFile;
	}

	protected void setBlockFile(BlockFile blockFile) {
		this.blockFile = blockFile;
	}

	public Integer getOneBlockDataSize() {
		return this.getBlockFile().getBlockSize() - getSimpleMetaDataSize();
	}

	private Integer getSimpleMetaDataSize() {
		return new Long(shortSerializer.getDehydrateSize((short)0)).intValue();
	}

	/**
	 * Recupera los datos del bloque y su metadata
	 * @param blockNumber
	 */
	public void readBlock(Long blockNumber) {
		if (!this.fullBlockData.isEmpty() && getCurrentBlockData().getBlockNumber().equals(blockNumber)) return;
		
		clearBlockInformation();
		retrieveBlockDataAndMeta(blockNumber);
	}

	private void retrieveBlockDataAndMeta(Long blockNumber) {
		this.setCurrentBlockData(new SimpleBlockData(this.getBlockFile(),blockNumber));
		this.setBlockMetaData(new BlockMetaData(this.getCurrentBlockData()));
	}

	private void clearBlockInformation() {
		this.fullBlockData.clear();
		this.fullBlockMeta.clear();
	}

	public Boolean isBlockHead() {
		return this.fullBlockMeta.get(0).isHead();
	}

	public Integer getRegistryCount() {
		return getBlockMetaData().getRegistryCount().intValue();
	}

	protected BlockMetaData getBlockMetaData() {
		return this.fullBlockMeta.get(this.fullBlockMeta.size() -1);
	}

	/**
	 * Requiere que el último "goToBlock" se haya posicionado en un bloque
	 * que sea <code>isHead</code>
	 * @return un buffer con todos los datos cargados referidos al bloque actual
	 */
	public InputBuffer getData() {
		retrieveAllInformation();
		SimpleInputBuffer retorno = new SimpleInputBuffer();
		for (Integer i = 0; i < this.fullBlockData.size(); i ++) {
			SimpleBlockData sbd = this.fullBlockData.get(i);
			retorno.append(new SimpleArrayByte(sbd.getBlock()).getLeftSubArray(this.fullBlockMeta.get(i).getDataSize()));
		}
		return retorno;
	}

	private void retrieveAllInformation() {
		if (!this.isBlockHead()) throw new NotHeadException("No se puede recuperar la información de bloques que no son cabecera");
		
		while (!this.getBlockMetaData().isTail()) {
			retrieveBlockDataAndMeta(this.getBlockMetaData().getNextBlockNumber());
		}
	}

	public List<BlockMetaData> getMetaData() {
		retrieveAllInformation();
		return this.fullBlockMeta;
	}
	protected SimpleBlockData getCurrentBlockData() {
		return this.fullBlockData.get(this.fullBlockData.size() -1);
	}

	protected void setCurrentBlockData(SimpleBlockData currentBlockData) {
		this.fullBlockData.add(currentBlockData);
	}

	protected void setBlockMetaData(BlockMetaData blockMetaData) {
		this.fullBlockMeta.add(blockMetaData);
	}

	public Long getNextBlockNumber() {
		retrieveAllInformation();
		Long minimo = this.fullBlockMeta.get(0).getBlockNumber() + 1L;

		SortedSet<Long> valores = new TreeSet<Long>();
		for (BlockMetaData bmd : this.fullBlockMeta) {
			if (minimo <= bmd.getBlockNumber()) valores.add(bmd.getBlockNumber());
		}
		for (Long valor: valores) {
			if (valor > minimo) break;
			minimo += 1;
		}
		BlockReader auxBlockReader = new BlockReader(getBlockFile());
		while (minimo < getBlockFile().getTotalBlocks()) {
			auxBlockReader.readBlock(minimo);
			if (auxBlockReader.isBlockHead()) break;
			minimo += 1;
		}
		
		if (minimo == getBlockFile().getTotalBlocks()) return BlockFile.END_BLOCK;
		
		return minimo;
	}
}
