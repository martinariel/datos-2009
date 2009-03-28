package ar.com.datos.file.variableLength;

import ar.com.datos.file.Address;

public class VariableLengthAddress implements Address<Long, Short> {

	private Long block;
	private Short object;
	public VariableLengthAddress(Long block, Short object) {
		this.block = block;
		this.object = object;
	}

	@Override
	public Long getBlockNumber() {
		return this.block;
	}

	@Override
	public Short getObjectNumber() {
		return this.object;
	}

}
