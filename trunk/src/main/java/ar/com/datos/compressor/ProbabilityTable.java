package ar.com.datos.compressor;

import ar.com.datos.util.Tuple;

public interface ProbabilityTable extends Iterable<Tuple<SuperChar, Double>>{

	int getNumberOfChars();

	int countCharsWithProbabilityUnder(double minimumProbability);

	
}
