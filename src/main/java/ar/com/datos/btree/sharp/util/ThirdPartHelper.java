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
 * Operaciones de utilidad relacionadas con la divisi�n en tercios.
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
	 * Divide en 3 partes lo m�s aproximadamente iguales, como sea posible, la lista
	 * pasada considerando a cada uno de los elementos de la lista de tama�o fijo.
	 * La colecci�n original quedar� sin elementos.
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
	 * Divide en 3 partes lo m�s aproximadamente iguales, como sea posible, la lista
	 * pasada considerando a cada uno de los elementos de la lista de tama�o fijo y
	 * teniendo en cuenta para el c�lculo de la igualdad que a la segunda y tercer
	 * parte se les quitar� el primer elemento (es decir el c�lculo de la igualdad
	 * se hace sin ese elemento)l.
	 * La colecci�n original quedar� sin elementos.
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
	 * 3 listados de KeyNodeReference. La combinaci�n la realiza tomando en orden la Key de los
	 * 3 listados de Keys y el NodeReference del listado de KeyNodeReference.
	 * La primer KeyNodeReference ser� la primer KeyNodeReference del listado de KeyNodeReference
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
	 * a un tama�o tal que thirdPart sea un tercio de la suma del tama�o de thirdPart y mainPart.
	 * Left indica si la tercera parte es la de la izquierda de mainPart o de su derecha.
	 * Para el c�lculo del tama�o de las partes se usa serializer.
	 * El parametro mainPartExtraSize se le suma al tama�o de mainPart al hacer los c�lculos.
	 */
	public static <T> void balanceThirdPart(List<T> thirdPart, List<T> mainPart, Serializer<List<T>> serializer, long mainPartExtraSize) {
		// Abstracci�n para facilitar el c�lculo de los tama�os y pasaje de elementos.
		EspecialListForThirdPart<T> eThirdPart = new EspecialListForThirdPart<T>(thirdPart, serializer, false);
		EspecialListForThirdPart<T> eMainPart = new EspecialListForThirdPart<T>(mainPart, serializer, true);
		
		long thirdPartSize, mainPartSize, optimalThirdPartSize;

		// Si tengo exactamente el 1/3 (poco probable), termin�...
		thirdPartSize = eThirdPart.size();
		mainPartSize = eMainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		if (thirdPartSize == optimalThirdPartSize) {
			return;
		}
		
		// En lo que sigue, adem�s de lo que dicen los comentarios, siempre verifico
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
			// Recalculo los tama�os de las partes (puesto que no puedo saber como serializa
			// el serializador).
			thirdPartSize = eThirdPart.size();
			mainPartSize = eMainPart.size() + mainPartExtraSize;
			optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		}
		// Tomo la diferencia actual con el tama�o �ptimo.
		long firstDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Paso un elemento al rev�s que antes.
		if (end.listSize() > 1) {
			end.giveOneElementTo(source);
		}
		// Tomo de nuevo la diferencia actual con el tama�o �ptimo.
		thirdPartSize = eThirdPart.size();
		mainPartSize = eMainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		long secondDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Si la primer diferencia era menor que la segunda vuelvo un paso atr�s, sino no hago nada.
		if (firstDifference < secondDifference) {
			source.giveOneElementTo(end);
		}
	}
}
