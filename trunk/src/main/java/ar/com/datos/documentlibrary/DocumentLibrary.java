package ar.com.datos.documentlibrary;

import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

/**
 * Libreria de documentos
 * 
 */
public class DocumentLibrary {
	
	private StraightVariableLengthFile<String> documentFile;
	
	public DocumentLibrary(String fileName){
		documentFile = new StraightVariableLengthFile<String>(fileName, new StringSerializerDelimiter());
	}
	
	/**
	 * Devuelve un documento
	 * @param offset
	 * @return Documento
	 */
	public Document get(OffsetAddress offset){
		
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
		
		StringBuilder fileContent = new StringBuilder();
		document.close();
		document.open();
		for (String linea = document.readLine(); linea != null; linea = document.readLine()) {
            fileContent.append(linea);
        }
		document.close();
		
		return documentFile.addEntity(fileContent.toString());
	}
}
