package ar.com.datos.compressor;

import java.util.Iterator;

import ar.com.datos.util.Tuple;
/**
 * Tabla de probabilidades incremental.
 * Inicia con todos los caracteres de un rango con frecuencias equiprobables (1)
 * Luego permite incrementar las frecuencias de cualquiera de dicho rango
 * @author dev
 *
 * @deprecated
 */
public class IncrementalProbabilityTable implements ProbabilityTable {

	private Integer lowerCharValue;
	private Integer higherCharValue;
	public IncrementalProbabilityTable(int lower, int higher) {
		lowerCharValue = lower;
		higherCharValue = higher;
	}

	@Override
	public int countCharsWithProbabilityUnder(double minimumProbability) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfChars() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<Tuple<SuperChar, Double>> iterator() {
		return new IncrementalProbabilityTableIterator(this);
	}
	
	public double getProbabilityFor(SuperChar aChar) {
		return 1.0;
	}
	protected class IncrementalProbabilityTableIterator implements Iterator<Tuple<SuperChar, Double>> {

		private int currentChar;
		private IncrementalProbabilityTable table;
		public IncrementalProbabilityTableIterator(IncrementalProbabilityTable incrementalProbabilityTable) {
			this.currentChar = incrementalProbabilityTable.getLowerCharValue();
			this.table = incrementalProbabilityTable;
		}

		@Override
		public boolean hasNext() {
			return this.table.getHigherCharValue() > this.currentChar ;
		}

		@Override
		public Tuple<SuperChar, Double> next() {
			SimpleSuperChar aChar = new SimpleSuperChar(this.currentChar);
			return null;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
	protected int getLowerCharValue() {
		return lowerCharValue;
	}

	protected void setLowerCharValue(int lowerCharValue) {
		this.lowerCharValue = lowerCharValue;
	}

	protected int getHigherCharValue() {
		return higherCharValue;
	}

	protected void setHigherCharValue(int higherCharValue) {
		this.higherCharValue = higherCharValue;
	}
}
