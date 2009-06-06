package ar.com.datos.compressor;

import ar.com.datos.util.Tuple;
/**
 * Interfaz que responde la representación de una tabla de probabilidades que utiliza
 * el aritmético para comprimir
 * @author jbarreneche
 *
 */
public interface ProbabilityTable extends Iterable<Tuple<SuperChar, Double>>{

	/**
	 * Devuelve la cantidad de caracteres en la tabla de probabilidades
	 */
	int getNumberOfChars();

	/**
	 * Cuenta la cantidad de caracteres con probabilidad menor o igual a la recibida por parámetro
	 */
	int countCharsWithProbabilityUnder(double minimumProbability);

	
}
