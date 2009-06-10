package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.util.Tuple;

/**
 * Compresor LZP.
 * 
 * @author fvalido
 */
public class LzpCompressor {
	/**
	 * Compara el texto que se obtiene a partir de iteratorCurrent con el texto posterior a la �ltima aparici�n
	 * de lzpContext.
	 * El iterador pasado quedar� posicionado luego del primer caracter que no matchea.
	 * La tabla de trabajo de contextos quedar� actualizada con la �ltima aparici�n del lzpContext.
	 *  
	 * @return
	 * Una tupla con:
	 * - first: la m�xima longitud posible de matcheo (m�ximo 2^16)
	 * - second: tupla con:
	 *    - �ltimo caracter que matche� (si no matche� [longitud 0] ser� el segundo del lzpContext pasado).
	 *    - primer caracter que no matche�.
	 */
	private Tuple<Integer, Tuple<Character, Character>> compareWithPrecedingText(TextEmisor textEmisor, Iterator<Character> currentIterator, 
																				 LzpContext lzpContext, int lzpContextPosition,
																				 LzpContextWorkingTable lzpContextWorkingTable) {
		Long positionMatchStart = lzpContextWorkingTable.getPosition(lzpContext);
		
		int sizeMatch = 0;
		Character lastMatch = lzpContext.getSecondChar();
		Character firstUnMatch;
		if (positionMatchStart == null) {
			// El contexto pasado no exist�a todav�a.
			firstUnMatch = currentIterator.next();
		} else {
			// Encontr� el contextoPasado.
			Iterator<Character> precedingIterator = textEmisor.iterator(positionMatchStart.intValue());
			
			/* Proceso un caracter de cada iterador hasta que deje de haber match. Mientras
			 * tanto aumento la longitudde matcheo y voy guardando el �ltimo caracter que
			 * matchea. */
			boolean match = true;
			Character old, current = lastMatch;
			while (match && currentIterator.hasNext()) {
				lastMatch = current;
				
				old = precedingIterator.next();
				current = currentIterator.next();
				match = old.equals(current);
				sizeMatch++;
			}
			if (!match) {
				sizeMatch--;
			}
			firstUnMatch = current; // El primero que no matchea.
		}

		// Agrego o reemplazo el contexto en la tabla.
		lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
		
		return new Tuple<Integer, Tuple<Character,Character>>(sizeMatch, new Tuple<Character, Character>(lastMatch, firstUnMatch));
	}
	
	/**
	 * Comprime el texto recibido dejando la salida en el {@link OutputBuffer}.
	 */
	public void compress(TextEmisor textEmisor, OutputBuffer output) {
		LzpContextWorkingTable lzpContextWorkingTable = new LzpContextWorkingTable();
		ArithmeticEmissor arithmetic = new ArithmeticEmissor(output);
		FirstOrderLzpModel firstOrderLzpModel = new FirstOrderLzpModel();
		ProbabilityTableByFrequencies zeroOrderLzpModel = new ProbabilityTableByFrequencies(new SimpleSuperChar(0), SuperChar.PRE_EOF_SUPER_CHAR);
		
		Iterator<Character> textIterator = textEmisor.iterator(0);
		Character currentChar = null; // �ltimo caracter que no matche� (caracter a emitir).
		Character previousChar = null; // �ltimo caracter que matche� (caracter de contexto).
		LzpContext lzpContext = null; // contexto lzp actual.
		int lzpContextPosition;
		
		// Primero proceso los 2 primeros caracters (hasta ahi no tengo contexto).
		if (textIterator.hasNext()) {
			previousChar = null;
			currentChar = textIterator.next();
			sendOutLength(arithmetic, zeroOrderLzpModel, 0); // Esto se podr�a evitar porque ya se que la primera vez emito con long 0 (lo mantengo porque se explic� as�).
			sendOutCharacter(arithmetic, firstOrderLzpModel, previousChar, currentChar);
		}
		if (textIterator.hasNext()) {
			previousChar = currentChar;
			currentChar = textIterator.next();
			sendOutLength(arithmetic, zeroOrderLzpModel, 0); // Esto se podr�a evitar porque ya se que la segunda vez emito con long 0 (lo mantengo porque se explic� as�).
			sendOutCharacter(arithmetic, firstOrderLzpModel, previousChar, currentChar);
		}
		
		// Armo el contexto inicial y su posici�n inicial.
		if (previousChar != null && currentChar != null) {
			lzpContext = new LzpContext(previousChar, currentChar);
		}
		lzpContextPosition = 0;
		
		// Ahora puedo usar un algoritmo general pues no hay m�s casos especiales.
		Tuple<Integer, Tuple<Character, Character>> triple = null;
		boolean lastEmissionIncomplete = false;
		while (textIterator.hasNext()) { // Mientras hayan m�s caracteres.
			triple = compareWithPrecedingText(textEmisor, textIterator, lzpContext, lzpContextPosition, lzpContextWorkingTable);
			
			lastEmissionIncomplete = true;
			
			// Emito lo que debo emitir (Solo si quedan caracteres, sino debo luego emitir EOF)
			if (textIterator.hasNext()) {
				lastEmissionIncomplete = false;
				sendOutLength(arithmetic, zeroOrderLzpModel, triple.getFirst());
				sendOutCharacter(arithmetic, firstOrderLzpModel, triple.getSecond().getFirst(), triple.getSecond().getSecond());
			
				// Actualizo el contexto
				lzpContext = new LzpContext(triple.getSecond().getFirst(), triple.getSecond().getSecond());
				lzpContextPosition += triple.getFirst() + 1;
			}
		}
		// Emito EOF.
		if (lastEmissionIncomplete) {
			// La "�ltima" emisi�n tuvo una longitud mayor a 0 (hubo un match al final)
			sendOutLength(arithmetic, zeroOrderLzpModel, triple.getFirst());
			sendOutEOF(arithmetic, firstOrderLzpModel, triple.getSecond().getFirst());
		} else {
			// La �ltima emisi�n tuvo una longitud igual a 0 (no hubo match al final).
			sendOutLength(arithmetic, zeroOrderLzpModel, 0);
			if (triple != null) {
				sendOutEOF(arithmetic, firstOrderLzpModel, triple.getSecond().getSecond());
			} else {
				sendOutEOF(arithmetic, firstOrderLzpModel, null);
			}
		}
		
		lzpContextWorkingTable.close();
	}
	
	/**
	 * Emite la longitud de matcheo usando el aritm�tico.
	 * Mantiene actualizada la tabla de probabilidades recibida.
	 */
	private void sendOutLength(ArithmeticEmissor arithmetic, ProbabilityTableByFrequencies zeroOrderLzpModel, int length) {
		arithmetic.compress(new SimpleSuperChar(length), zeroOrderLzpModel);
	}

	/**
	 * Emite el caracter en el contexto adecuado usando el aritm�tico.
	 * Mantiene actualizado el modelo recibido.
	 */
	private void sendOutCharacter(ArithmeticEmissor arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter, Character character) {
		arithmetic.compress(new SimpleSuperChar(character), firstOrderLzpModel.getProbabilityTableFor(contextCharacter));
		firstOrderLzpModel.addOccurrence(contextCharacter, character);
	}
	
	/**
	 * Emite EOF
	 */
	private void sendOutEOF(ArithmeticEmissor arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter) {
		arithmetic.compress(SuperChar.EOF, firstOrderLzpModel.getProbabilityTableFor(contextCharacter));
	}
}
