package ar.com.datos.documentlibrary;

import ar.com.datos.file.variableLength.address.OffsetAddress;

/**
 * Implementaci�n de {@link Document} para documentos de {@link DocumentLibrary}.
 * Es Lazy hasta que se usa el m�todo open().
 *
 * @author fvalido
 */
public class DocumentFromDocumentLibrary extends Document {
    private DocumentLibrary documentLibrary;
    private OffsetAddress offsetAddress;
    private Document realDocument;

    /**
     * Construye una instancia.
     *
     * @param documentLibrary
     * {@link DocumentLibrary} desde donde obtener el documento.
     * @param offsetAddress
     * Posici�n del documento dentro de la {@link DocumentLibrary}.
     */
    public DocumentFromDocumentLibrary(DocumentLibrary documentLibrary, OffsetAddress offsetAddress) {
        this.offsetAddress = offsetAddress;
        this.documentLibrary = documentLibrary;
    }

    /*
     * (non-Javadoc)
     * @see ar.com.datos.documentlibrary.Document#canOpen()
     */
    public boolean canOpen() {
        return !this.documentLibrary.isClosed() || (this.realDocument != null && this.realDocument.canOpen());
    }

    /*
     * (non-Javadoc)
     * @see ar.com.datos.documentlibrary.Document#close()
     */
    public void close() {
        if (this.realDocument != null) {
            this.realDocument.close();
        }
        // Nota: Tendr�a que haber un m�todo que lo saque del cache ??
    }

    /*
     * (non-Javadoc)
     * @see ar.com.datos.documentlibrary.Document#open()
     */
    public void open() {
        if (this.realDocument == null) {
            this.realDocument = this.documentLibrary.get(this.offsetAddress);
        }
        this.realDocument.open();
    }

    /*
     * (non-Javadoc)
     * @see ar.com.datos.documentlibrary.Document#readLine()
     */
    public String readLine() {
        return this.realDocument.readLine();
    }

    @Override
    protected SizeKnowerDocumentReadable getMultipleReadableDocument() {
        return this.realDocument.getMultipleReadableDocument();
    }

	@Override
	public void addLine(String linea) {
		this.realDocument.addLine(linea);
	}

}
