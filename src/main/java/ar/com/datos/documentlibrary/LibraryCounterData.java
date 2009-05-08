package ar.com.datos.documentlibrary;

public class LibraryCounterData implements LibraryData {

	private Long contador;
	public LibraryCounterData(Long contador) {
		this.contador = contador;
	}

	public Long getCurrentCount() {
		return contador;
	}

	public void increment() {
		contador++;
	}

}
