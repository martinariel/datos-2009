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

}
