package ar.com.datos.bits.impl;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.bits.BitSequence;
import ar.com.datos.buffer.OutputBuffer;

/**
 * {@link BitReceiver} que guarda sus bits en un {@link OutputBuffer}.
 * 
 * @author fvalido
 */
public class OutputBufferBitReceiver implements BitReceiver {
	/**
	 * BitSequence usado para completar cada byte.
	 */
	private BitSequence bitSequenceBuffer;
	/**
	 * OutputBuffer usado como destino de cada byte completado.
	 */
	private OutputBuffer outputBuffer;
	
	/**
	 * Construye una instancia.
	 * 
	 * @param outputBuffer
	 * Buffer de destino para los bits agregados.
	 */
	public OutputBufferBitReceiver(OutputBuffer outputBuffer) {
		this.outputBuffer = outputBuffer;
		this.bitSequenceBuffer = new BitSequenceImpl();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#add0()
	 */
	@Override
	public void add0() {
		addBit((byte)0);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#add1()
	 */
	@Override
	public void add1() {
		addBit((byte)1);
	}

	/**
	 * Finaliza el agregado de un byte completo.
	 */
	private void closeByte() {
		this.outputBuffer.write(this.bitSequenceBuffer.toDecimalByte(0, (byte)8));
		this.bitSequenceBuffer.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#addBit(byte)
	 */
	@Override
	public void addBit(byte bit) {
		this.bitSequenceBuffer.addBit(bit);
		if (this.bitSequenceBuffer.getBitsCount() == 8) {
			closeByte();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.bits.BitReceiver#close()
	 */
	@Override
	public void close() {
		closeByte();
	}
}
