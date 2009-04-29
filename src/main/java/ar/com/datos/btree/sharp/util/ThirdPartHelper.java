package ar.com.datos.btree.sharp.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.elements.Element;
import ar.com.datos.btree.elements.Key;
import ar.com.datos.btree.sharp.node.AbstractInternalNode;
import ar.com.datos.btree.sharp.node.KeyNodeReference;
import ar.com.datos.btree.sharp.node.NodeReference;
import ar.com.datos.util.WrappedParam;

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
	 * Combina 3 listados de Keys con un listado de NodeReferences. 
	 * Con esa combinación configura los nodos pasados, además establece los valores para las claves
	 * que deben apuntar a center y a right en las overflowKey correspondiente.
	 */
	public static <E extends Element<K>, K extends Key> void combineKeysAndNodeReferences(List<NodeReference<E, K>> nodeReferences, 
							List<List<K>> keyParts, AbstractInternalNode<E, K> leftNode,
							AbstractInternalNode<E, K> centerNode, AbstractInternalNode<E, K> rightNode,
							WrappedParam<K> overflowKeyCenter, WrappedParam<K> overflowKeyRight) {
		List<AbstractInternalNode<E, K>> nodes = new LinkedList<AbstractInternalNode<E,K>>();
		nodes.add(leftNode);
		nodes.add(centerNode);
		nodes.add(rightNode);
		
		Iterator<NodeReference<E, K>>itNodeReference = nodeReferences.iterator();
		Iterator<K> itKey;
		AbstractInternalNode<E, K> currentNode;
		for (int i = 0; i < 3; i++) {
			currentNode = nodes.remove(0);
			itKey = keyParts.remove(0).iterator();
			
			currentNode.getKeysNodes().clear();
			currentNode.setFirstChild(itNodeReference.next());
		
			if (i == 1) {
				overflowKeyCenter.setValue(itKey.next());
			}
			if (i == 2) {
				overflowKeyRight.setValue(itKey.next());
			}
			while (itKey.hasNext()) {
				currentNode.getKeysNodes().add(new KeyNodeReference<E, K>(itKey.next(), itNodeReference.next()));
			}
		}
	}
	
//	FIXME No va más
//	/**
//	 * Combina 3 listados de Keys con un listado de KeyNodeReference, dejando como resultado
//	 * 3 listados de KeyNodeReference. La combinación la realiza tomando en orden la Key de los
//	 * 3 listados de Keys y el NodeReference del listado de KeyNodeReference.
//	 * La primer KeyNodeReference será la primer KeyNodeReference del listado de KeyNodeReference
//	 * sin combinar con el listado de Keys.
//	 * 
//	 * PRE: Sumatoria(keyParts(i).size()) == keyNodeReferences.size() - 1
//	 */
//	public static <E extends Element<K>, K extends Key> List<List<KeyNodeReference<E, K>>> combineKeysAndNodeReferences(List<KeyNodeReference<E, K>> keyNodeReferences, List<List<K>> keyParts) {
//		List<List<KeyNodeReference<E, K>>> returnValue = new LinkedList<List<KeyNodeReference<E,K>>>();
//		List<KeyNodeReference<E, K>> part; 
//		
//		Iterator<KeyNodeReference<E, K>>itKeyNodeReference = keyNodeReferences.iterator();
//		Iterator<K> itKey;
//		for (int i = 0; i < 3; i++) {
//			part = new LinkedList<KeyNodeReference<E,K>>();
//			if (i == 0) {
//				part.add(itKeyNodeReference.next());
//			}
//			itKey = keyParts.get(i).iterator();
//			while (itKey.hasNext()) {
//				part.add(new KeyNodeReference<E, K>(itKey.next(), itKeyNodeReference.next().getNodeReference()));
//			}
//			returnValue.add(part);
//		}
//		
//		return returnValue;
//	}
	
	/**
	 * Pasa elementos desde thirdPart hacia mainPart o viceversa hasta ajustarse lo mejor posible
	 * a un tamaño tal que thirdPart sea un tercio de la suma del tamaño de thirdPart y mainPart.
	 * El parametro mainPartExtraSize se le suma al tamaño de mainPart al hacer los cálculos.
	 * min*PartCount indica la cantidad mínima de elementos que puede tener la lista correspondiente.
	 */
	public static <T> void balanceThirdPart(EspecialListForThirdPart<T> thirdPart, EspecialListForThirdPart<T> mainPart, long mainPartExtraSize, int minThirdPartCount, int minMainPartCount) {
		long thirdPartSize, mainPartSize, optimalThirdPartSize;

		// Si tengo exactamente el 1/3 (poco probable), terminé...
		thirdPartSize = thirdPart.size();
		mainPartSize = mainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		if (thirdPartSize == optimalThirdPartSize) {
			return;
		}
		
		// En lo que sigue, además de lo que dicen los comentarios, siempre verifico
		// que en las partes quede al menos min*PartCount elementos.
		
		// Si es mayor que 1/3 paso elementos desde thirdPart hacia mainPart
		// hasta que deje de serlo. Sino la viceversa:
		EspecialListForThirdPart<T> source = thirdPart;
		int sourceMinCount = minThirdPartCount;
		EspecialListForThirdPart<T> end = mainPart;
		int endMinCount = minMainPartCount;
		boolean exceeds = (thirdPartSize > optimalThirdPartSize);
		if (!exceeds) {
			source = mainPart;
			sourceMinCount = minMainPartCount;
			end = thirdPart;
			endMinCount = minThirdPartCount;
		}
		while ((exceeds == (thirdPartSize > optimalThirdPartSize)) && source.listSize() > sourceMinCount) {
			source.giveOneElementTo(end);
			// Recalculo los tamaños de las partes (puesto que no puedo saber como serializa
			// el serializador).
			thirdPartSize = thirdPart.size();
			mainPartSize = mainPart.size() + mainPartExtraSize;
			optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		}
		// Tomo la diferencia actual con el tamaño óptimo.
		long firstDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Paso un elemento al revés que antes.
		if (end.listSize() > endMinCount) {
			end.giveOneElementTo(source);
		}
		// Tomo de nuevo la diferencia actual con el tamaño óptimo.
		thirdPartSize = thirdPart.size();
		mainPartSize = mainPart.size() + mainPartExtraSize;
		optimalThirdPartSize =  Math.round(((float)thirdPartSize + mainPartSize) / 3F);
		long secondDifference = Math.abs(optimalThirdPartSize - thirdPartSize);
		
		// Si la primer diferencia era menor que la segunda vuelvo un paso atrás, sino no hago nada.
		if (firstDifference < secondDifference) {
			source.giveOneElementTo(end);
		}
	}
}
