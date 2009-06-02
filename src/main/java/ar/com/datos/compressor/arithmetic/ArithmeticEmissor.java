package ar.com.datos.compressor.arithmetic;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import ar.com.datos.bits.BitReceiver;
import ar.com.datos.bits.impl.OutputBufferBitReceiver;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.util.Tuple;

public class ArithmeticEmissor implements Closeable {
	/**
	 * El long que se trabaja posee 64 bits, numerados de b(63) a b(0)
	 * Dado que el compresor requiere sólo 32 bits, se utilizan del b(31) al b(0)
	 */
	// bit 31 en uno, el resto en cero 
	private static final long OVERFLOW_SEPARATOR = (1L << 31);
	// bit 30 en uno, el resto en cero 
	private static final long UNDERFLOW_FLOOR_SEPARATOR = (1L << 30);
	// bit 31 en uno + ( (bit 30 en uno) menos uno, que equivale a bit 30 en 0 y del bit 29 al bit 0 en 1) 
	private static final long UNDERFLOW_CEILING_SEPARATOR = (1L << 31) + ((1L << 30) - 1L);
	// todos los bits en cero
	private static final long INITIAL_FLOOR = 0L; 
	// (bit 32 en uno) menos uno, que equivale a bit 31 al bit 0 en uno 
	private static final long INITIAL_CEILING = (1L << 32) - 1L;
	// bit 31 en uno
	private static final long ONE_IN_OVERFLOW_POSITION = (1L << 31); 

	private BitReceiver bitReceiver;
	private long floor = INITIAL_FLOOR;
	private long ceiling = INITIAL_CEILING;
	private int underflowCounter = 0;

	public ArithmeticEmissor(OutputBuffer outputBuffer) {
		this.bitReceiver = constructBitReceiver(outputBuffer);
	}

	protected BitReceiver constructBitReceiver(OutputBuffer outputBuffer) {
		return new OutputBufferBitReceiver(outputBuffer);
	}

	public SuperChar compress(SimpleSuperChar characterAEmitir, ProbabilityTable table) {
		Iterator<Tuple<SuperChar, Double>> tableIterator = table.iterator();
		boolean matched = false;
		Tuple<SuperChar, Double> current = null;;
		long currentFloor = this.floor;
		long currentCeiling = this.floor - 1;
		long range = this.ceiling - this.floor + 1; 
		while (tableIterator.hasNext() && !matched) {
			current = tableIterator.next();
			currentFloor = currentCeiling + 1;
			currentCeiling = currentFloor + new Double(Math.floor(range * current.getSecond())).longValue() - 1;
			matched = current.getFirst().matches(characterAEmitir);
		}
		this.floor = currentFloor;
		this.ceiling = currentCeiling;
		clearOverflow();
		clearUnderFloor();
		return current.getFirst();
	}

	protected void clearUnderFloor() {
		while (this.isInUnderflow()) {
			// Saco el uno, hago shift y reagrego el uno (esto me da un shiftleft desde los bits 30 al 0
			this.ceiling = removeTheOneInOverflowPosition(this.ceiling);
			zoomCeiling();
			this.ceiling = addTheOneInOverflowPosition(this.ceiling);
			// hago el shift completo y saco el uno que estaba en la posición de underflow
			zoomFloor();
			this.floor   = removeTheOneInOverflowPosition(this.floor);
			this.underflowCounter ++;
		}
	}

	protected void clearOverflow() {
		while (this.isInOverflow()) {
			byte underflowBit;
			if (this.isInHigherOverflow()) {
				clearHigherOverflow();
				underflowBit = 0;
			} else {
				clearLowerOverflow();
				underflowBit = 1;
			}
			flushUnderflow(underflowBit);
		}
	}

	protected void flushUnderflow(byte underflowBit) {
		for (;this.underflowCounter > 0; this.underflowCounter --) {
			this.bitReceiver.addBit(underflowBit);
		}
	}

	private void clearLowerOverflow() {
		this.bitReceiver.add0();
		zoomCeiling();
		zoomFloor();
	}


	private void clearHigherOverflow() {
		this.bitReceiver.add1();
		this.ceiling = removeTheOneInOverflowPosition(this.ceiling);
		this.floor = removeTheOneInOverflowPosition(this.floor);
		zoomCeiling();
		zoomFloor();
	}

	private long removeTheOneInOverflowPosition(long value) {
		return value - ONE_IN_OVERFLOW_POSITION;
	}

	private long addTheOneInOverflowPosition(long value) {
		return value + ONE_IN_OVERFLOW_POSITION;
	}

	/**
	 * Corro en un lugar todos los bits e inyecto un cero
	 */
	private void zoomFloor() {
		this.floor = shiftOneLeft(this.floor);
	}

	/**
	 * Corro en un lugar todos los bits e inyecto un cero
	 */
	protected void zoomCeiling() {
		this.ceiling = shiftOneLeft(this.ceiling) + 1L;
	}

	protected long shiftOneLeft(long value) {
		return value * 2L;
	}

	protected boolean isInUnderflow() {
		return this.ceiling  <= UNDERFLOW_CEILING_SEPARATOR && this.floor >= UNDERFLOW_FLOOR_SEPARATOR;
	}

	protected boolean isInOverflow() {
		return this.isInHigherOverflow() || this.isInLowerOverflow();
	}

	protected boolean isInLowerOverflow() {
		return this.ceiling < OVERFLOW_SEPARATOR;
	}

	protected boolean isInHigherOverflow() {
		return this.floor >=  OVERFLOW_SEPARATOR;
	}

	@Override
	public void close() throws IOException {
		this.ceiling = this.floor;
		clearOverflow();
		this.bitReceiver.close();
		
	}

}
