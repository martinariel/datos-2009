package ar.com.datos.btree.sharp.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.util.EspecialListForThirdPart;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.serializer.Serializer;

/**
 * Operaciones de utilidad relacionadas con la división en tercios.
 * 
 * @author fvalido
 */
public class ThirdPartHelper {
	/**
	 * Sin instancias.
	 */
	private ThirdPartHelper() {
	}

	/**
	 * Divide en 3 partes lo más aproximadamente iguales, como sea posible, la lista
	 * pasada considerando a cada uno de los elementos de la lista de tamaño fijo.
	 * La colección original quedará sin elementos.
	 */
	public static <T> List<List<T>> divideInThreeParts(List<T> source) {
		int partSize = Math.round(((float)source.size()) / 3F);
		
		List<T> part = null;
		List<List<T>> returnValue = new LinkedList<List<T>>();
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<T>();
			for (int j = 0; j < partSize && source.size() > 0; j++) {
				part.add(source.remove(0));
			}
			returnValue.add(part);
		}
		while (source.size() > 0) {
			part.add(source.remove(0));
		}
		
		return returnValue;
	}
	
	/**
	 * Divide en 3 partes lo más aproximadamente iguales, como sea posible, la lista
	 * pasada considerando a cada uno de los elementos de la lista de tamaño fijo y
	 * teniendo en cuenta para el cálculo de la igualdad que a la segunda y tercer
	 * parte se les quitará el primer elemento (es decir el cálculo de la igualdad
	 * se hace sin ese elemento)l.
	 * La colección original quedará sin elementos.
	 */
	public static <T> List<List<T>> divideInThreePartsEspecial(List<T> source) {
		int partSize =  Math.round(((float)source.size()) / 3F);;
		if (source.size() % 3 == 2 || source.size() %3 == 0) {
			partSize--;
		}
		
		List<T> part = null;
		List<List<T>> returnValue = new LinkedList<List<T>>();
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<T>();
			for (int j = 0; j < partSize + ((i == 0) ? 0 : 1) && source.size() > 0; j++) {
				part.add(source.remove(0));
			}
			returnValue.add(part);
		}
		while (source.size() > 0) {
			part.add(source.remove(0));
		}
		
		return returnValue;
	}

	/**
	 * Combina 3 listados de Keys con un listado de KeyNodeReference, dejando como resultado
	 * 3 listados de KeyNodeReference. La combinación la realiza tomando en orden la Key de los
	 * 3 listados de Keys y el NodeReference del listado de KeyNodeReference.
	 * La primer KeyNodeReference será la primer KeyNodeReference del listado de KeyNodeReference
	 * sin combinar con el listado de Keys.
	 * 
	 * PRE: Sumatoria(keyParts(i).size()) == keyNodeReferences.size() - 1
	 */
	public static <E extends Element<K>, K extends Key> List<List<KeyNodeReference<E, K>>> combineKeysAndNodeReferences(List<KeyNodeReference<E, K>> keyNodeReferences, List<List<K>> keyParts) {
		List<List<KeyNodeReference<E, K>>> returnValue = new LinkedList<List<KeyNodeReference<E,K>>>();
		List<KeyNodeReference<E, K>> part; 
		
		Iterator<KeyNodeReference<E, K>>itKeyNodeReference = keyNodeReferences.iterator();
		Iterator<K> itKey;
		for (int i = 0; i < 3; i++) {
			part = new LinkedList<KeyNodeReference<E,K>>();
			if (i == 0) {
				part.add(itKeyNodeReference.next());
			}
			itKey = keyParts.get(i).iterator();
			while (itKey.hasNext()) {
				part.add(new KeyNodeReference<E, K>(itKey.next(), itKeyNodeReference.next().getNodeReference()));
			}
			returnValue.add(part);
		}
		
		return returnValue;
	}
	
	/**
	 * Pasa elementos desde thirdPart hacia mainPart o viceversa hasta ajustarse lo mejor posible
	 * a un tamaño tal que thirdPart sea un tercio de la suma del tamaño de thirdPart y mainPart.
	 * Left indica si la tercera parte es la de la izquierda de mainPart o de su derecha.
	 * Para el cálculo del tamaño de las partes se usa serializer.
	 * El parametro mainPartExtraSize se le suma al tamaño de mainPart al hacer los cálculos.
	 */
	public static <T> void balanceThirdPart(List<T> thirdPart, List<T> mainPart, Serializer<List<T>> serializer, long mainPartExtraSize) {
		// Abstracción para facilitar el cálculo de los tamaños y pasaje de elementos.
		EspecialListForThirdPart<T> eThirdPart = new EspecialListForThirdPart<T>(thirdPart, serializer, false);
		EspecialListForThirdPart<T> eMainPart = new EspecialListForThirdPart<T>(mainPart, serializer, true);
		
		long thirdPartSize, mainPartSize, optimalThirdPartSize;

		// Si tengo exactamente el 1/3 (poco probable), terminé...
		thirdPartSize = eThirdPart.size();
		mainPartSize = eMainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		if (thirdPartSize == optimalThirdPartSize) {
			return;
		}
		
		// En lo que sigue, además de lo que dicen los comentarios, siempre verifico
		// que en las partes quede al menos un elemento.
		
		// Si es mayor que 1/3 paso elementos desde thirdPart hacia mainPart
		// hasta que deje de serlo. Sino la viceversa:
		EspecialListForThirdPart<T> source = eThirdPart;
		EspecialListForThirdPart<T> end = eMainPart;
		boolean exceeds = (thirdPartSize > optimalThirdPartSize);
		if (!exceeds) {
			source = eMainPart; 
			end = eThirdPart;
		}
		while ((exceeds == (thirdPartSize > optimalThirdPartSize)) && source.listSize() > 1) {
			source.giveOneElementTo(end);
			// Recalculo los tamaños de las partes (puesto que no puedo saber como serializa
			// el serializador).
			thirdPartSize = eThirdPart.size();
			mainPartSize = eMainPart.size() + mainPartExtraSize;
			optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		}
		// Tomo la diferencia actual con el tamaño óptimo.
		long firstDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Paso un elemento al revés que antes.
		if (end.listSize() > 1) {
			end.giveOneElementTo(source);
		}
		// Tomo de nuevo la diferencia actual con el tamaño óptimo.
		thirdPartSize = eThirdPart.size();
		mainPartSize = eMainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		long secondDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Si la primer diferencia era menor que la segunda vuelvo un paso atrás, sino no hago nada.
		if (firstDifference < secondDifference) {
			source.giveOneElementTo(end);
		}
	}
}
