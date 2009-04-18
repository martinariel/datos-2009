package ar.com.datos.file.variableLength;

import java.util.ArrayList;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.buffer.OverloadableOutputBuffer;
import ar.com.datos.buffer.SimpleInputBuffer;
import ar.com.datos.file.BlockFile;
import ar.com.datos.persistencia.variableLength.BlockReader;

/**
 * Clase auxiliar en la que delega el VariableLengthFileManager para obtener el
 * último bloque cargado en un OutputBuffer
 * 
 * @author dev
 * 
 */
public class BlockAppenderManager {

	public static final Long END_BLOCK = -1L;
	private BlockFile blockFile;
	private DelayedLastBlockReaderState estado = new UnreadState(this);

	public BlockAppenderManager(BlockFile blockFile) {
		setBlockFile(blockFile);
	}

	protected BlockFile getBlockFile() {
		return blockFile;
	}

	protected void setBlockFile(BlockFile blockFile) {
		this.blockFile = blockFile;
	}

	public OverloadableOutputBuffer getOutputBuffer() {
		return estado.getOutputBuffer();
	}

	private interface DelayedLastBlockReaderState {
		public OverloadableOutputBuffer getOutputBuffer();
	}

	private class UnreadState implements DelayedLastBlockReaderState {

		private BlockAppenderManager stateOwner;

		public UnreadState(BlockAppenderManager stateOwner) {
			super();
			this.stateOwner = stateOwner;
		}

		@Override
		public OverloadableOutputBuffer getOutputBuffer() {
			this.stateOwner.estado = new LoadedState(this.stateOwner);
			return this.stateOwner.estado.getOutputBuffer();
		}
	}

	private class LoadedState implements DelayedLastBlockReaderState {

		private OverloadableOutputBuffer buffer;

		public LoadedState(BlockAppenderManager stateOwner) {
			super();
			this.buffer = retrieveLastBlock(stateOwner);
		}

		/**
		 * Recupera el ultimo bloque, y lo hidrata en un buffer. En caso que el ultimo bloque
		 * pertenezca a un registro que no esta completo o que el archivo esta vacio crea un nuevo
		 * buffer sin datos
		 * @param stateOwner 
		 * @return
		 */
		protected OverloadableOutputBuffer retrieveLastBlock(BlockAppenderManager stateOwner) {
			BlockReader br = new BlockReader(stateOwner.getBlockFile());
			// Voy al último bloque del archivo
			Long lastBlockNumber = stateOwner.getBlockFile().getTotalBlocks() - 1;
			br.goToBlock(lastBlockNumber);
			// Si es un head, significa que puedo hidratarlo
			if (br.isBlockHead()) {
//				fillLastBlockBufferWith(br.getData(), lastBlockNumber, br.getRegistryCount());
			}
			return null;
		}
		@Override
		public OverloadableOutputBuffer getOutputBuffer() {
			return this.buffer;
		}
	}
}
