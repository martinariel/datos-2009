package ar.com.datos.util;

import java.util.Comparator;

public class Tuple<K, V> {
	protected K first;
	protected V second;

	public Tuple(K first, V second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public K getFirst() {
		return this.first;
	}
	public V getSecond() {
		return this.second;
	}
	public void setFirst(K first) {
		this.first = first;
	}
	public void setSecond(V second) {
		this.second = second;
	}
	
	/**
	 * Compara dos objects:
	 * - si son comparables y asignables entre si por el criterio natural (comparable).
	 * - si no, por hashCode.
	 */
	@SuppressWarnings("unchecked")
	private static int compareObjects(Object o1, Object o2) {
		int returnValue;
		if (Comparable.class.isAssignableFrom(o2.getClass()) &&
				(o1.getClass().isAssignableFrom(o2.getClass()) 
				 || o2.getClass().isAssignableFrom(o1.getClass()))) {
			Comparable o1c = (Comparable)o1;
			Comparable o2c = (Comparable)o2;
			returnValue = o1c.compareTo(o2c);
		} else {
			returnValue = (o1.hashCode() - o2.hashCode());
		}
		
		return returnValue;
	}
	
	/**
	 * Compara una tupla con otra usando first.
	 * El segundo criterio de orden es second, en caso de ser Comparable y asignables entre si (Sino
	 * usa como criterio secundario el hashCode).
	 */
	public static class FirstComparator<T extends Tuple<C, ?>, C extends Comparable<C>> implements Comparator<T> {
		public int compare(T o1, T o2) {
			int returnValue = o1.getFirst().compareTo(o2.getFirst());
			if (returnValue == 0) {
				returnValue = compareObjects(o1.getSecond(), o2.getSecond());
			}
			
			return returnValue;
		}
	}

	/**
	 * Compara una tupla con otra usando second.
	 * El segundo criterio de orden es first, en caso de ser Comparable y asignables entre si (Sino
	 * usa como criterio secundario el hashCode).
	 */
	public static class SecondComparator<T extends Tuple<?, C>, C extends Comparable<C>> implements Comparator<T> {
		public int compare(T o1, T o2) {
			int returnValue = o1.getSecond().compareTo(o2.getSecond());
			if (returnValue == 0) {
				returnValue = compareObjects(o1.getFirst(), o2.getFirst());
			}
			
			return returnValue;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("< ").append(getFirst()).append(" , ").append(getSecond()).append(" >");
		return sb.toString();
	}
}
