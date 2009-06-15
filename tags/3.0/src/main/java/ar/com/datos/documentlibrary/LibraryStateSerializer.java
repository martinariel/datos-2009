package ar.com.datos.documentlibrary;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

public class LibraryStateSerializer implements Serializer<LibraryData> {

	private Serializer<LibraryData> serializer;
	private Byte compressor = LibraryDocumentDataSerializer.NO_COMPRESSION;
	
	public LibraryStateSerializer() {
		setCurrentToDocument();
	}
	@Override
	public void dehydrate(OutputBuffer output, LibraryData object) throws SerializerException {
		this.serializer.dehydrate(output, object);
	}

	@Override
	public long getDehydrateSize(LibraryData object) {
		return this.serializer.getDehydrateSize(object);
	}

	@Override
	public LibraryData hydrate(InputBuffer input) throws SerializerException {
		return this.serializer.hydrate(input);
	}
	public void setCurrentToCounter() {
		this.serializer = new LibraryCounterDataSerializer();
	}

	public void setCurrentToDocument() {
		this.serializer = new LibraryDocumentDataSerializer();
		((LibraryDocumentDataSerializer)this.serializer).setCompressor(this.compressor);
	}
	
	public void setCompressor( Byte compressor){
		this.compressor = compressor;
	}
	

}
