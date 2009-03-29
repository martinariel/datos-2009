package ar.com.datos.file.variableLength;

import java.util.List;
import java.util.Queue;

public class HydratedBlock {
	private List<Queue<Object>> datos;
	private Long blockNumber;
	private Long nextBlockNumber;
	public HydratedBlock(List<Queue<Object>> datos, Long blockNumber, Long nextBlockNumber) {
		super();
		setDatos(datos);
		setNextBlockNumber(nextBlockNumber);
		setBlockNumber(blockNumber);
		
	}
	public List<Queue<Object>> getDatos() {
		return datos;
	}
	public void setDatos(List<Queue<Object>> datos) {
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
