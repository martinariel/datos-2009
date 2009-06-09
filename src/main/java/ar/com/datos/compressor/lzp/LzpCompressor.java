package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.compressor.arithmetic.ArithmeticEmissor;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.util.Tuple;

public class LzpCompressor {
	/** Compresor aritmético */
	private ArithmeticEmissor arithmetic;
	/** Tabla de trabajo para los contextos */
	private LzpContextWorkingTable lzpContextWorkingTable;
	
	/**
	 * Constructor.
	 */
	public LzpCompressor() {
	}

	/**
	 * Compara el texto que se obtiene a partir de iteratorCurrent con el texto posterior a la última aparición
	 * de lzpContext.
	 * El iterador pasado quedará posicionado luego del primer caracter que no matchea.
	 * La tabla de trabajo de contextos quedará actualizada con la última aparición del lzpContext.
	 *  
	 * @return
	 * Una tupla con:
	 * - first: la máxima longitud posible de matcheo (máximo 2^16)
	 * - second: tupla con:
	 *    - último caracter que matcheó (si no matcheó [longitud 0] será el segundo del lzpContext pasado).
	 *    - primer caracter que no matcheó.
	 */
	private Tuple<Integer, Tuple<Character, Character>> compareWithPrecedingText(TextEmisor textEmisor, Iterator<Character> currentIterator, LzpContext lzpContext, int lzpContextPosition) {
		Long positionMatchStart = this.lzpContextWorkingTable.getPosition(lzpContext);
		
		int sizeMatch = 0;
		Character lastMatch = lzpContext.getSecondChar();
		Character firstUnMatch;
		if (positionMatchStart == null) {
			// El contexto pasado no existía todavía.
			firstUnMatch = currentIterator.next();
		} else {
			// Encontré el contextoPasado.
			Iterator<Character> precedingIterator = textEmisor.iterator(positionMatchStart.intValue());
			
			/* Proceso un caracter de cada iterador hasta que deje de haber match. Mientras
			 * tanto aumento la longitudde matcheo y voy guardando el último caracter que
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
		this.lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
		
		return new Tuple<Integer, Tuple<Character,Character>>(sizeMatch, new Tuple<Character, Character>(lastMatch, firstUnMatch));
	}
	
	/**
	 * Comprime el texto recibido usando para ello el compresor aritmético recibido
	 * mediante {@link #setArithmeticCompressor(ArithmeticEmissor)}.
	 * 
	 * PRE: Debe haberse llamado a {@link #setArithmeticCompressor(ArithmeticEmissor)}
	 */
	public void compress(TextEmisor textEmisor) {
		this.lzpContextWorkingTable = new LzpContextWorkingTable();
		
		Iterator<Character> textIterator = textEmisor.iterator(0);
		Character currentChar = null; // último caracter que no matcheó (caracter a emitir).
		Character previousChar = null; // último caracter que matcheó (caracter de contexto).
		LzpContext lzpContext; // contexto lzp actual.
		int lzpContextPosition;
		
		// Primero proceso los 2 primeros caracters (hasta ahi no tengo contexto).
		if (textIterator.hasNext()) {
			previousChar = null;
			currentChar = textIterator.next();
			sendOutLength(0); // Esto se podría evitar porque ya se que la primera vez emito con long 0 (lo mantengo porque se explicó así).
			sendOutCharacter(previousChar, currentChar);
		}
		if (textIterator.hasNext()) {
			previousChar = currentChar;
			currentChar = textIterator.next();
			sendOutLength(0); // Esto se podría evitar porque ya se que la segunda vez emito con long 0 (lo mantengo porque se explicó así).
			sendOutCharacter(previousChar, currentChar);
		}
		
		// Armo el contexto inicial y su posición inicial.
		lzpContext = new LzpContext(previousChar, currentChar);
		lzpContextPosition = 0;
		
		// Ahora puedo usar un algoritmo general pues no hay más casos especiales.
		Tuple<Integer, Tuple<Character, Character>> triple = null;
		boolean lastEmissionIncomplete = false;
		while (textIterator.hasNext()) { // Mientras hayan más caracteres.
			triple = compareWithPrecedingText(textEmisor, textIterator, lzpContext, lzpContextPosition);
			
			lastEmissionIncomplete = true;
			
			// Emito lo que debo emitir (Solo si quedan caracteres, sino debo luego emitir EOF)
			if (textIterator.hasNext()) {
				lastEmissionIncomplete = false;
				sendOutLength(triple.getFirst());
				sendOutCharacter(triple.getSecond().getFirst(), triple.getSecond().getSecond());
			
				// Actualizo el contexto
				lzpContext = new LzpContext(triple.getSecond().getFirst(), triple.getSecond().getSecond());
				lzpContextPosition += triple.getFirst() + 1;
			}
		}
		// Emito EOF.
		if (lastEmissionIncomplete) {
			// La "última" emisión tuvo una longitud mayor a 0 (hubo un match al final)
			sendOutLength(triple.getFirst());
			sendOutEOF(triple.getSecond().getFirst());
		} else {
			// La última emisión tuvo una longitud igual a 0 (no hubo match al final).
			sendOutLength(0);
			sendOutEOF(triple.getSecond().getSecond());
		}
		
		this.lzpContextWorkingTable.close();
	}
	
	/**
	 * Emite la longitud de matcheo usando el aritmético.
	 */
	private void sendOutLength(int length) {
		// TODO
	}

	/**
	 * Emite el caracter en el contexto adecuado usando el aritmético.
	 */
	private void sendOutCharacter(Character contextCharacter, Character character) {
		// TODO
	}
	
	/**
	 * Emite EOF
	 */
	private void sendOutEOF(Character contextCharacter) {
		// TODO
	}
	
	/**
	 * Establece el compresor aritmético hacia el cual emitir.
	 */
	public void setArithmeticCompressor(ArithmeticEmissor arithmetic){
		this.arithmetic = arithmetic;
	}
}
