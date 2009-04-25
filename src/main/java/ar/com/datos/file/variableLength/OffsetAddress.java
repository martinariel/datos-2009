package ar.com.datos.file.variableLength;

import ar.com.datos.file.address.Address;

public class OffsetAddress implements Address {

	private Long offset;
	
	public OffsetAddress(Long offset) {
		super();
		this.offset = offset;
	}

	@Override
	public String getUnifiedAddress() {
		return null;
	}

	public Long getOffset() {
		return offset;
	}

}
