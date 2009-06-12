package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.compressor.lzp.text.TextReceiver;
import ar.com.datos.compressor.lzp.text.impl.MemoryTextEmisorAndReceiver;
import ar.com.datos.util.Tuple;

/**
 * Descompresor LZP.
 * 
 * @author fvalido
 */
public class LzpDeCompressor {
	/**
	 * A partir de la longitud (en la tupla pasada) y el contexto recibido toma la cantidad indicada de
	 * caracteres desde textEmisor y los pone en textReceiver. Para ello busca el contexto en la tabla
	 * para saber la posición del contexto en textEmisor.
	 * Luego agrega el caracter que no matcheaba (second de la tupla) en textReceiver.
	 * Por último actualiza la tabla de trabajo con el contexto y la posición pasadas.
	 * 
	 * Devuelve el nuevo contexto a usar.
	 */
	private LzpContext recoverCharacters(Tuple<Integer, Character> currentTuple, TextReceiver textReceiver, 
										 TextEmisor textEmisor, LzpContext lzpContext, int lzpContextPosition, 
										 LzpContextWorkingTable lzpContextWorkingTable) {
		int sizeMatch = currentTuple.getFirst();
		Character lastMatchCharacter = lzpContext.getSecondChar(); // ultimo caracter de matcheo. Usado para el LzpContext a devolver.
		
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

		// Actualizo la tabla de contextos
		lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
		
		// Agrego el caracter que no matcheaba
		LzpContext returnValue = null;
		if (currentTuple.getSecond() != null) {
			textReceiver.addChar(currentTuple.getSecond());
			returnValue = new LzpContext(lastMatchCharacter, currentTuple.getSecond()); 
		}
		
		return returnValue;
	}
	
	/**
	 * Descomprime el texto recibido usando como fuente el {@link InputBuffer} recibido.
	 */
	public String decompress(InputBuffer input) {
		LzpContextWorkingTable lzpContextWorkingTable = new LzpContextWorkingTable();
		ArithmeticInterpreter arithmetic = new ArithmeticInterpreter(input);
//		ArithmeticInterpreter arithmetic = new AMD(input);
		FirstOrderLzpModel firstOrderLzpModel = new FirstOrderLzpModel();
		ProbabilityTableByFrequencies zeroOrderLzpModel = new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.PRE_EOF_SUPER_CHAR);
		
		MemoryTextEmisorAndReceiver memoryTextEmisorAndReceiver = new MemoryTextEmisorAndReceiver();
		Tuple<Integer, Character> currentTuple;
		Character lzpContextFirstChar = null, lzpContextSecondChar = null;
		LzpContext lzpContext; // contexto lzp actual.
		int lzpContextPosition;
		boolean foundEOF = false;
		
		// Primero proceso los 2 primeros caracters (hasta ahi no tengo contexto y la longitud es seguro 0).
		currentTuple = getNextTuple(arithmetic, firstOrderLzpModel, zeroOrderLzpModel, null);
		foundEOF = true;
		if (currentTuple.getSecond() != null) {
			lzpContextFirstChar = currentTuple.getSecond();
			memoryTextEmisorAndReceiver.addChar(currentTuple.getSecond());
			currentTuple = getNextTuple(arithmetic, firstOrderLzpModel, zeroOrderLzpModel, lzpContextFirstChar);
			if (currentTuple.getSecond() != null) {
				lzpContextSecondChar = currentTuple.getSecond();
				memoryTextEmisorAndReceiver.addChar(currentTuple.getSecond());
				foundEOF = false;
			}
		}

		// Armo el contexto inicial y su posición inicial.
		lzpContext = new LzpContext(lzpContextFirstChar, lzpContextSecondChar);
		lzpContextPosition = 2;
		// Ahora puedo usar un algoritmo general pues no hay más casos especiales.
		while (!foundEOF) {
			currentTuple = getNextTuple(arithmetic, firstOrderLzpModel, zeroOrderLzpModel, lzpContextSecondChar);
			
			lzpContext = recoverCharacters(currentTuple, memoryTextEmisorAndReceiver, memoryTextEmisorAndReceiver, 
										   lzpContext, lzpContextPosition, lzpContextWorkingTable);
			lzpContextPosition += currentTuple.getFirst() + 1;
			lzpContextSecondChar = currentTuple.getSecond();
			foundEOF = lzpContextSecondChar == null;
		}
		lzpContextWorkingTable.close();
		
		return memoryTextEmisorAndReceiver.getText();
	}
	
	/**
	 * Obtiene la siguiente tupla de longitud-caracter desde el aritmético.
	 * Si es la última tupla el caracter será null.
	 * Mantiene actualizados el modelo y la tabla de probabilidades recibidos.
	 */
	private Tuple<Integer, Character> getNextTuple(ArithmeticInterpreter arithmetic, FirstOrderLzpModel firstOrderLzpModel, 
												   ProbabilityTableByFrequencies zeroOrderLzpModel, Character contextCharacter) {
		Integer length = getLength(arithmetic, zeroOrderLzpModel);
		Character character = getCharacter(arithmetic, firstOrderLzpModel, contextCharacter);
		
		return new Tuple<Integer, Character>(length, character);
	}
	
	/**
	 * Recibe la siguiente longitud desde el aritmético.
	 * Mantiene actualizada la tabla de probabilidades recibida.
	 */
	private int getLength(ArithmeticInterpreter arithmetic, ProbabilityTableByFrequencies zeroOrderLzpModel) {
		SuperChar lengthRepresentation = arithmetic.decompress(zeroOrderLzpModel);
		zeroOrderLzpModel.addOccurrence(lengthRepresentation);
		
		return lengthRepresentation.intValue();
	}
	
	
	/**
	 * Recibe el siguiente caracter desde el aritmético. Si el caracter recibido es
	 * EOF entonces devuelve null.
	 * Mantiene actualizado el modelo recibido. 
	 */
	private Character getCharacter(ArithmeticInterpreter arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter) {
		SuperChar superChar = arithmetic.decompress(firstOrderLzpModel.getProbabilityTableFor(contextCharacter));
		
		Character returnValue = null;
		if (!superChar.equals(SuperChar.EOF)) {
			returnValue = superChar.charValue();
			firstOrderLzpModel.addOccurrence(contextCharacter, returnValue);
		}
		
		return returnValue;
	}
}
