package ar.com.datos.compressor.arithmetic;

import ar.com.datos.compressor.SuperChar;

public interface ArithmeticMatcher {
	
	/**
	 * Este matcher permite al emisor y al interprete manejar la misma lógica de processor
	 * El emisor hará match de acuerdo al caracter que se pidió emitir, mientras
	 * que el interprete, hará match de acuerdo a que el valor actual sea menor al rango
	 * Ya que el procesador entrega techos crecientes esto implica que el match se dará
	 * para el menor de los techos. 
	 * @param character
	 * @param ceiling
	 * @return
	 */
	boolean matches(SuperChar character, long ceiling);
}
