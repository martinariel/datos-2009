package ar.com.datos.documentlibrary;

import java.util.HashMap;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.compressor.lzp.LzpSerializer;
import ar.com.datos.compressor.ppmc.PPMCSerializer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.ByteSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.common.StringSerializerDelimiter;
import ar.com.datos.serializer.exception.SerializerException;

public class LibraryDocumentDataSerializer implements Serializer<LibraryData> {

	private HashMap<Byte,Serializer<Document>> compressorSerializers;
	private StringSerializerDelimiter serializer = SerializerCache.getInstance().getSerializer(StringSerializerDelimiter.class);
	private ByteSerializer serializerByte = SerializerCache.getInstance().getSerializer(ByteSerializer.class);
	
	private Byte currentSerializerIdentificator;
	
	public static final Byte NO_COMPRESSION = 0;
	public static final Byte LZP_IDENTIFICATOR  = 1;
	public static final Byte PPCM_IDENTIFICATOR = 2;
	
	public LibraryDocumentDataSerializer(){
		
		this.compressorSerializers = new HashMap<Byte,Serializer<Document>>();
		
		this.compressorSerializers.put( LZP_IDENTIFICATOR , new LzpSerializer());
		this.compressorSerializers.put( PPCM_IDENTIFICATOR , new PPMCSerializer());
		
		this.currentSerializerIdentificator = NO_COMPRESSION;
	}
	
	
	public void setCompressor ( Byte compressorIdentificator){
		this.currentSerializerIdentificator = compressorIdentificator;
	}
	
	
	private Serializer<Document> getCompressorSerializer() {
		return this.compressorSerializers.get(this.currentSerializerIdentificator);	
	}
	
	@Override
	public void dehydrate(OutputBuffer output, LibraryData object) throws SerializerException {
		Document document = ((LibraryDocumentData) object).getDocument();
		
		serializerByte.dehydrate(output, this.currentSerializerIdentificator);
		
		Serializer<Document> compressorSerializer = getCompressorSerializer();
		
		if (compressorSerializer == null) {
			
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
		else
		{
			compressorSerializer.dehydrate(output, document);
		}
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
		
		Document document;
		
		this.setCompressor(serializerByte.hydrate(input));
		
		Serializer<Document> compressorSerializer = this.getCompressorSerializer();
		
		if ( compressorSerializer == null){	
			String fileContent = serializer.hydrate(input);
			MemoryDocument memoryDocument = new MemoryDocument();
			memoryDocument.addLine(fileContent);
			document = memoryDocument;
		}
		else
		{
			document = compressorSerializer.hydrate(input);
		}
		
		return new LibraryDocumentData(document);
	}

}
