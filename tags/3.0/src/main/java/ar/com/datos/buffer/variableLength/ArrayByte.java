package ar.com.datos.buffer.variableLength;

public interface ArrayByte {

	public Integer getLength();

	public byte getByte(Integer index);

	public byte[] getArray();

	public ArrayByte getRightSubArray(Integer beginIndex);

	public ArrayByte getLeftSubArray(Integer endIndex);

	public ArrayByte getSubArray(Integer beginIndex, Integer endIndex);

	public void setByte(Integer index, Byte leOctetDeBits);
}