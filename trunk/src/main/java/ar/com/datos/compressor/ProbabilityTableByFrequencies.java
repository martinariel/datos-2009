package ar.com.datos.compressor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import ar.com.datos.util.Tuple;
import ar.com.datos.util.TupleByFirst;
import ar.com.datos.util.UpsideDownComparator;

/**
 * Implementación de {@link ProbabilityTable}.
 * Permite manejar de manera eficiente estos casos:
 * - Se tienen todos los chars posibles con probabilidad inicial 1. Se permite independientemente:
 *   (1) Incrementar la frecuencia de un caracter. 
 *   (2) Trabajar con una lista de exclusión
 *   !! (2) sin (1) es el caso orden -1 de PPMC.
 *   !! (1) sin (2) es el caso LZP o un aritmético común y corriente (aritmético de orden n).
 * - Se tiene inicialmente una lista vacia y se incorporan caracteres. Se permite independientemente;
 *   (3) Incrementar la frecuencia de un caracter. 
 *   (4) Trabajar con una lista de exclusión 
 *   !! (3) y (4) es el caso orden 0-(n-1) de PPMC.
 *   !! (3) sin (4) es el caso orden n de PPMC.
 *   
 * El manejo de una lista con todos los chars posibles o sin ellos se hace desde el constructor,
 * que recibe el menor y el mayor char posible. Si se quiere empezar sin ningún caracter e incorporarlos
 * luego se debe pasar en el constructor un char "mayor" posible que sea menor al "menor" posible.
 * 
 * El manejo de listas de exclusión debe antes de que se pase el objeto {@link ProbabilityTableByFrequencies}
 * al aritmético; debiendo pasar una lista vacia en caso de no haber exclusiones.
 * 
 * @author fvalido
 */
public class ProbabilityTableByFrequencies implements ProbabilityTable {
	/** 
	 * Conjunto ordenado tuplas de un SuperChar y una frecuencia; El criterio de ordenamiento
	 * es: primero por frecuencia descendente, y a igual frecuencia por orden alfabético.
	 */
	private NavigableSet<Tuple<SuperChar, Long>> frequenciesTable;
	/**
	 * frequenciesTable no está indexado por char, por lo que buscar por un char implicaría
	 * hacer un recorrido secuencial... Entonces se tiene esta otra estructura que tiene
	 * las tuplas indexadas por el char... Primero se busca la tupla aquí y luego, una vez
	 * obtenida, se usa la tupla para buscar en frequenciesTable.
	 */
	private Map<SuperChar, Tuple<SuperChar, Long>> frequenciesTableIndex;
	/** Número total de ocurrencias. */
	private long totalNumberOfOcurrencies;
	/** Frecuencia inicial para un caracter que aún no fue agregado. */
	private long initialFrequency;
	/** Mínimo caracter posible. */
	private SuperChar lowestSuperChar;
	/** Máximo caracter posible. */
	private SuperChar highestSuperChar;
	/** Conjunto de SuperChar a excluir */
	private Set<SuperChar> excluded;
	/** Cantidad de ocurrencias de excluidos */
	private long excludedNumberOfOcurrencies;
	
