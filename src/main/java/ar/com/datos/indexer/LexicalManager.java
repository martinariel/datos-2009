package ar.com.datos.indexer;

import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

public class LexicalManager {

	private DynamicAccesor<OffsetAddress, String> lexical;
	private StringSerializerDelimiter serializer = new StringSerializerDelimiter();
	public LexicalManager(String fileName) {
		this.lexical = constructFile(fileName);
	}
	public String get(OffsetAddress address) {
		return this.lexical.get(address);
	}
	public OffsetAddress add(String token) {
		return this.lexical.addEntity(token);
	}
	protected DynamicAccesor<OffsetAddress, String> constructFile(String fileName) {
		return new StraightVariableLengthFile<String>(fileName, this.serializer);
	}
}
