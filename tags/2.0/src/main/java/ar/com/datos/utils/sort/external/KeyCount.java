package ar.com.datos.utils.sort.external;

import ar.com.datos.util.Tuple;

public class KeyCount<T> extends Tuple<T, Integer> {
	
	public KeyCount(T key, Integer count) {
		super(key, count);
	}

	public T getKey() {
		return getFirst();
	}

	public Integer getCount() {
		return getSecond();
	}

}
