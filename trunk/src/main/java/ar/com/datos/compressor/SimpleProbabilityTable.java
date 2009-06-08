package ar.com.datos.compressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.util.Tuple;

/**
 * Implementación básica de una tabla de probabilidades.
 * Para cargarla se pasan los caracteres, en el orden deseado, junto con las frecuencias
 * En base a la suma de todas las frecuencias es que calcula la probabilidad de cada caracter (P(i) = f(i) / suma(f(j)) para todo j 
 * @author dev
 * @deprecated
 */
public class SimpleProbabilityTable implements ProbabilityTable {

	private Collection<Tuple<SuperChar, Integer>> frequencies = new ArrayList<Tuple<SuperChar,Integer>>();
	private Long totalNumberOfOcurrencies = 0L;

	/**
	 * Agrega un caracter <code>character</code> con frecuencia <code>frequency</code> a la 
	 * tabla de probabilidades
	 * @param character
	 * @param frequency
	 */
	public void addChar(SuperChar character, Integer frequency) {
		frequencies.add(new Tuple<SuperChar, Integer>(character, frequency));
		totalNumberOfOcurrencies += frequency;
	}
	/**
	 * devuelve el iterador sobre la tabla
	 */
	@Override
	public Iterator<Tuple<SuperChar, Double>> iterator() {
		return new TableIterator(totalNumberOfOcurrencies, frequencies);
	}
	@Override
	public int getNumberOfChars() {
		return this.frequencies.size();
	}
	@Override
	public int countCharsWithProbabilityUnder(double minimumProbability) {
		int counter = 0;
		for (Tuple<SuperChar, Double> tuple : this) {
			if (tuple.getSecond() <= minimumProbability)
				counter++;
		}
		return counter;
	}

	private class TableIterator implements Iterator<Tuple<SuperChar, Double>> {

		private Long totalNumberOfOcurrencies;
		private Iterator<Tuple<SuperChar, Integer>> iterator;
		public TableIterator(Long totalNumberOfOcurrencies, Collection<Tuple<SuperChar, Integer>> frequencies) {
			this.totalNumberOfOcurrencies = totalNumberOfOcurrencies;
			this.iterator = frequencies.iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public Tuple<SuperChar, Double> next() {
			Tuple<SuperChar, Integer> current = iterator.next();
			return new Tuple<SuperChar, Double>(current.getFirst(), current.getSecond() / totalNumberOfOcurrencies.doubleValue());
		}

		@Override
		public void remove() {
		}
		
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[ ");
		for (Tuple<SuperChar, Integer> tup: frequencies) {
			sb.append(tup);
		}
		sb.append(" ]");
		return sb.toString(); 
	}
	@Override
	public boolean contains(SuperChar ch) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public SuperChar removeCharacter(SuperChar ch) {
		// TODO Auto-generated method stub
		return null;
	}
}
