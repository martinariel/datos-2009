package ar.com.datos.util;

import java.util.Comparator;

public class Tuple<K, V> {
	private K first;
	private V second;

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
	 * Compara una tupla con otra usando first.
	 */
	public static class FirstComparator<T extends Tuple<C, ?>, C extends Comparable<C>> implements Comparator<T> {
		public int compare(T o1, T o2) {
			return o1.getFirst().compareTo(o2.getFirst());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("< ").append(getFirst()).append(" , ").append(getSecond()).append(" >");
		return sb.toString();
	}
}
