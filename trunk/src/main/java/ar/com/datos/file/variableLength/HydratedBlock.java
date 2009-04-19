package ar.com.datos.file.variableLength;

import java.util.ArrayList;
import java.util.List;

public class HydratedBlock<T> {
	private List<T> datos;
	private List<Long> blockNumbers;
	private Long nextBlockNumber;
	/**
	 * Construye un bloque como un contenedor de registros de tipo T que conocen el número
	 * de bloque en el que están y el próximo bloque (necesario para poder iterar)
	 * @param datos
	 * @param blocks
	 * @param nextBlockNumber
	 */
	public HydratedBlock(List<T> datos, List<Long> blocks, Long nextBlockNumber) {
		super();
		setData(datos);
		setNextBlockNumber(nextBlockNumber);
		setBlockNumbers(blocks);
	}
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
		setBlock(blockNumber);
	}
	protected void setBlock(Long blockNumber) {
		List<Long> blocks = new ArrayList<Long>(1);
		blocks.add(blockNumber);
		setBlockNumbers(blocks);
	}
	public List<T> getData() {
		return datos;
	}
	public void setData(List<T> datos) {
		this.datos = datos;
	}
	public Long getNextBlockNumber() {
		return nextBlockNumber;
	}
	public void setNextBlockNumber(Long nextBlocknumber) {
		this.nextBlockNumber = nextBlocknumber;
	}
	public Long getBlockNumber() {
		return getBlockNumbers().get(0);
	}
	public List<Long> getBlockNumbers() {
		return blockNumbers;
	}
	public void setBlockNumbers(List<Long> blockNumbers) {
		this.blockNumbers = blockNumbers;
	}
}
