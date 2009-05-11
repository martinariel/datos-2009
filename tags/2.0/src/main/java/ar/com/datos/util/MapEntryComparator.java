package ar.com.datos.util;

import java.util.Comparator;
import java.util.Map;

public class MapEntryComparator {
	// Sin constructor.
	private MapEntryComparator() {}
	
	public static class ByKey<E extends Map.Entry<? extends Comparable, ?>> implements Comparator<E> {
		@SuppressWarnings("unchecked")
		public int compare(E o1, E o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	}
	public static class ByValue<E extends Map.Entry<?, ? extends Comparable>> implements Comparator<E> {
		@SuppressWarnings("unchecked")
		public int compare(E o1, E o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	}
}
