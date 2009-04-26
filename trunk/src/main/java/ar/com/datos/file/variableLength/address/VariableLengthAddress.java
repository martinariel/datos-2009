package ar.com.datos.file.variableLength.address;

import ar.com.datos.file.address.BlockAddress;

public class VariableLengthAddress implements BlockAddress<Long, Short> {

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

	@Override
	public String getUnifiedAddress() {
		return String.format("%1$019d%2$05d", getBlockNumber(), getObjectNumber());
	}

}
