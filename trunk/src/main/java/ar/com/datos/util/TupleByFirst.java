package ar.com.datos.util;



public class TupleByFirst<K extends Comparable<K>, V> extends Tuple<K, V> implements Comparable<TupleByFirst<K, V>> {
	private Tuple.FirstComparator<Tuple<K, V>, K> comparator;
	
	public TupleByFirst(K first, V second) {
		super(first, second);
		this.comparator = new Tuple.FirstComparator<Tuple<K,V>, K>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !TupleByFirst.class.isAssignableFrom(obj.getClass())) {
			return super.equals(obj);
		}
		
		TupleByFirst<K, V> o = (TupleByFirst<K, V>)obj;
		return this.first.equals(o.first);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.first.hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TupleByFirst<K, V> o) {
		return this.comparator.compare(this, o);
	}
	
}
