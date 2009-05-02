package ar.com.datos.indexer.lexic;

public class LexicalTermData implements LexicalData {

	private String term;
	public LexicalTermData(String token) {
		this.term = token;
	}

	@Override
	public String toString() {
		return term;
	}
}
