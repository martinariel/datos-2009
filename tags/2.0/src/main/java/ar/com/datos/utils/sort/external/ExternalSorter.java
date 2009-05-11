package ar.com.datos.utils.sort.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.com.datos.file.BlockFile;
import ar.com.datos.file.SimpleBlockFile;
import ar.com.datos.util.ArrayOfBytesComparator;

public class ExternalSorter {

	private BlockFile file;
	private Integer ammountOfBlocks;
	public ExternalSorter(BlockFile file, Integer ammountOfBlocks) {
		this.file = file;
		this.ammountOfBlocks = ammountOfBlocks;
	}

	public List<BlockFile> getSortedChunks() {
		List<BlockFile> chunks = new ArrayList<BlockFile>();
		Comparator<byte[]> comparador = getNewComparator();
		// Para los casos que la cantidad de archivos temporales a generar exceda a 127 fuerzo a esa cantidad de 
		if ((this.file.getTotalBlocks() / this.ammountOfBlocks) > 127)
			this.ammountOfBlocks = this.file.getTotalBlocks().intValue() / 127;
		List<byte[]> currentChunkData = new ArrayList<byte[]>(this.ammountOfBlocks);
		for (byte[] data : this.file) {
			currentChunkData.add(data);
			if (currentChunkData.size() == this.ammountOfBlocks) {
				addCurrentChunk(chunks, comparador, currentChunkData);
			}
		}
		if (!currentChunkData.isEmpty()) addCurrentChunk(chunks, comparador, currentChunkData);
		return chunks;
	}

	private void addCurrentChunk(Collection<BlockFile> chunks, Comparator<byte[]> comparador, List<byte[]> currentChunkData) {
		BlockFile tempFile = constructTempFile();
		Collections.sort(currentChunkData, comparador);
		for (byte[] sortedData : currentChunkData) {
			tempFile.appendBlock(sortedData);
		}
		tempFile.close();
		chunks.add(tempFile);
		currentChunkData.clear();
	}
	protected BlockFile constructTempFile() {
		return new SimpleBlockFile(this.file.getBlockSize());
	}

	protected Comparator<byte[]> getNewComparator() {
		return new ArrayOfBytesComparator();
	}
}
