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

	/** 
	 * Elimina de la tabla un determinado caracter y lo devuelve. 
	 */
	public SuperChar removeCharacter(SuperChar ch);
	
	/** 
	 * Indica si la tabla contiene o no al caracter. 
	 */
	public boolean contains(SuperChar ch);
}