	/**
	 * Permite indicar que ha aumentado la frecuencia de un término.
	 */
	public void addOcurrency(SuperChar superChar) {
		// Lo busco en la estructura índice.
		Tuple<SuperChar, Long> charFrequency = this.frequenciesTableIndex.get(superChar);
		// Si estaba...
		if (charFrequency != null) {
			// Debo avisarle de alguna forma al frequenciesTable que el objeto se va
			// a actualizar [cambia su frecuencia!] (para que actualize su posición de ordenamiento).
			// Para ello lo saco (y luego lo agregaré).
			this.frequenciesTable.remove(charFrequency);
			// Y actualizo su frecuencia.
			charFrequency.setSecond(charFrequency.getSecond() + 1);
		} else {
			// Si no existía creo la tupla.
			long frequency = 1;
			if (superChar.compareTo(this.lowestSuperChar) >= 0 && superChar.compareTo(this.highestSuperChar) <= 0) {
				frequency = this.initialFrequency + 1;
			}
			charFrequency = new TupleByFirst<SuperChar, Long>(superChar, frequency);
			// Y la agrego a la estructura de indexación.
			this.frequenciesTableIndex.put(superChar, charFrequency);
		}
		
		// Agrego/reagrego la tupla a la estructura ordenada.
		this.frequenciesTable.add(charFrequency);
		
		// Por último actualizo el número total de ocurrencias.
		this.totalNumberOfOcurrencies++;
		if (this.excluded.contains(superChar)) {
			this.excludedNumberOfOcurrencies++;
		}
	}

	
	/**
	 * Busca la frecuencia de un superChar sin tomar en cuenta los excluidos.
	 */
	private long getFrequencyFor(SuperChar superChar) {
		long frequency = 0;
		
		Tuple<SuperChar, Long> charFrequency = this.frequenciesTableIndex.get(superChar);
		if (charFrequency == null) {
			if (superChar.compareTo(this.highestSuperChar) <= 0 && superChar.compareTo(this.lowestSuperChar) >= 0) {
				frequency = this.initialFrequency;
			}
		} else {
			frequency = charFrequency.getSecond();
		}
		
		return frequency;
	}
	
	/**
	 * Establece el conjunto de excluidos.
	 */
	public void setExcludedSet(Set<SuperChar> excluded) {
		this.excluded = excluded;
		
		// Actualizo la cantidad de ocurrencias de excluidos.
		this.excludedNumberOfOcurrencies = 0;
		Iterator<SuperChar> it = excluded.iterator();
		while (it.hasNext()) {
			this.excludedNumberOfOcurrencies += getFrequencyFor(it.next());
		}
	}
	
	/**
	 * Permite agregar un char al conjunto de excluidos.
	 */
	public void addToExcludedSet(SuperChar superChar) {
		this.excluded.add(superChar);
		this.excludedNumberOfOcurrencies += getFrequencyFor(superChar);
	}
	
	/**
	 * Constructor.
	 * Ver notas de la clase para más detalles!!
	 * 
	 * @param lowerCharValue
	 * Mínimo caracter posible.
	 * 
	 * @param higherCharValue
	 * Máximo caracter posible.
	 */
	public ProbabilityTableByFrequencies(SuperChar lowestSuperChar, SuperChar highestSuperChar) {
		Comparator<Tuple<SuperChar, Long>> comparator = new UpsideDownComparator<Tuple<SuperChar,Long>>(new Tuple.SecondComparator<Tuple<SuperChar, Long>, Long>());
		this.lowestSuperChar = lowestSuperChar;
		this.highestSuperChar = highestSuperChar;
		this.totalNumberOfOcurrencies = highestSuperChar.intValue() - lowestSuperChar.intValue() + 1;
		if (this.totalNumberOfOcurrencies < 0) {
			totalNumberOfOcurrencies = 0;
			this.initialFrequency = 0;
		} else {
			this.initialFrequency = 1;
		}
		this.frequenciesTable = new TreeSet<Tuple<SuperChar, Long>>(comparator);
		this.frequenciesTableIndex = new HashMap<SuperChar, Tuple<SuperChar,Long>>();
		
		this.excluded = new HashSet<SuperChar>();
	}


