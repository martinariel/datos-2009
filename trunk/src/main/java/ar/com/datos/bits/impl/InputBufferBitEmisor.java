package ar.com.datos.bits.impl;

import java.util.Iterator;

import ar.com.datos.bits.BitEmisor;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;

/**
 * {@link BitEmisor} que obtiene sus bits desde un {@link InputBuffer}.
 * 
 * @author fvalido
 */
public class InputBufferBitEmisor implements BitEmisor {
	/** InputBuffer usado como origen de cada byte (con sus 8 bits). */
	private InputBuffer inputBuffer;
	
	/**
	 * Construye una instancia.
	 * 
	 * @param inputBuffer
	 * Buffer de origen para los bits emitidos.
	 */
	public InputBufferBitEmisor(InputBuffer inputBuffer) {
		this.inputBuffer = inputBuffer;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitEmisor#iterator()
	 */
	@Override
	public Iterator<Byte> iterator() {
		// Notar que luego del primer uso el input buffer queda vacio (si se lo
		// usa hasta al final; pero por lo menos quedará con un byte de menos).
		// Si es necesario se puede modificar para que los bits emitidos sean
		// guardados en algún lugar para su uso posterior, pero aparentemente
		// esto no es necesario por ahora (por eso no lo hago).
		return new InputBufferBitIterator(this.inputBuffer);
	}
	
	/**
	 * Iterador de bits basado en un InputBuffer de origen en el que cada 
	 * byte otorga 8 bits.
	 * 
	 * @author fvalido
	 */
	private class InputBufferBitIterator implements Iterator<Byte> {
		/** Estructura usada para iterar los bits de cada byte del buffer. */
		private byte[] bitSequenceBuffer;
		/** Estructura usada para saber la posición actual dentro de bitSequenceBuffer */
		private byte bitSequencePosition;
		/** InputBuffer usado como origen de cada byte (con sus 8 bits). */
		private InputBuffer inputBuffer;

		/**
		 * Obtiene desde inputBuffer
		 */
		private void getMoreBitsFromInputBuffer() {
			try {
				byte sourceBits = this.inputBuffer.read();
				int andOperator = 128;
				for (int i = 0; i < 8 ; i++) {
					this.bitSequenceBuffer[i] = (byte)((sourceBits & andOperator) > 0 ? 1 : 0);
					andOperator /= 2;
				}
				this.bitSequencePosition = 0;
			} catch (BufferException e) {
				// No hago nada. bitSequencePosition va a quedar en 8 y por tanto hasNext() va a dar false.
			}
		}
		
		/**
		 * Constructor.
		 */
		public InputBufferBitIterator(InputBuffer inputBuffer) {
			this.inputBuffer = inputBuffer;
			this.bitSequencePosition = 8;
			this.bitSequenceBuffer = new byte[8];
			getMoreBitsFromInputBuffer();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return this.bitSequencePosition < 8;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		public Byte next() {
			Byte returnValue = this.bitSequenceBuffer[bitSequencePosition];
			this.bitSequencePosition++;
			if (this.bitSequencePosition == 8) {
				getMoreBitsFromInputBuffer();
			}
			
			return returnValue;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
