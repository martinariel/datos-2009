package ar.com.datos.documentlibrary;

import java.io.IOException;

import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

/**
 * Libreria de documentos
 * 
 */
public class DocumentLibrary {
	private StraightVariableLengthFile<String> documentFile;
	private boolean isClosed;
	
	public DocumentLibrary(String fileName){
		this.documentFile = new StraightVariableLengthFile<String>(fileName, new StringSerializerDelimiter());
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
		
		MemoryDocument document = new MemoryDocument();
		
		String fileContent = documentFile.get(offset);
		document.addLine(fileContent);
		
		return document;
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
		
		StringBuilder fileContent = new StringBuilder();
		document.close();
		document.open();
		for (String linea = document.readLine(); linea != null; linea = document.readLine()) {
            fileContent.append(linea);
        }
		document.close();
		
		return documentFile.addEntity(fileContent.toString());
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
}
