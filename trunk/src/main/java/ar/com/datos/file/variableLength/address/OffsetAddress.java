package ar.com.datos.file.variableLength.address;

import ar.com.datos.file.address.Address;


public class OffsetAddress implements Address {

	private Long offset;
	
	public OffsetAddress(Long offset) {
		super();
		this.offset = offset;
	}

	@Override
	public String getUnifiedAddress() {
		return String.format("%1$019d", getOffset());
	}

	public Long getOffset() {
		return offset;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
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
		OffsetAddress other = (OffsetAddress) obj;
		if (offset == null) {
			if (other.offset != null)
				return false;
		} else if (!offset.equals(other.offset))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder("< ").append(this.offset).append(" >").toString();
	}
}
