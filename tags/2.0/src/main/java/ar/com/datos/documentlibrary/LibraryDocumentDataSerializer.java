package ar.com.datos.documentlibrary;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.serializer.exception.SerializerException;

public class LibraryDocumentDataSerializer implements Serializer<LibraryData> {

	private StringSerializerDelimiter serializer = SerializerCache.getInstance().getSerializer(StringSerializerDelimiter.class);
	@Override
	public void dehydrate(OutputBuffer output, LibraryData object) throws SerializerException {
		Document document = ((LibraryDocumentData) object).getDocument();
		StringBuilder fileContent = new StringBuilder();
		document.close();
		document.open();
		boolean isEmpty = true;
		for (String linea = document.readLine(); linea != null; linea = document.readLine()) {
            fileContent.append(linea).append('\n');
            isEmpty = false;
        }
		document.close();
		serializer.dehydrate(output, (isEmpty)? "" : fileContent.substring(0, fileContent.length()-1));
	}

	@Override
	public long getDehydrateSize(LibraryData object) {
		Document document = (Document) object;
		document.close();
		document.open();
		Integer acumulado = 0;
		for (String linea = document.readLine(); linea != null; linea = document.readLine()) {
			// Utilizo el conocimiento de como funciona el String Delimiter, utiliza Unicode (2 bytes por caracter
			acumulado += linea.length() * 2 + 2;
		}
		document.close();
		// Y utiliza un único delimitador al final
		acumulado += serializer.getDelimiter().length;
		return 0;
	}

	@Override
	public LibraryData hydrate(InputBuffer input) throws SerializerException {
		MemoryDocument document = new MemoryDocument();
		
		String fileContent = serializer.hydrate(input);
		document.addLine(fileContent);
		return new LibraryDocumentData(document);
	}

}
