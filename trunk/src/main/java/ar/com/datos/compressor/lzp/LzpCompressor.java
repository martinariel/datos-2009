package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.ProbabilityTable;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable4K;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.util.Tuple;

/**
 * Compresor LZP.
 * 
 * @author fvalido
 */
public class LzpCompressor {
	private static int LONGEST_MATCH = ((1 << 16) - 1);
//	private StringBuffer outputAAritmetico; // DEBUG
	
	// DEBUG
//	public String getOutputAAritmetico() {
//		return this.outputAAritmetico.toString();
//	}
	
	/**
	 * Compara el texto que se obtiene a partir de iteratorCurrent con el texto posterior a la �ltima aparici�n
	 * de lzpContext.
	 * El iterador pasado quedar� posicionado luego del primer caracter que no matchea.
	 * La tabla de trabajo de contextos quedar� actualizada con la �ltima aparici�n del lzpContext.
	 *  
	 * @return
	 * Una tupla con:
	 * - first: la m�xima longitud posible de matcheo (m�ximo 2^16)
	 * - second: Una tupla de caracteres con:
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
			while (match && currentIterator.hasNext() && sizeMatch < LONGEST_MATCH) {
				lastMatch = current;
				
				old = precedingIterator.next();
				current = currentIterator.next();
				match = old.equals(current);
				sizeMatch++;
			}
			if (!match) {
				// Si no hay matcheo significa que sal� del bucle porque encontr� dos diferentes.
				// Entonces lastMatch quedo con el �ltimo que matche� y current tiene el primero que no matchea.
				sizeMatch--;
				firstUnMatch = current; // Pongo en fistUnMatch a current.
			} else {
				// Si sigue habiendo matcheo significa que sal� del bucle porque llegu� a LONGEST_MATCH o
				// poque no hay m�s caracteres.
				// Entonces el �ltimo que matcheo qued� en current, pero todav�a no hab�a sido asignado
				// a lastMatch. Lo hago.
				lastMatch = current;
				// Si sal� porque no hay m�s caracteres entonces luego deben emitir EOF. Lo dejo marcado con
				// null en firstUnMatch.
				firstUnMatch = null;
				// Si hay m�s caracteres entonces sal� porque llegu� a LONGEST_MATCH. Asigno entonces
				// a firstUnMatch el primer caracter disponible (que podr�a ser un matcheo pero eso
				// no me importa).
				if (currentIterator.hasNext()) {
					firstUnMatch = currentIterator.next();
				}
			}

		}

		// Agrego o reemplazo el contexto en la tabla.
		lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
		
		return new Tuple<Integer, Tuple<Character, Character>>(sizeMatch, new Tuple<Character, Character>(lastMatch, firstUnMatch));
	}
	
	/**
	 * Comprime el texto recibido dejando la salida en el {@link OutputBuffer}.
	 */
	public void compress(TextEmisor textEmisor, OutputBuffer output) {
//		this.outputAAritmetico = new StringBuffer(); // DEBUG
		LzpContextWorkingTable lzpContextWorkingTable = new LzpContextWorkingTable4K();
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
		lzpContextPosition = 2;
		
		// Ahora puedo usar un algoritmo general pues no hay m�s casos especiales.
		Tuple<Integer, Tuple<Character, Character>> triple = null;
		boolean lastEmissionIncomplete = false;
		while (textIterator.hasNext()) { // Mientras hayan m�s caracteres.
			triple = compareWithPrecedingText(textEmisor, textIterator, lzpContext, lzpContextPosition, lzpContextWorkingTable);
			
			lastEmissionIncomplete = true;
			
			// Emito lo que debo emitir (Solo si quedan caracteres, sino debo luego emitir EOF)
			if (triple.getSecond().getSecond() != null) {
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
		arithmetic.close();
	}
	
	/**
	 * Emite la longitud de matcheo usando el aritm�tico.
	 * Mantiene actualizada la tabla de probabilidades recibida.
	 */
	private void sendOutLength(ArithmeticEmissor arithmetic, ProbabilityTableByFrequencies zeroOrderLzpModel, int length) {
//		this.outputAAritmetico.append("<Longitud>\n"); // DEBUG
//		this.outputAAritmetico.append("TablaFrecuencias:\n"); // DEBUG
//		this.outputAAritmetico.append(zeroOrderLzpModel); // DEBUG
//		this.outputAAritmetico.append("\nLong: " + length + "\n"); // DEBUG
//		this.outputAAritmetico.append("</Longitud>\n"); // DEBUG
		SuperChar lengthRepresentation = new SimpleSuperChar(length);
		arithmetic.compress(lengthRepresentation, zeroOrderLzpModel);
		zeroOrderLzpModel.addOccurrence(lengthRepresentation);
	}

	/**
	 * Emite el caracter en el contexto adecuado usando el aritm�tico.
	 * Mantiene actualizado el modelo recibido.
	 */
	private void sendOutCharacter(ArithmeticEmissor arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter, Character character) {
//		this.outputAAritmetico.append("<Caracter>\n"); // DEBUG
//		this.outputAAritmetico.append("Contexto: " + (contextCharacter == null ? "null" : contextCharacter) + "\n"); // DEBUG
		ProbabilityTable probabilityTable = firstOrderLzpModel.getProbabilityTableFor(contextCharacter);
//		this.outputAAritmetico.append("TablaFrecuencias:\n"); // DEBUG
//		this.outputAAritmetico.append(probabilityTable); //DEBUG
//		this.outputAAritmetico.append("\nCaracter: " + character + "\n"); // DEBUG
//		this.outputAAritmetico.append("</Caracter>\n\n"); // DEBUG
		arithmetic.compress(new SimpleSuperChar(character), probabilityTable);
		firstOrderLzpModel.addOccurrence(contextCharacter, character);
	}
	
	/**
	 * Emite EOF
	 */
	private void sendOutEOF(ArithmeticEmissor arithmetic, FirstOrderLzpModel firstOrderLzpModel, Character contextCharacter) {
//		this.outputAAritmetico.append("<Caracter>\n"); // DEBUG
//		this.outputAAritmetico.append("Contexto: " + (contextCharacter == null ? "null" : contextCharacter) + "\n"); // DEBUG
		ProbabilityTable probabilityTable = firstOrderLzpModel.getProbabilityTableFor(contextCharacter);
//		this.outputAAritmetico.append("TablaFrecuencias:\n"); // DEBUG
//		this.outputAAritmetico.append(probabilityTable); //DEBUG
//		this.outputAAritmetico.append("\nCaracter: EOF\n"); // DEBUG
//		this.outputAAritmetico.append("</Caracter>\n\n"); // DEBUG
		arithmetic.compress(SuperChar.EOF, probabilityTable);
	}
}
