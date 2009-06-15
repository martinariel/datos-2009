package ar.com.datos.compressor.arithmetic;

import ar.com.datos.compressor.SuperChar;

public interface ArithmeticMatcher {
	
	/**
	 * Este matcher permite al emisor y al interprete manejar la misma l�gica de processor
	 * El emisor har� match de acuerdo al caracter que se pidi� emitir, mientras
	 * que el interprete, har� match de acuerdo a que el valor actual sea menor al rango
	 * Ya que el procesador entrega techos crecientes esto implica que el match se dar�
	 * para el menor de los techos. 
	 * @param character
	 * @param ceiling
	 * @return
	 */
	boolean matches(SuperChar character, long ceiling);
}
