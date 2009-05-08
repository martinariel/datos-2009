package ar.com.datos.documentlibrary;

public class LibraryDocumentData implements LibraryData {

	private Document document;

	public LibraryDocumentData(Document document) {
		super();
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}
	
}
