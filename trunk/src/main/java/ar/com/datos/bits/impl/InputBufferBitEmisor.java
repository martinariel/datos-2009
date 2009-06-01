package ar.com.datos.bits.impl;

import java.util.Iterator;

import ar.com.datos.bits.BitEmisor;
import ar.com.datos.bits.BitSequence;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;

/**
 * {@link BitEmisor} que obtiene sus bits desde un {@link InputBuffer}.
 * 
 * @author fvalido
 */
public class InputBufferBitEmisor implements BitEmisor {
	/**
	 * InputBuffer usado como origen de cada byte (con sus 8 bits).
	 */
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
		// Notar que luego del primer uso el input buffer queda vacio.
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
		/**
		 * BitSequence usado para completar cada byte.
		 */
		private BitSequence bitSequenceBuffer;

		/**
		 * Posición actual dentro del bitSequenceBuffer.
		 */
		private byte bitSequencePosition;
		
		/**
		 * InputBuffer usado como origen de cada byte (con sus 8 bits).
		 */
		private InputBuffer inputBuffer;

		/**
		 * Constructor.
		 */
		public InputBufferBitIterator(InputBuffer inputBuffer) {
			this.inputBuffer = inputBuffer;
			this.bitSequenceBuffer = new BitSequenceImpl();
			this.bitSequenceBuffer.addBits(this.inputBuffer.read());
			this.bitSequencePosition = 0;
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
			Byte returnValue = this.bitSequenceBuffer.getBit(bitSequencePosition);
			this.bitSequencePosition++;
			if (this.bitSequencePosition == 8) {
				try {
					this.bitSequenceBuffer.clear();
					this.bitSequenceBuffer.addBits(this.inputBuffer.read());
					this.bitSequencePosition = 0;
				} catch (BufferException e) {
					// No se hace nada, queda bitSequencePosition en 8 y entonces hasNext() dará false.
				}
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
