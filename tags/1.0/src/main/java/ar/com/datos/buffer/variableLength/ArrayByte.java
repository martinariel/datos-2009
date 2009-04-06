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
		return this.miArr[index + start];
	}
	public byte[] getArray() {
		return (this.start == 0 && this.getLength().equals(miArr.length))? miArr : deepCopy();
	}
	private byte[] deepCopy() {
		byte[] retorno = new byte[this.getLength()];
		for (Integer i = 0; i < getLength(); i++) {
			retorno[i] = this.getByte(i);
		}
		return retorno;
	}
	public ArrayByte getRightSubArray(Integer beginIndex) {
		if (beginIndex > getLength()) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, this.start + beginIndex, this.getLength() - beginIndex);
	}
	public ArrayByte getLeftSubArray(Integer endIndex) {
		if (endIndex > getLength()) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, this.start, endIndex);
	}
	public ArrayByte getSubArray(Integer beginIndex, Integer endIndex) {
		if (endIndex > getLength() || beginIndex > endIndex) throw new IndexOutOfBoundsException();
		return new ArrayByte(this.miArr, this.start + beginIndex, endIndex - beginIndex);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		for (int i = start; i < (length + start); i++) {
			result = prime * result + miArr[i];
		}
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayByte other = (ArrayByte) obj;
		if (length == null) {
			if (other.length != null)
				return false;
		} else if (!length.equals(other.length))
			return false;
		for (int i = 0; i < (this.getLength()); i++) {
			if (this.getByte(i) != other.getByte(i)) return false;
		}
		return true;
	}
}
