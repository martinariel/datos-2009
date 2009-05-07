package ar.com.datos.persistencia.trieStructures;

import ar.com.datos.trie.KeyAtom;

public class CharAtom implements KeyAtom {

	private Character character;
	public CharAtom(Character character) {
		super();
		this.character = character;
	}

	@Override
	public int compareTo(KeyAtom arg0) {
		CharAtom other = (CharAtom) arg0;
		return this.character.compareTo(other.character);
	}

	public Character getCharacter() {
		return character;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((character == null) ? 0 : character.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CharAtom other = (CharAtom) obj;
		if (character == null) {
			if (other.character != null)
				return false;
		} else if (!character.equals(other.character))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.character.toString();
	}
}
