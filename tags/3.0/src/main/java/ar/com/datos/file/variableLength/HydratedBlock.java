package ar.com.datos.file.variableLength;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import ar.com.datos.file.BlockFile;
import ar.com.datos.persistencia.variableLength.BlockReader;

public class HydratedBlock<T> {
	private List<T> datos;
	private List<Long> blockNumbers;
	private Long nextBlockNumber;
	private BlockFile file;
	/**
	 * Construye un bloque como un contenedor de registros de tipo T que conocen el número
	 * de bloque en el que están y el próximo bloque (necesario para poder iterar)
	 */
	public HydratedBlock(List<T> datos, List<Long> blocks, BlockFile file) {
		super();
		setData(datos);
		setNextBlockNumber(null);
		setBlockNumbers(blocks);
		this.file = file;
	}
	/**
	 * Construye un bloque como un contenedor de registros de tipo T que conocen el número
	 * de bloque en el que están y el próximo bloque (necesario para poder iterar)
	 */
	public HydratedBlock(List<T> datos, Long blockNumber, BlockFile file) {
		super();
		setData(datos);
		setNextBlockNumber(nextBlockNumber);
		setBlock(blockNumber);
		this.file = file;
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
		if (nextBlockNumber != null) return nextBlockNumber; 
		
		Long minimo = this.getBlockNumber() + 1L;

		SortedSet<Long> valores = new TreeSet<Long>();
		for (Long blockNumber : this.getBlockNumbers()) {
			if (minimo <= blockNumber) valores.add(blockNumber);
		}
		for (Long valor: valores) {
			if (valor > minimo) break;
			minimo += 1;
		}
		BlockReader auxBlockReader = new BlockReader(this.file);
		while (minimo < this.file.getTotalBlocks()) {
			auxBlockReader.readBlock(minimo);
			if (auxBlockReader.isBlockHead()) break;
			minimo += 1;
		}
		
		if (minimo >= (this.file.getTotalBlocks())) return BlockFile.END_BLOCK;
		
		return minimo;
		
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
