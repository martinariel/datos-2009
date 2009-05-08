package ar.com.datos.documentlibrary;

import java.io.IOException;

import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;

/**
 * Libreria de documentos
 * 
 */
public class DocumentLibrary {
	private StraightVariableLengthFile<LibraryData> documentFile;
	private boolean isClosed;
	private LibraryStateSerializer serializer;
	public DocumentLibrary(String fileName){
		this.serializer = new LibraryStateSerializer();
		this.documentFile = new StraightVariableLengthFile<LibraryData>(fileName, this.serializer);
		this.isClosed = false;
	}
	
	/**
	 * Devuelve un documento
	 * @param offset
	 * @return Documento
	 */
	public Document get(OffsetAddress offset){
		if (this.isClosed) {
			throw new RuntimeException();
		}
		
		return ((LibraryDocumentData)this.documentFile.get(offset)).getDocument();
		
	}
	
	/**
	 * Agrega un documento a la libreria devolviendo su offset
	 * @param document
	 * @return OffsetAdress
	 */
	public OffsetAddress add(Document document){
		if (this.isClosed) {
			throw new RuntimeException();
		}
		this.incrementDocumentCounter();
		return documentFile.addEntity(new LibraryDocumentData(document));
	}
	
	protected void incrementDocumentCounter() {
		getSerializer().setCurrentToCounter();
		try {
			OffsetAddress counterAddress = new OffsetAddress(0L);
			LibraryCounterData lcd = (LibraryCounterData) this.documentFile.get(counterAddress);
			lcd.increment();
			this.documentFile.updateEntity(counterAddress, lcd);
		} catch (OutOfBoundsException obe) {
			this.documentFile.addEntity(new LibraryCounterData(1L));
		}
		getSerializer().setCurrentToDocument();
		
	}
	
	public void close() {
		if (this.isClosed) {
			throw new RuntimeException();
		}
		
		this.isClosed = true;
		try {
			this.documentFile.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isClosed() {
		return this.isClosed;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (!this.isClosed) {
			close();
		}
	}

	public Long getNumberOfDocuments() {
		try {
			getSerializer().setCurrentToCounter();
			OffsetAddress counterAddress = new OffsetAddress(0L);
			LibraryCounterData lcd = (LibraryCounterData) this.documentFile.get(counterAddress);
			return lcd.getCurrentCount();
		} catch (OutOfBoundsException obe) {
			return 0L;
		} finally {
			getSerializer().setCurrentToDocument();
		}
	}

	protected LibraryStateSerializer getSerializer() {
		return this.serializer;
	}
	
}