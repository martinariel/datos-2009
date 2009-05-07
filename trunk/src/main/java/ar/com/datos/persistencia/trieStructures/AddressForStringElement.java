package ar.com.datos.persistencia.trieStructures;

import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.trie.Element;

public class AddressForStringElement implements Element<StringKey, CharAtom> {

	private StringKey key;
	private OffsetAddress address;
	public AddressForStringElement(StringKey key, OffsetAddress address) {
		super();
		this.key = key;
		this.address = address;
	}
	@Override
	public StringKey getKey() {
		return this.key;
	}

	public OffsetAddress getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return this.address == null? "null" : this.address.toString();
	}
}
