package ar.com.datos.persistencia.trieStructures;

import java.util.ArrayList;
import java.util.List;

import ar.com.datos.trie.Key;

public class StringKey implements Key<CharAtom> {

	private String key;
	public StringKey(String key) {
		super();
		this.key = key;
	}

	@Override
	public CharAtom getKeyAtom(int level) {
		return new CharAtom(key.charAt(level));
	}

	@Override
	public List<CharAtom> getRestOfKey(int level) {
		List<CharAtom> restOfKey = new ArrayList<CharAtom>();
		for (Integer i = level; i < this.key.length(); i++)
			restOfKey.add(this.getKeyAtom(i));
		return restOfKey;
	}

}
