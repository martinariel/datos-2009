package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable4K;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.compressor.lzp.text.TextReceiver;
import ar.com.datos.compressor.lzp.text.impl.MemoryTextEmisorAndReceiver;

/**
 * Descompresor LZP.
 * 
 * @author fvalido
 */
public class LzpDeCompressor {
//	private StringBuffer inputDesdeAritmetico; // DEBUG
//	
//	// DEBUG
//	public String getInputDesdeAritmetico() {
//		return this.inputDesdeAritmetico.toString();
//	}
	
	/**
	 * A partir de la longitud y el contexto recibido toma la cantidad indicada de caracteres
	 * desde textEmisor y los pone en textReceiver. Para ello busca el contexto en la tabla para
	 * saber la posición del contexto en textEmisor.
	 * 
	 * Devuelve el último caracter que se agrega a textReceiver.
	 */
	private Character recoverCharacters(Integer sizeMatch, TextReceiver textReceiver, TextEmisor textEmisor,
										 LzpContext lzpContext, LzpContextWorkingTable lzpContextWorkingTable) {
		Character lastMatchCharacter = lzpContext.getSecondChar(); // ultimo caracter de matcheo.
		
		if (sizeMatch > 0) {
			// Si hay match... Busco el contexto en la tabla... (y como hubo match, el contexto seguro está
			// en la tabla).
			Long positionMatchStart = lzpContextWorkingTable.getPosition(lzpContext);
			// Obtengo un iterador desde la posición del contexto.
			Iterator<Character> precedingIterator = textEmisor.iterator(positionMatchStart.intValue());
			Character addCharacter = null;

			// Copio desde el iterador hacia el texto destino tantos caracteres como sizeMatch
			for (int i = 0; i < sizeMatch; i++) {
				addCharacter = precedingIterator.next();
				textReceiver.addChar(addCharacter);
			}
			lastMatchCharacter = addCharacter; // Me guardo el último caracter que matchea.
		}
		
		return lastMatchCharacter;
	}
	
	/**
	 * A partir del último caracter que matcheó obtiene desde el aritmético el siguiente caracter y
	 * lo agrega en textReceiver.
	 * Devuelve a partir de esto el nuevo {@link LzpContext}.  
	 */
	private LzpContext addCharacter(ArithmeticInterpreter arithmetic, FirstOrderLzpModel firstOrderLzpModel,
								    TextReceiver textReceiver, Character contextCharacter) {
		Character addCharacter = getCharacter(arithmetic, firstOrderLzpModel, contextCharacter);
		
		LzpContext lzpContext = null;
		if (addCharacter != null) {
			textReceiver.addChar(addCharacter);
			lzpContext = new LzpContext(contextCharacter, addCharacter);
		}
		
		return lzpContext;
	}
	
	/**
	 * Descomprime el texto recibido usando como fuente el {@link InputBuffer} recibido.
	 */
	public String decompress(InputBuffer input) {
//		this.inputDesdeAritmetico = new StringBuffer(); // DEBUG
		LzpContextWorkingTable lzpContextWorkingTable = new LzpContextWorkingTable4K();
		ArithmeticInterpreter arithmetic = new ArithmeticInterpreter(input);
		FirstOrderLzpModel firstOrderLzpModel = new FirstOrderLzpModel();
		ProbabilityTableByFrequencies zeroOrderLzpModel = new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.PRE_EOF_SUPER_CHAR);
		
		MemoryTextEmisorAndReceiver memoryTextEmisorAndReceiver = new MemoryTextEmisorAndReceiver();
		Integer sizeMatch;
		Character addCharacter, lastAddedCharacter = null;
		LzpContext lzpContext = null; // contexto lzp actual.
		int lzpContextPosition = 0;
		boolean foundEOF = false;
		
