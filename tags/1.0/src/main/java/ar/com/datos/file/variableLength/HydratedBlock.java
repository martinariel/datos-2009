package ar.com.datos.file.variableLength;

import java.util.List;

public class HydratedBlock<T> {
	private List<T> datos;
	private Long blockNumber;
	private Long nextBlockNumber;
	/**
	 * Construye un bloque como un contenedor de registros de tipo T que conocen el número
	 * de bloque en el que están y el próximo bloque (necesario para poder iterar)
	 * @param datos
	 * @param blockNumber
	 * @param nextBlockNumber
	 */
	public HydratedBlock(List<T> datos, Long blockNumber, Long nextBlockNumber) {
		super();
		setData(datos);
		setNextBlockNumber(nextBlockNumber);
		setBlockNumber(blockNumber);
		
	}
	public List<T> getData() {
		return datos;
	}
	public void setData(List<T> datos) {
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
