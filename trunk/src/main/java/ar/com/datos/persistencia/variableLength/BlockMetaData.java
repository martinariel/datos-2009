package ar.com.datos.persistencia.variableLength;

import ar.com.datos.buffer.variableLength.ArrayByte;
import ar.com.datos.buffer.variableLength.ArrayInputBuffer;
import ar.com.datos.buffer.variableLength.SimpleArrayByte;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.ShortSerializer;

public class BlockMetaData {

	private static ShortSerializer firstMetaDataSerializer = SerializerCache.getInstance().getSerializer(ShortSerializer.class);
	private static LongSerializer secondMetaDataSerializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	private Long blockNumber;
	private Boolean head;
	private Boolean tail;
	private Long nextBlockNumber;
	private Short registryCount;
	private Integer metaDataSize;
	private Integer blockSize;

	public BlockMetaData(SimpleBlockData simpleBlockData) {
		ArrayByte data = new SimpleArrayByte(simpleBlockData.getBlock());
		setBlockSize(simpleBlockData.getBlockFile().getBlockSize());
		setBlockNumber(simpleBlockData.getBlockNumber());
		this.metaDataSize = new Long(firstMetaDataSerializer.getDehydrateSize((short)0)).intValue();
		Integer inicioMetaData = data.getLength() - this.metaDataSize;
		this.setRegistryCount(firstMetaDataSerializer.hydrate(new ArrayInputBuffer(data.getRightSubArray(inicioMetaData))));
		setHead(!(this.getRegistryCount().intValue() == 0));
		if (this.getRegistryCount() > 0) {
			completeOneBlockData();
		} else {
			completeMultipleBlockData(data.getLeftSubArray(inicioMetaData));
		}
	}
	private void completeMultipleBlockData(ArrayByte data) {
		Integer inicioMetaData = data.getLength() - new Long(secondMetaDataSerializer.getDehydrateSize(0L)).intValue();
		this.metaDataSize += new Long(secondMetaDataSerializer.getDehydrateSize(0L)).intValue();
		this.setNextBlockNumber(secondMetaDataSerializer.hydrate(new ArrayInputBuffer(data.getRightSubArray(inicioMetaData))));
		setTail(this.getBlockNumber().equals(getNextBlockNumber())); 
		this.registryCount = 1;
	}
	private void completeOneBlockData() {
		setNextBlockNumber(blockNumber + 1L);
		setTail(true);
	}
	public Long getBlockNumber() {
		return blockNumber;
	}
	protected void setBlockNumber(Long blockNumber) {
		this.blockNumber = blockNumber;
	}
	public Boolean isHead() {
		return head;
	}
	protected void setHead(Boolean head) {
		this.head = head;
	}
	public Boolean isTail() {
		return tail;
	}
	protected void setTail(Boolean tail) {
		this.tail = tail;
	}
	/**
	 * En registros multipartes es el numero de bloque que tiene la siguiente parte, salvo que isTail() en cuyo caso es igual al número de bloque
	 * En registros que se encuentran en un único bloque es blockNumber +1;
	 */
	public Long getNextBlockNumber() {
		return nextBlockNumber;
	}
	protected void setNextBlockNumber(Long nextBlockNumber) {
		this.nextBlockNumber = nextBlockNumber;
	}
	protected Short getRegistryCount() {
		return registryCount;
	}
	protected void setRegistryCount(Short registryCount) {
		this.registryCount = registryCount;
	}
	protected Integer getMetaDataSize() {
		return metaDataSize;
	}
	protected void setMetaDataSize(Integer metaDataSize) {
		this.metaDataSize = metaDataSize;
	}
	public Integer getDataSize() {
		return this.getBlockSize() - this.getMetaDataSize();
	}
	protected Integer getBlockSize() {
		return blockSize;
	}
	private void setBlockSize(Integer blockSize) {
		this.blockSize = blockSize;
	}
}
