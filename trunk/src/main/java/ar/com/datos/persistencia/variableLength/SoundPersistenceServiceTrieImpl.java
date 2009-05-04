package ar.com.datos.persistencia.variableLength;

import java.io.IOException;
import java.io.InputStream;

import ar.com.datos.audio.AnotherInputStream;
import ar.com.datos.persistencia.SoundPersistenceService;
import ar.com.datos.persistencia.exception.UnregisteredWordException;
import ar.com.datos.persistencia.exception.WordIsAlreadyRegisteredException;
import ar.com.datos.trie.Trie;

/**
 * TODO: SoundPersistenceService utilizando un Trie
 *
 * @author mfernandez
 *
 */
public class SoundPersistenceServiceTrieImpl implements SoundPersistenceService {


    public void addWord(String word, AnotherInputStream stream)
            throws WordIsAlreadyRegisteredException {
        // TODO Auto-generated method stub

    }

    public boolean isRegistered(String word) {
        // TODO Auto-generated method stub
        return false;
    }

    public InputStream readWord(String word) throws UnregisteredWordException {
        // TODO Auto-generated method stub
        return null;
    }

    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

}
