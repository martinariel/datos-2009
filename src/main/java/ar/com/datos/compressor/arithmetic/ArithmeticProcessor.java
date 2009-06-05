package ar.com.datos.compressor.arithmetic;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.util.Tuple;

public abstract class ArithmeticProcessor implements Closeable {

	/**
	 * El long que se trabaja posee 64 bits, numerados de b(63) a b(0)
	 * Dado que el compresor requiere sólo 32 bits, se utilizan del b(31) al b(0)
	 */
	protected static final long OVERFLOW_SEPARATOR = (1L << 31);
	protected static final long UNDERFLOW_FLOOR_SEPARATOR = (1L << 30);
	protected static final long UNDERFLOW_CEILING_SEPARATOR = (1L << 31) + ((1L << 30) - 1L);
	protected static final long INITIAL_FLOOR = 0L;
	protected static final long INITIAL_CEILING = (1L << 32) - 1L;
	protected static final long ONE_IN_OVERFLOW_POSITION = (1L << 31);
	protected static final long ONE_IN_UNDERFLOW_POSITION = (1L << 30);

	protected static final int precission = 32;
	
	private long floor = INITIAL_FLOOR;
	private long ceiling = INITIAL_CEILING;
	private int underflowCounter = 0;

	public ArithmeticProcessor() {
		super();
	}

	protected long removeTheOneInOverflowPosition(long value) {
		return value - ONE_IN_OVERFLOW_POSITION;
	}

	protected long addTheOneInOverflowPosition(long value) {
		return value + ONE_IN_OVERFLOW_POSITION;
	}
	protected long removeTheOneInUnderflowPosition(long value) {
		return value - ONE_IN_UNDERFLOW_POSITION;
	}


	protected SuperChar processTable(ProbabilityTable table, ArithmeticMatcher matcher) {
		
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
			matched = matcher.matches(current.getFirst(), currentCeiling);
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
			this.floor   = removeTheOneInUnderflowPosition(this.floor);
			zoomFloor();
			this.underflowCounter ++;
			notifyUnderflow();
		}
	}

	protected void notifyUnderflow() {
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
			clearUnderflow(underflowBit);
		}
	}
	protected void clearUnderflow(byte underflowBit) {
		for (;this.underflowCounter > 0; this.underflowCounter --) {
			flushUnderflow(underflowBit);
		}
	}

	protected void clearLowerOverflow() {
		zoomCeiling();
		zoomFloor();
		flushOverflow((byte)0);
	}

	protected void clearHigherOverflow() {
		this.ceiling = removeTheOneInOverflowPosition(this.ceiling);
		this.floor = removeTheOneInOverflowPosition(this.floor);
		zoomCeiling();
		zoomFloor();
		flushOverflow((byte)1);
	}

	@Override
	public void close() throws IOException {
		this.ceiling = this.floor;
		clearOverflow();
	}
	
	protected abstract void flushUnderflow(byte underflowBit);
	protected abstract void flushOverflow(byte overflowBit);
	
	/**
	 * Corro en un lugar todos los bits e inyecto un cero
	 */
	protected void zoomFloor() {
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

	protected int getUnderflowCount() {
		return this.underflowCounter;
	}
	protected boolean isInRange(long value) {
		return value <= this.ceiling && value >= this.floor;
	}
}