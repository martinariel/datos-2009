package ar.com.datos.test.compressor;

import java.util.Iterator;

import junit.framework.TestCase;
import ar.com.datos.compressor.ProbabilityTableByFrequencies;
import ar.com.datos.compressor.SimpleSuperChar;
import ar.com.datos.compressor.SuperChar;
import ar.com.datos.util.Tuple;

public class TestProbabilityTableByFrequencies extends TestCase {

	public void testWithoutExclusion() {
		SuperChar lowestSuperChar = new SimpleSuperChar(0);
		SuperChar highestSuperChar = new SimpleSuperChar(10);
		ProbabilityTableByFrequencies probabilityTable = new ProbabilityTableByFrequencies(lowestSuperChar, highestSuperChar);
		
		assertEquals(11, probabilityTable.getNumberOfChars());
		assertEquals(11, probabilityTable.countCharsWithProbabilityUnder((double)1/5));		
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/11));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/100));
		Iterator<Tuple<SuperChar, Double>> it = probabilityTable.iterator();
		Tuple<SuperChar, Double> currentTuple;
		int i = lowestSuperChar.intValue();
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/11, currentTuple.getSecond());
			i++;
		}
		
		probabilityTable.addOccurrence(new SimpleSuperChar(5));
		assertEquals(11, probabilityTable.getNumberOfChars());
		assertEquals(11, probabilityTable.countCharsWithProbabilityUnder((double)1/5));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/12));
		assertEquals(10, probabilityTable.countCharsWithProbabilityUnder((double)2/12));
		it = probabilityTable.iterator();
		currentTuple = it.next();
		assertEquals(new SimpleSuperChar(5), currentTuple.getFirst());
		assertEquals((double)2/12, currentTuple.getSecond());
		i = lowestSuperChar.intValue();;
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/12, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i++;
			}
		}
		
		probabilityTable.addOccurrence(new SimpleSuperChar(20));
		assertEquals(12, probabilityTable.getNumberOfChars());
		assertEquals(12, probabilityTable.countCharsWithProbabilityUnder((double)1/5));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/13));
		assertEquals(11, probabilityTable.countCharsWithProbabilityUnder((double)2/13));
		it = probabilityTable.iterator();
		currentTuple = it.next();
		assertEquals(new SimpleSuperChar(5), currentTuple.getFirst());
		assertEquals((double)2/13, currentTuple.getSecond());
		currentTuple = it.next();
		assertEquals(new SimpleSuperChar(20), currentTuple.getFirst());
		assertEquals((double)1/13, currentTuple.getSecond());
		i = lowestSuperChar.intValue();;
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/13, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i++;
			}
		}
	}

	public void testWithExclusion() {
		SuperChar lowestSuperChar = new SimpleSuperChar(0);
		SuperChar highestSuperChar = new SimpleSuperChar(10);
		ProbabilityTableByFrequencies probabilityTable = new ProbabilityTableByFrequencies(lowestSuperChar, highestSuperChar);
		
		probabilityTable.addToExcludedSet(new SimpleSuperChar(5));
		assertEquals(10, probabilityTable.getNumberOfChars());
		assertEquals(10, probabilityTable.countCharsWithProbabilityUnder((double)1/5));		
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/10));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/100));
		Iterator<Tuple<SuperChar, Double>> it = probabilityTable.iterator();
		Tuple<SuperChar, Double> currentTuple;
		int i = lowestSuperChar.intValue();
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/10, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i++;
			}
		}

		probabilityTable.addToExcludedSet(new SimpleSuperChar(20));
		assertEquals(10, probabilityTable.getNumberOfChars());
		assertEquals(10, probabilityTable.countCharsWithProbabilityUnder((double)1/5));		
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/10));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/100));
		it = probabilityTable.iterator();
		i = lowestSuperChar.intValue();
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/10, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i++;
			}
		}
		
		probabilityTable.addOccurrence(new SimpleSuperChar(5));
		probabilityTable.addOccurrence(new SimpleSuperChar(6));
		assertEquals(10, probabilityTable.getNumberOfChars());
		assertEquals(10, probabilityTable.countCharsWithProbabilityUnder((double)1/5));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/11));
		assertEquals(9, probabilityTable.countCharsWithProbabilityUnder((double)2/11));
		it = probabilityTable.iterator();
		currentTuple = it.next();
		assertEquals(new SimpleSuperChar(6), currentTuple.getFirst());
		assertEquals((double)2/11, currentTuple.getSecond());
		i = lowestSuperChar.intValue();;
		while (it.hasNext()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/11, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i += 2;
			}
		}
	}

	public void testESC() {
		SuperChar lowestSuperChar = new SimpleSuperChar(0);
		SuperChar highestSuperChar = new SimpleSuperChar(10);
		ProbabilityTableByFrequencies probabilityTable = new ProbabilityTableByFrequencies(lowestSuperChar, highestSuperChar);
		
		probabilityTable.addOccurrence(SuperChar.ESC);
		probabilityTable.addOccurrence(SuperChar.ESC);
		probabilityTable.addOccurrence(SuperChar.ESC);
		probabilityTable.addOccurrence(SuperChar.EOF);
		probabilityTable.addOccurrence(new SimpleSuperChar(5));
		probabilityTable.addOccurrence(new SimpleSuperChar(5));
		probabilityTable.addOccurrence(new SimpleSuperChar(5));
		assertEquals(13, probabilityTable.getNumberOfChars());
		assertEquals(13, probabilityTable.countCharsWithProbabilityUnder((double)1/2));		
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/18));
		assertEquals(11, probabilityTable.countCharsWithProbabilityUnder((double)2/18));
		assertEquals(11, probabilityTable.countCharsWithProbabilityUnder((double)3/18));
		assertEquals(12, probabilityTable.countCharsWithProbabilityUnder((double)4/18));
		assertEquals(13, probabilityTable.countCharsWithProbabilityUnder((double)5/18));
		assertEquals(0, probabilityTable.countCharsWithProbabilityUnder((double)1/100));
		Iterator<Tuple<SuperChar, Double>> it = probabilityTable.iterator();
		Tuple<SuperChar, Double> currentTuple;
		currentTuple = it.next();
		assertEquals(new SimpleSuperChar(5), currentTuple.getFirst());
		assertEquals((double)4/18, currentTuple.getSecond());
		currentTuple = it.next();
		assertEquals(SuperChar.EOF, currentTuple.getFirst());
		assertEquals((double)1/18, currentTuple.getSecond());
		int i = lowestSuperChar.intValue();
		while (i <= highestSuperChar.intValue()) {
			currentTuple = it.next();
			assertEquals(new SimpleSuperChar(i), currentTuple.getFirst());
			assertEquals((double)1/18, currentTuple.getSecond());
			i++;
			if (i == 5) {
				i++;
			}
		}
		currentTuple = it.next();
		assertEquals(SuperChar.ESC, currentTuple.getFirst());
		assertEquals((double)3/18, currentTuple.getSecond());
	}
}
