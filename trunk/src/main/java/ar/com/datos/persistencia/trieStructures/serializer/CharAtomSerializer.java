package ar.com.datos.persistencia.trieStructures.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.persistencia.trieStructures.CharAtom;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.CharacterSerializer;
import ar.com.datos.serializer.common.SerializerCache;
import ar.com.datos.serializer.exception.SerializerException;

public class CharAtomSerializer implements Serializer<CharAtom> {

	private CharacterSerializer charSerializer = SerializerCache.getInstance().getSerializer(CharacterSerializer.class);
	
	@Override
	public void dehydrate(OutputBuffer output, CharAtom object)	throws SerializerException {
		charSerializer.dehydrate(output, object.getCharacter());
	}

	@Override
	public long getDehydrateSize(CharAtom object) {
		return charSerializer.getDehydrateSize(object.getCharacter());
	}

	@Override
	public CharAtom hydrate(InputBuffer input) throws SerializerException {
		return new CharAtom(charSerializer.hydrate(input));
	}

}
