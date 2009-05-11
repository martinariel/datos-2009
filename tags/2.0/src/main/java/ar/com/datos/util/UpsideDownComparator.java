package ar.com.datos.util;

import java.util.Comparator;

public class UpsideDownComparator<T> implements Comparator<T> {
	private Comparator<T> realComparator;
	
	public UpsideDownComparator(Comparator<T> realComparator) {
		this.realComparator = realComparator;
	}

	public int compare(T o1, T o2) {
		return this.realComparator.compare(o2, o1);
	}

}
