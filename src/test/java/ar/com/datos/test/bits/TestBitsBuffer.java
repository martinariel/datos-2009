package ar.com.datos.test.bits;

import java.util.Iterator;

import ar.com.datos.bits.impl.InputBufferBitEmisor;
import ar.com.datos.bits.impl.OutputBufferBitReceiver;
import ar.com.datos.test.ExtendedTestCase;
import ar.com.datos.test.serializer.mock.OutputBufferTest;

/**
 * Tests para las clases {@link InputBufferBitEmisor} y {@link OutputBufferBitReceiver}.
 * 
 * @author fvalido
 */
public class TestBitsBuffer extends ExtendedTestCase {
	public void testRandomBits() {
		OutputBufferTest o = new OutputBufferTest();
		OutputBufferBitReceiver oBit = new OutputBufferBitReceiver(o);
		
		int size = 509;
		byte[] bits = new byte[size];
		for(int i = 0; i < size; i++){
			bits[i] = (byte)(Math.round(Math.random()));
			oBit.addBit(bits[i]);
		}
		oBit.close();
		
		InputBufferBitEmisor iBit = new InputBufferBitEmisor(o.getAsInputBuffer());
		Iterator<Byte> it = iBit.iterator();
		for (int i = 0; i < size; i++) {
			assertEquals((Byte)bits[i], it.next());
		}
	}
}
