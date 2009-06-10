package ar.com.datos.compressor.lzp;

import java.util.HashMap;
import java.util.Map;

import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;

/**
 * Modelo de orden 1 del Lzp.
 * 
 * @author fvalido
 */
public class FirstOrderLzpModel {
	/** Contextos del modelo */
	private Map<Character, ProbabilityTableByFrequencies> contexts;
	
	/**
	 * Constructor.
	 */
	public FirstOrderLzpModel() {
		this.contexts = new HashMap<Character, ProbabilityTableByFrequencies>(); 
	}

	/**
	 * Permite obtener la tabla de probabilidades correspondiente al contexto pasado.
	 */
	private ProbabilityTableByFrequencies getProbabilityTable(Character contextCharacter) {
		if (contextCharacter == null) {
			return new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.EOF);
		}
		
		ProbabilityTableByFrequencies returnValue = this.contexts.get(contextCharacter);
		if (returnValue == null) {
			returnValue = new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.EOF);
			this.contexts.put(contextCharacter, returnValue);
		}
		
		return returnValue;
	}
	
	/**
	 * Permite obtener la tabla de probabilidades correspondiente al contexto pasado.
	 */
	public ProbabilityTable getProbabilityTableFor(Character contextCharacter) {
		return getProbabilityTable(contextCharacter);
	}
	
	/**
	 * Permite aumentar la frecuencia de un caracter dentro de un modelo.
	 */
	public void addOccurrence(Character contextCharacter, Character occurrence) {
		ProbabilityTableByFrequencies probabilityTable = getProbabilityTable(contextCharacter);
		probabilityTable.addOccurrence(new SimpleSuperChar(occurrence));
	}
}
