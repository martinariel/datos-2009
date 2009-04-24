package ar.com.datos.indexer.keywordIndexer;

public class KeyCount<T> {
	private T key;
	private Integer count;
	
	public KeyCount(T key, Integer count) {
		super();
		this.key = key;
		this.count = count;
	}

	public T getKey() {
		return key;
	}

	public Integer getCount() {
		return count;
	}

}
