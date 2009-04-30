package ar.com.datos.indexer;

public class LexicalCounterData implements LexicalData {
	
	private Long currentCount;
	public LexicalCounterData(Long currentCount) {
		super();
		this.currentCount = currentCount;
	}
	public void increment() {
		this.currentCount ++;
	}
	public Long getCurrentCount() {
		return this.currentCount;
	}
}