		// Primero proceso los 2 primeros caracters (hasta ahi no tengo contexto y la longitud es seguro 0).
		getLength(arithmetic, zeroOrderLzpModel); // No me interesa. Es 0.
		foundEOF = true;
		addCharacter = getCharacter(arithmetic, firstOrderLzpModel, null);
		if (addCharacter != null) {
			memoryTextEmisorAndReceiver.addChar(addCharacter);

			getLength(arithmetic, zeroOrderLzpModel); // No me interesa. Es 0.
			lzpContext = addCharacter(arithmetic, firstOrderLzpModel, memoryTextEmisorAndReceiver, addCharacter);
			lzpContextPosition = 2;
			foundEOF = (lzpContext == null);
		}


		// Ahora puedo usar un algoritmo general pues no hay más casos especiales.
		while (!foundEOF) {
			sizeMatch = getLength(arithmetic, zeroOrderLzpModel);
			lastAddedCharacter = recoverCharacters(sizeMatch, memoryTextEmisorAndReceiver, memoryTextEmisorAndReceiver,
												   lzpContext, lzpContextWorkingTable);
			
			// Actualizo el contexto en la tabla de contextos
			lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
			
			// Busco el siguiente caracter en el aritmético, lo agrego al texto, y obtengo el siguiente contexto.
			lzpContext = addCharacter(arithmetic, firstOrderLzpModel, memoryTextEmisorAndReceiver, lastAddedCharacter);

			// Calculo la posición para el nuevo contexto.
			lzpContextPosition += sizeMatch + 1;
			foundEOF = (lzpContext == null);
		}
		lzpContextWorkingTable.close();
		arithmetic.close();
		
		return memoryTextEmisorAndReceiver.getText();
	}
	
	/**
	 * Recibe la siguiente longitud desde el aritmético.
	 * Mantiene actualizada la tabla de probabilidades recibida.
	 */
	private int getLength(ArithmeticInterpreter arithmetic, ProbabilityTableByFrequencies zeroOrderLzpModel) {
		SuperChar lengthRepresentation = arithmetic.decompress(zeroOrderLzpModel);
//		this.inputDesdeAritmetico.append("<Longitud>\n"); // DEBUG
//		this.inputDesdeAritmetico.append("TablaFrecuencias:\n"); // DEBUG
//		this.inputDesdeAritmetico.append(zeroOrderLzpModel); // DEBUG
//		this.inputDesdeAritmetico.append("\nLong: " + lengthRepresentation.intValue() + "\n"); // DEBUG
//		this.inputDesdeAritmetico.append("</Longitud>\n"); // DEBUG
		zeroOrderLzpModel.addOccurrence(lengthRepresentation);

		return lengthRepresentation.intValue();
	}
	
	
	/**
	 * Recibe el siguiente caracter desde el aritmético. Si el caracter recibido es
	 * EOF entonces devuelve null.
	 * Mantiene actualizado el modelo recibido. 
	 */
	private Character getCharacter(ArithmeticInterpreter arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter) {
		ProbabilityTable probabilityTable = firstOrderLzpModel.getProbabilityTableFor(contextCharacter);
//		this.inputDesdeAritmetico.append("<Caracter>\n"); // DEBUG
//		this.inputDesdeAritmetico.append("Contexto: " + (contextCharacter == null ? "null" : contextCharacter) + "\n"); // DEBUG
//		this.inputDesdeAritmetico.append("TablaFrecuencias:\n"); // DEBUG
//		this.inputDesdeAritmetico.append(probabilityTable); //DEBUG
		SuperChar superChar = arithmetic.decompress(probabilityTable);
		Character returnValue = null;
		if (!superChar.equals(SuperChar.EOF)) {
			returnValue = superChar.charValue();
			firstOrderLzpModel.addOccurrence(contextCharacter, returnValue);
		}
//		this.inputDesdeAritmetico.append("\nCaracter: " + (returnValue == null ? "EOF" : returnValue) + "\n"); // DEBUG
//		this.inputDesdeAritmetico.append("</Caracter>\n\n"); // DEBUG
		
		return returnValue;
	}
}
