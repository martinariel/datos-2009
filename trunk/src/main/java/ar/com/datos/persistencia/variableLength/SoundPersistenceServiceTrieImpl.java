package ar.com.datos.persistencia.variableLength;

import java.io.IOException;
import java.io.InputStream;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.StraightVariableLengthFile;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.persistencia.trieStructures.AddressForStringElement;
import ar.com.datos.persistencia.trieStructures.CharAtom;
import ar.com.datos.persistencia.trieStructures.StringKey;
import ar.com.datos.persistencia.trieStructures.serializer.AddressForStringSerializer;
import ar.com.datos.persistencia.trieStructures.serializer.CharAtomSerializer;
import ar.com.datos.persistencia.variableLength.registros.RegistroInputStream;
import ar.com.datos.trie.Trie;
import ar.com.datos.trie.disk.DiskTrie;

/**
 * SoundPersistenceService utilizando un Trie
 *
 * @author mfernandez
 *
 */
public class SoundPersistenceServiceTrieImpl implements SoundPersistenceService {

	private static final int INNER_BLOCK_SIZE = 128;
	private static final int LEAF_BLOCK_SIZE = 256;
	private Trie<AddressForStringElement, StringKey, CharAtom> wordsWithAudio;
	private DynamicAccesor<OffsetAddress, RegistroInputStream> audioData;
	/**
	 * Constructor con argumentos.Permite definir el path de los archivos
	 * en donde se guardan, y de los cuales se obtienen, palabras y sonidos.
	 * */
	
	public SoundPersistenceServiceTrieImpl(String wordsFileName, String audioDataFileName){

		audioData = new StraightVariableLengthFile<RegistroInputStream>(audioDataFileName,RegistroInputStream.getSerializerStatic());
		wordsWithAudio = new DiskTrie<AddressForStringElement, StringKey, CharAtom>(audioDataFileName, INNER_BLOCK_SIZE, LEAF_BLOCK_SIZE, new AddressForStringSerializer(), new CharAtomSerializer());
		
	}


    public void addWord(String word, AnotherInputStream stream) throws WordIsAlreadyRegisteredException {
    	// XXX: Tener en cuenta que si la palabra ya está registrada no tira excepción.... solo para hacer mas rápido
    	wordsWithAudio.addElement(new AddressForStringElement(new StringKey(word),audioData.addEntity(new RegistroInputStream(stream))));
    }

    public boolean isRegistered(String word) {
    	return isDataValid(getWord(word));
    }

    protected boolean isDataValid(AddressForStringElement data) {
    	return data != null && data.getAddress() != null;
    }

	private AddressForStringElement getWord(String word) {
		return wordsWithAudio.findElement(new StringKey(word));
	}

    public InputStream readWord(String word) throws UnregisteredWordException {
    	AddressForStringElement wordData = getWord(word);
    	if (!isDataValid(wordData)) throw new UnregisteredWordException();
        return audioData.get(wordData.getAddress()).getStream();
    }

    public void close() throws IOException {
        audioData.close();
        wordsWithAudio.close();
    }

}
