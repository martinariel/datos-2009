package ar.com.datos.compressor.ppmc;

import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.SuperChar;

/**
 * Representa un contexto para el compresor PPMC. 
 * @author marcos
 */
public interface Context {

	/**
	 * Devuelve el {@link Context} de nivel superior ("hijo") correspondiente al 
	 * caracter recibido por parametro. Devuelve null si el contexto no existe.
	 * @param c el caracter para el cual se busca el contexto
	 * @return el contexto buscado si existe o null si no lo existe.
	 */
	Context getNextContextFor(SuperChar c);
	
	/**
	 * Devuelve la tabla de probabilidad correspondiente a este contexto.
	 * Esta tabla siempre existe ya que un contexto no tiene sentido sin su 
	 * tabla de probabilidades. Lo minimo que tendra la tabla sera un ESC con
	 * probabilidad 1. 
	 * @return la {@link ProbabilityTable} de este contexto. Nunca null.
	 */
	ProbabilityTable getProbabilityTable();
}
