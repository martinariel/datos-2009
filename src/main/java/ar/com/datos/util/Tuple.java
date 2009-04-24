package ar.com.datos.util;

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
}
