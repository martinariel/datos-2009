package ar.com.datos.compressor.lzp;

import java.util.Iterator;

import ar.com.datos.compressor.SuperChar;
import ar.com.datos.compressor.arithmetic.ArithmeticInterpreter;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextWorkingTable;
import ar.com.datos.compressor.lzp.text.TextEmisor;
import ar.com.datos.compressor.lzp.text.TextReceiver;
import ar.com.datos.compressor.lzp.text.impl.MemoryTextEmisorAndReceiver;
import ar.com.datos.util.Tuple;

public class LzpDeCompressor {
	/** Descompresor aritmético */
	private ArithmeticInterpreter arithmetic;
	/** Tabla de trabajo para los contextos */
	private LzpContextWorkingTable lzpContextWorkingTable;
	
	/**
	 * Constructor.
	 */
	public LzpDeCompressor() {
		this.lzpContextWorkingTable = new LzpContextWorkingTable();
	}

	
	/**
	 * A partir de la longitud (en la tupla pasada) y el contexto recibido toma la cantidad indicada de
	 * caracteres desde textEmisor y los pone en textReceiver. Para ello busca el contexto en la tabla
	 * para saber la posición del contexto en textEmisor.
	 * Luego agrega el caracter que no matcheaba (second de la tupla) en textReceiver.
	 * Por último actualiza la tabla de trabajo con el contexto y la posición pasadas.
	 * 
	 * Devuelve el nuevo contexto a usar.
	 */
	private LzpContext recoverCharacters(Tuple<Integer, Character> currentTuple, TextReceiver textReceiver, TextEmisor textEmisor, LzpContext lzpContext, int lzpContextPosition) {
		int sizeMatch = currentTuple.getFirst();
		Character lastMatchCharacter = lzpContext.getSecondChar(); // ultimo caracter de matcheo. Usado para el LzpContext a devolver.
		
		if (sizeMatch > 0) {
			// Si hay match... Busco el contexto en la tabla... (y como hubo match, el contexto seguro está
			// en la tabla).
			Long positionMatchStart = this.lzpContextWorkingTable.getPosition(lzpContext);
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
		this.lzpContextWorkingTable.addOrReplace(lzpContext, lzpContextPosition);
		
		// Agrego el caracter que no matcheaba
		if (currentTuple.getSecond() != null) {
			textReceiver.addChar(currentTuple.getSecond());
		}
		
		return new LzpContext(lastMatchCharacter, currentTuple.getSecond());
	}
	
	/**
	 * Descomprime el texto recibido usando como fuente el descompresor aritmético recibido
	 * mediante {@link #setArithmeticCompressor(ArithmeticInterpreter)}
	 * 
	 * PRE: Debe haberse llamado a {@link #setArithmeticCompressor(ArithmeticInterpreter)}
	 */
	public String decompress() {
		this.lzpContextWorkingTable = new LzpContextWorkingTable();

		MemoryTextEmisorAndReceiver memoryTextEmisorAndReceiver = new MemoryTextEmisorAndReceiver();
		Tuple<Integer, Character> currentTuple;
		Character lzpContextFirstChar = null, lzpContextSecondChar = null;
		LzpContext lzpContext; // contexto lzp actual.
		int lzpContextPosition;
		boolean foundEOF = false;
		
		// Primero proceso los 2 primeros caracters (hasta ahi no tengo contexto y la longitud es seguro 0).
		currentTuple = getNextTuple(null);
		foundEOF = true;
		if (currentTuple.getSecond() != null) {
			lzpContextFirstChar = currentTuple.getSecond();
			memoryTextEmisorAndReceiver.addChar(currentTuple.getSecond());
			currentTuple = getNextTuple(lzpContextFirstChar);
			if (currentTuple.getSecond() != null) {
				lzpContextSecondChar = currentTuple.getSecond();
				memoryTextEmisorAndReceiver.addChar(currentTuple.getSecond());
				foundEOF = false;
			}
		}

		// Armo el contexto inicial y su posición inicial.
		lzpContext = new LzpContext(lzpContextFirstChar, lzpContextSecondChar);
		lzpContextPosition = 0;
		// Ahora puedo usar un algoritmo general pues no hay más casos especiales.
		while (!foundEOF) {
			currentTuple = getNextTuple(lzpContextSecondChar);
			
			lzpContext = recoverCharacters(currentTuple, memoryTextEmisorAndReceiver, memoryTextEmisorAndReceiver, lzpContext, lzpContextPosition);
			lzpContextPosition += currentTuple.getFirst() + 1;
			foundEOF = currentTuple.getSecond() == null;
		}
				
		return memoryTextEmisorAndReceiver.getText();
	}
	
	/**
	 * Obtiene la siguiente tupla de longitud-caracter desde el aritmético.
	 * Si es la última tupla el caracter será null.
	 */
	private Tuple<Integer, Character> getNextTuple(Character contextCharacter) {
		// TODO
		Integer length = getLength();
		Character character = getCharacter();
		
		return new Tuple<Integer, Character>(length, character);
	}
	
	/**
	 * Recibe la siguiente longitud desde el aritmético. 
	 */
	private int getLength() {
		// TODO
		SuperChar superChar = this.arithmetic.decompress(null);
		
		return superChar.intValue();
	}
	
	
	/**
	 * Recibe el siguiente caracter desde el aritmético. Si el caracter recibido es
	 * EOF entonces devuelve null. 
	 */
	private Character getCharacter() {
		// TODO
		SuperChar superChar = this.arithmetic.decompress(null);
		
		Character returnValue = null;
		if (!superChar.equals(SuperChar.EOF)) {
			returnValue = superChar.charValue();
		}
		
		return returnValue;
	}
	
	/**
	 * Establece el compresor aritmético desde el cual recibir los datos a interpretar.
	 */
	public void setArithmeticCompressor(ArithmeticInterpreter arithmetic){
		this.arithmetic = arithmetic;
	}
}
