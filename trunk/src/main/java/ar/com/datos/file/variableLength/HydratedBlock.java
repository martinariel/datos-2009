package ar.com.datos.file.variableLength;

import java.util.List;

public class HydratedBlock<T> {
	private List<T> datos;
	private Long blockNumber;
	private Long nextBlockNumber;
	public HydratedBlock(List<T> datos, Long blockNumber, Long nextBlockNumber) {
		super();
		setDatos(datos);
		setNextBlockNumber(nextBlockNumber);
		setBlockNumber(blockNumber);
		
	}
	public List<T> getDatos() {
		return datos;
	}
	public void setDatos(List<T> datos) {
		this.datos = datos;
	}
	public Long getBlockNumber() {
		return blockNumber;
	}
	public void setBlockNumber(Long blockNumber) {
		this.blockNumber = blockNumber;
	}
	public Long getNextBlockNumber() {
		return nextBlockNumber;
	}
	public void setNextBlockNumber(Long nextBlocknumber) {
		this.nextBlockNumber = nextBlocknumber;
	}
}
