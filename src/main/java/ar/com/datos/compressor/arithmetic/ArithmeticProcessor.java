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
	// bit 31 en uno, el resto en cero 
	protected static final long OVERFLOW_SEPARATOR = (1L << 31);
	// bit 30 en uno, el resto en cero 
	protected static final long UNDERFLOW_FLOOR_SEPARATOR = (1L << 30);
	// bit 31 en uno + ( (bit 30 en uno) menos uno, que equivale a bit 30 en 0 y del bit 29 al bit 0 en 1) 
	protected static final long UNDERFLOW_CEILING_SEPARATOR = (1L << 31) + ((1L << 30) - 1L);
	// todos los bits en cero
	protected static final long INITIAL_FLOOR = 0L;
	// (bit 32 en uno) menos uno, que equivale a bit 31 al bit 0 en uno 
	protected static final long INITIAL_CEILING = (1L << 32) - 1L;
	// bit 31 en uno
	protected static final long ONE_IN_OVERFLOW_POSITION = (1L << 31);
	// bit 30 en uno
	protected static final long ONE_IN_UNDERFLOW_POSITION = (1L << 30);

	// Percisión con la que se trabaja
	protected static final int precission = 32;
	
	private long floor = INITIAL_FLOOR;
	private long ceiling = INITIAL_CEILING;
	private int underflowCounter = 0;

	public ArithmeticProcessor() {
		super();
	}

	/**
	 * Procesa la tabla recalculando piso y techo para cada caracter de la tabla.
	 * Cuando el matcher recibido indique que debo parar toma el rango actual como el rango
	 * para el próximo procesado
	 * también verifica los overflow (piso y techo poseen el mismo primer bit) y 
	 * los underflow (piso y techo poseen primero y segundo bit diferentes y opuestos entre piso y techo)
	 * para mejorar la precisión haciendo zoom
	 * @param table
	 * @param matcher
	 * @return
	 */
	protected SuperChar processTable(ProbabilityTable table, ArithmeticMatcher matcher) {
		
		Iterator<Tuple<SuperChar, Double>> tableIterator = table.iterator();
		boolean matched = false;
		Tuple<SuperChar, Double> current = null;
		double probabilityFloor = getMinimumProbability(table);
		long currentFloor = this.floor;
		long currentCeiling = this.floor - 1;
		long range = this.ceiling - this.floor + 1 - table.countCharsWithProbabilityUnder(probabilityFloor);
		while (tableIterator.hasNext() && !matched) {
			current = tableIterator.next();
			currentFloor = currentCeiling + 1;
			currentCeiling = calculateNewCeiling(current, probabilityFloor, currentFloor, range);
			matched = matcher.matches(current.getFirst(), currentCeiling);
		}
		this.floor = currentFloor;
		this.ceiling = currentCeiling;
		clearOverflow();
		clearUnderflow();
		return current.getFirst();
		
	}

	/**
	 * Devuelve el próximo techo.
	 * Para los casos en que la probabilidad le asignaría menos de una posición le asigna el mismo techo (es decir, exactamente una posición)
	 * Para el resto de los casos le asigna el tamaño del rango por la probabilidad redondeado hacia abajo. 
	 * @param current
	 * @param probabilityFloor
	 * @param currentFloor
	 * @param range
	 * @return
	 */
	private long calculateNewCeiling(Tuple<SuperChar, Double> current, double probabilityFloor, long currentFloor, long range) {
		long currentCeiling = currentFloor;
		if (current.getSecond() > probabilityFloor) {
			currentCeiling += new Double(Math.floor(range * current.getSecond())).longValue() - 1;
		}
		return currentCeiling;
	}
	
	/**
	 * Calcula el menor valor para que, dado el rango disponible de valores a para asignar sub-rangos,
	 * a cada caracter le corresponda al menos una posición.
	 * Los caracteres cuya probabilidad les asigne menos de una posición serán tratados excluidos del rango general
	 * y se les asignarán posiciones de antemano. Esto reduce el rango disponible para los símbolos que reciben varias posiciones
	 * pero se asegura que que todos reciban posiciones.  
	 * @param table
	 * @return
	 */
	protected double getMinimumProbability(ProbabilityTable table) {
		int numberOfChars = table.getNumberOfChars();
		// Importante que range sea double para que la división se realice
		// en punto flotante
		double range = this.ceiling - this.floor + 1;
		double minimumProbability = numberOfChars / range;
		int previousAmmountOfChars = 0;
		int currentAmmountOfChars = table.countCharsWithProbabilityUnder(minimumProbability);
		while (previousAmmountOfChars != currentAmmountOfChars) {
			previousAmmountOfChars = currentAmmountOfChars;
			minimumProbability = (numberOfChars - currentAmmountOfChars) / (range - currentAmmountOfChars);
			currentAmmountOfChars = table.countCharsWithProbabilityUnder(minimumProbability);
		}
		return minimumProbability;
	}

	/**
	 * Mientras que detecte underflow descarta el primer caracter de underflow, tanto del piso como del techo,
	 * y hace zoom de esa parte.
	 * También notifica a las subclases mediante <code>{@link #notifyUnderflow()}</code> por si requieren
	 * tomar alguna acción ante la ocurrencia de un underflow
	 */
	protected void clearUnderflow() {
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

	/**
	 * Mientras que detecte overflow descarta el primer caracter de overflow, tanto del piso como del techo,
	 * y hace zoom de esa parte.
	 * Finalmente limpia
	 * tomar alguna acción ante la ocurrencia de un underflow
	 */
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
			flushExistingUnderflow(underflowBit);
		}
	}
	/**
	 * Solicita que se haga flush de los bits de underflow pendientes de flushear
	 * @param underflowBit
	 */
	protected void flushExistingUnderflow(byte underflowBit) {
		for (;this.underflowCounter > 0; this.underflowCounter --) {
			flushUnderflow(underflowBit);
		}
	}
	/**
	 * Limpia la información de un lower overflow y hace zoom al rango
	 * Notifica también de un overflow de bit cero 
	 */
	protected void clearLowerOverflow() {
		zoomCeiling();
		zoomFloor();
		flushOverflow((byte)0);
	}

	/**
	 * Limpia la información de un higher overflow y hace zoom al rango
	 * Notifica también de un overflow de bit uno 
	 */
	protected void clearHigherOverflow() {
		this.ceiling = removeTheOneInOverflowPosition(this.ceiling);
		this.floor = removeTheOneInOverflowPosition(this.floor);
		zoomCeiling();
		zoomFloor();
		flushOverflow((byte)1);
	}

	/**
	 * Flushea los últimos bits como overflow
	 */
	@Override
	public void close() throws IOException {
		this.ceiling = this.floor;
		clearOverflow();
	}
	
	/**
	 * Maneja el caso que se tenga que liberar un overflow 
	 * @param overflowBit
	 */
	protected abstract void flushOverflow(byte overflowBit);
	/**
	 * Maneja el caso que se tenga que liberar un underflow. Siempre ocurre después de un overflow
	 * Pero la cantidad de veces que se invocará a este método depende de la cantidad
	 * de veces que se haya hecho {@link ArithmeticProcessor#notifyUnderflow()} desde el último
	 * overflow 
	 * @param overflowBit
	 */
	protected abstract void flushUnderflow(byte underflowBit);
	/**
	 * Notifica que los rangos del aritmético fueron modificados por la ocurrencia de un underflow. Sin embargo
	 * todavía no se conoce el bit que se debe descartar. Esto será indicado cuando se produzca la llamada a {@link ArithmeticProcessor#flushUnderflow(byte)}
	 */
	protected abstract void notifyUnderflow();
	
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

	/**
	 * Remueve el uno existente en value en la posición correspondiente al overflow
	 * @param value
	 * @return
	 */
	protected long removeTheOneInOverflowPosition(long value) {
		return value - ONE_IN_OVERFLOW_POSITION;
	}

	/**
	 * Agrega un uno en la posición de overflow a value
	 * @param value
	 * @return
	 */
	protected long addTheOneInOverflowPosition(long value) {
		return value + ONE_IN_OVERFLOW_POSITION;
	}
	
	/**
	 * Remueve el uno existente en value en la posición correspondiente al underflow
	 * @param value
	 * @return
	 */
	protected long removeTheOneInUnderflowPosition(long value) {
		return value - ONE_IN_UNDERFLOW_POSITION;
	}

	/**
	 * Provee un shift a izquierda
	 * @param value
	 * @return
	 */
	protected long shiftOneLeft(long value) {
		return value * 2L;
	}

	/**
	 * Indica si se encuentra en underflow
	 * @return
	 */
	protected boolean isInUnderflow() {
		return this.ceiling  <= UNDERFLOW_CEILING_SEPARATOR && this.floor >= UNDERFLOW_FLOOR_SEPARATOR;
	}

	/**
	 * Indica si se encuentra en overflow
	 * @return
	 */
	protected boolean isInOverflow() {
		return this.isInHigherOverflow() || this.isInLowerOverflow();
	}

	/**
	 * Indica si se encuentra en lower overflow (techo y piso comienzan con cero)
	 * @return
	 */
	protected boolean isInLowerOverflow() {
		return this.ceiling < OVERFLOW_SEPARATOR;
	}

	/**
	 * Indica si se encuentra en higher overflow (techo y piso comienzan con uno)
	 * @return
	 */
	protected boolean isInHigherOverflow() {
		return this.floor >=  OVERFLOW_SEPARATOR;
	}

	/**
	 * Devuelve el contador actual de underflows ocurridos desde el último overflow
	 * @return
	 */
	protected int getUnderflowCount() {
		return this.underflowCounter;
	}
	/**
	 * devuelve si value está dentro del rango de piso y techo actual.
	 * @param value
	 * @return
	 */
	protected boolean isInRange(long value) {
		return value <= this.ceiling && value >= this.floor;
	}
}