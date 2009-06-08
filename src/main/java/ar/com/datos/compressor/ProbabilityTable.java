package ar.com.datos.compressor;

import ar.com.datos.util.Tuple;

public interface ProbabilityTable extends Iterable<Tuple<SuperChar, Double>>{
	
	/**
	 * Devuelve la cantidad de caracteres en la tabla de probabilidades
	 */
	public int getNumberOfChars();

	/**
	 * Cuenta la cantidad de caracteres con probabilidad menor o igual a la recibida por parámetro
	 */
	public int countCharsWithProbabilityUnder(double minimumProbability);
}
