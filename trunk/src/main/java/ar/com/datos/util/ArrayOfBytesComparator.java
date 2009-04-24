package ar.com.datos.util;

import java.util.Comparator;

public class ArrayOfBytesComparator implements Comparator<byte[]> {

	@Override
	public int compare(byte[] arg0, byte[] arg1) {
		
		int index = 0;
		
		while (index < arg0.length && arg0[index] == arg1[index]) { index++; }

		return (index == arg0.length)? 0: new Byte(arg0[index]).compareTo(new Byte(arg1[index]));
	}

}
