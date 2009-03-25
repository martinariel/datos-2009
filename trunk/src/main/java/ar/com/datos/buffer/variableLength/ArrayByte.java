package ar.com.datos.buffer.variableLength;
/**
 * Clase que encapsula un byte[] y permite generar subarrays a partir de la misma
 * 
 * @author Juan Manuel Barreneche
 *
 */
public class ArrayByte {
	private byte[] miArr;
	private Integer start;
	private Integer length;
	public ArrayByte(byte[] miArr) {
		this.miArr = miArr;
		this.start = 0;
		this.length = miArr.length;
	}
	protected ArrayByte(byte[] miArr, Integer start, Integer length) {
		this(miArr);
		this.start = start;
		this.length = length;
	}
	public Integer getLength() {
		return length;
	}
	public byte getByte(Integer index) {
		return this.miArr[index - start];
	}
	public byte[] getArray() {
		return (this.start == 0 && this.getLength().equals(miArr.length))? miArr : deepCopy();
	}
	private byte[] deepCopy() {
		byte[] retorno = new byte[this.getLength()];
		Integer j = 0;
		for (Integer i = this.start; i < getLength(); i++) {
			retorno[j] = this.miArr[i];
			j += 1;
		}
		return retorno;
	}
	public ArrayByte getRightSubArray(Integer beginIndex) {
		if (beginIndex > getLength()) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, beginIndex, this.getLength() - beginIndex);
	}
	public ArrayByte getLeftSubArray(Integer endIndex) {
		if (endIndex > getLength()) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, 0, endIndex);
	}
	public ArrayByte getSubArray(Integer beginIndex, Integer endIndex) {
		if (endIndex > getLength() || beginIndex > endIndex) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, beginIndex, endIndex);
	}
}