	/**
	 * Obtiene la cantidad de caracteres contenidos en frequenciesTable y no contenidos en excluded
	 * cuya frecuencia sea menor que la pasada.
	 */
	private int countCharsFromFrequencyTableWithFrequencyUnder(long minimunFrequency) {
		int returnValue = 0;
		
		// Mayor caracter posible.
		SuperChar charAux = new SimpleSuperChar(Integer.MIN_VALUE);

		// Obtengo un subset con todos los caracteres que tienen una probabilidad inferior a la frecuencia
		// y caracter obtenidos.
		Tuple<SuperChar, Long> fromElement = new Tuple<SuperChar, Long>(charAux, minimunFrequency);
		Set<Tuple<SuperChar, Long>> subset = this.frequenciesTable.tailSet(fromElement, false);
		Iterator<Tuple<SuperChar, Long>> it = subset.iterator();
		Tuple<SuperChar, Long> currentCharFrequency;
		while (it.hasNext()) {
			currentCharFrequency = it.next();
			if (!this.excluded.contains(currentCharFrequency.getFirst())) {
				returnValue++;
			}
		}
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.ProbabilityTable#countCharsWithProbabilityUnder(double)
	 */
	@Override
	public int countCharsWithProbabilityUnder(double minimumProbability) {
		// A partir de la probabilidad que me pasan calculo la frecuencia (exacta o) inmediatamente superior
		// que me permita obtener una probabilidad (igual o) mayor a la pasada. Toda frecuencia menor
		// a esta se corresponderá con una probabilidad menor a la pasada.
		long minimunFrequency = (long)Math.ceil(minimumProbability * (this.totalNumberOfOcurrencies - this.excludedNumberOfOcurrencies));
		
		int returnValue = 0;

		if (minimunFrequency > 1) {
			if (this.initialFrequency != 0) {
				// Si la frecuencia inicial es distinta de 0, entonces estoy usando todos los chars posibles.
				// Todos los chars que no estén en frequenciesTable ni en excluded tienen frecuencia 1, que es
				// la menor frecuencia posible. Por tanto si frequencyAux es mayor que 1, entonces los
				// mencionados cumplen la condición. A ellos hay que sumarles los de frequenciesTable que
				// no estén en excluded y también cumplan la condición.

				// Necesito los elementos de frequenciesTable y excluded sin repeticiones...
				Set<SuperChar> excludedFromRange = new HashSet<SuperChar>(this.excluded);
				excludedFromRange.addAll(this.frequenciesTableIndex.keySet());
				int sizeExcludedFromRange = 0;
				Iterator<SuperChar> itExcluded = excludedFromRange.iterator();
				SuperChar currentExcluded;
				while (itExcluded.hasNext()) {
					currentExcluded = itExcluded.next();
					if (currentExcluded.compareTo(this.lowestSuperChar) >= 0 && currentExcluded.compareTo(this.highestSuperChar) <= 0) {
						sizeExcludedFromRange++;
					}
				}
				// Con ellos calculo la cantidad de caracteres del rango..
				returnValue = this.highestSuperChar.intValue() - this.lowestSuperChar.intValue() + 1 - sizeExcludedFromRange;
				
				// Ahora veo los de frequenciesTable que no están en excluded y también cumplen...
				returnValue += countCharsFromFrequencyTableWithFrequencyUnder(minimunFrequency);
			} else {
				// Si la frecuencia inicial es 0, entonces estoy usando solo una lista reducida con
				// exclusiones.
				// Solo tengo que contabilizar cuantos de frequenciesTable que no están en excluded cumplen.
				returnValue = countCharsFromFrequencyTableWithFrequencyUnder(minimunFrequency);
			}
		}
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.ProbabilityTable#getNumberOfChars()
	 */
	@Override
	public int getNumberOfChars() {
		int returnValue = this.highestSuperChar.intValue() - this.lowestSuperChar.intValue() + 1;
		if (returnValue <= 0) {
			returnValue = this.frequenciesTable.size();
		}
		returnValue -= this.excluded.size();
		
		return returnValue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Tuple<SuperChar, Double>> iterator() {
		return new ProbabilityTableIterator();
	}

	/**
	 * Iterador para {@link ProbabilityTableByFrequencies}
	 * 
	 * @author fvalido
	 */
	private class ProbabilityTableIterator implements Iterator<Tuple<SuperChar, Double>> {
		/** Iterador de la tabla de frecuencias */
		private Iterator<Tuple<SuperChar, Long>> frequenciesIt;
		/** Siguiente tupla a devolver */
		private Tuple<SuperChar, Long> nextSuperChar;
		/** Número de ocurrencias a usar para calcular la probabilidad */
		private long numberOfOcurrencies;
		/** Indica si las tuplas deben obtenerse desde la tabla de frecuencias
		 *  o desde el rango. */
		private boolean flagFromFrequenciesTable;
		
		/**
		 * Deja preparada la siguiente tupla a devolver cuando se deben usar los caracteres del rango.
		 */
		private void prepareNextSuperCharUsingRangeSuperChars(SuperChar currentSuperChar) {
			if (currentSuperChar.compareTo(highestSuperChar) > 0) {
				// No hay más valores para devolver.
				this.nextSuperChar = null;
			} else {
				// Si ese super Char está en la tabla de frecuencias (ya lo usé) o en la tabla de 
				// excluidos (no debo usarlo), pido otro.
				if (frequenciesTableIndex.containsKey(currentSuperChar) || excluded.contains(currentSuperChar)) {
					prepareNextSuperCharUsingRangeSuperChars(new SimpleSuperChar(currentSuperChar.intValue() + 1));
				} else {
					// Sino encontré uno para usar :)
					this.nextSuperChar = new Tuple<SuperChar, Long>(currentSuperChar, initialFrequency);
				}
			}
		}
		
		/**
		 * Deja preparada la siguiente tupla a devolver obteniéndola de tabla de frecuencias.
		 * Devuelve si pudo hallar o no una siguiente tupla usando esa tabla.
		 */
		private void prepareNextSuperCharUsingFrequenciesTable() {
			this.nextSuperChar = null;
			if (this.frequenciesIt.hasNext()) {
				this.nextSuperChar = this.frequenciesIt.next();
				// Si el superChar está en los excluidos pido otro.
				if (excluded.contains(this.nextSuperChar.getFirst())) {
					prepareNextSuperCharUsingFrequenciesTable();
				}			
			}
		}
		
		/**
		 * Deja preparada la siguiente tupla a devolver.
		 */
		private void prepareNextSuperChar() {
			if (this.flagFromFrequenciesTable) {
				// Los que están en la tabla de frecuencias tienen mayor probabilidad que los
				// que están en el rango (pues estos tienen a lo sumo una probabilidad de 1).
				// Por tanto si dispongo de uno de dicha tabla lo uso primero.
				prepareNextSuperCharUsingFrequenciesTable();
				
				// Si no hay más chars en la tabla de frecuencias.
				if (this.nextSuperChar == null) {
					// Si desean usar rango...
					if (initialFrequency != 0) {
						// Switcheo al modo usar desde el rango y dejo preparado el primero de dicho rango. 
						this.flagFromFrequenciesTable = false;
						prepareNextSuperCharUsingRangeSuperChars(new SimpleSuperChar(lowestSuperChar.intValue()));
					}
				}
			} else {
				prepareNextSuperCharUsingRangeSuperChars(new SimpleSuperChar(this.nextSuperChar.getFirst().intValue() + 1));
			}
		}
		
		public ProbabilityTableIterator() {
			this.numberOfOcurrencies = totalNumberOfOcurrencies - excludedNumberOfOcurrencies;
			this.frequenciesIt = frequenciesTable.iterator();
			this.flagFromFrequenciesTable = true;

			// Inicializo nextSuperChar con el primer valor a devolver...
			prepareNextSuperChar();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return (nextSuperChar != null);
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Tuple<SuperChar, Double> next() {
			SuperChar currentSuperChar = this.nextSuperChar.getFirst();
			double currentProbability = ((double)this.nextSuperChar.getSecond()) / (double)this.numberOfOcurrencies;
			
			Tuple<SuperChar, Double> returnValue = new Tuple<SuperChar, Double>(currentSuperChar, currentProbability);
			
			prepareNextSuperChar();
			
			return returnValue;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
