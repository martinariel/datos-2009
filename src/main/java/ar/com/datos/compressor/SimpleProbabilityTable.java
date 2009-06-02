package ar.com.datos.compressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.util.Tuple;

public class SimpleProbabilityTable implements ProbabilityTable {

	private Collection<Tuple<SuperChar, Integer>> frequencies = new ArrayList<Tuple<SuperChar,Integer>>();
	private Long totalNumberOfOcurrencies = 0L;
	public void addChar(SuperChar character, Integer frequency) {
		frequencies.add(new Tuple<SuperChar, Integer>(character, frequency));
		totalNumberOfOcurrencies += frequency;
	}
	@Override
	public Iterator<Tuple<SuperChar, Double>> iterator() {
		return new TableIterator(totalNumberOfOcurrencies, frequencies);
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
			// TODO Auto-generated method stub
			
		}
		
	}

}
