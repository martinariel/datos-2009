package ar.com.datos.test.trie;

import java.io.File;
import java.io.FileFilter;

import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.persistencia.trieStructures.AddressForStringElement;
import ar.com.datos.persistencia.trieStructures.CharAtom;
import ar.com.datos.persistencia.trieStructures.StringKey;
import ar.com.datos.persistencia.trieStructures.serializer.AddressForStringSerializer;
import ar.com.datos.persistencia.trieStructures.serializer.CharAtomSerializer;
import ar.com.datos.trie.Trie;
import ar.com.datos.trie.disk.DiskTrie;
import junit.framework.TestCase;

public class TestTrie extends TestCase {

	private static final String ARCHIVOS = "pepe";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// Limpio todos los archivos que genera el test
		File dir = new File("./");
		File[] files = dir.listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(ARCHIVOS);
			}
			
		});
		for (File f: files) f.delete();
	}

	public void testInicial() throws Exception {
		Trie<AddressForStringElement, StringKey, CharAtom> trie = 
			new DiskTrie<AddressForStringElement, StringKey, CharAtom>(ARCHIVOS,128,128, new AddressForStringSerializer(), new CharAtomSerializer());
		
		trie.addElement(new AddressForStringElement(new StringKey("p2"),new OffsetAddress(25L)));
		AddressForStringElement a = trie.findElement(new StringKey("p2"));
		assertEquals(25L, a.getAddress().getOffset().longValue());
		trie.addElement(new AddressForStringElement(new StringKey("abcdefg"),new OffsetAddress(55L)));
		AddressForStringElement b = trie.findElement(new StringKey("abcdefg"));
		assertEquals(55L, b.getAddress().getOffset().longValue());
		trie.addElement(new AddressForStringElement(new StringKey("p2aabc"),new OffsetAddress(89L)));
		AddressForStringElement c = trie.findElement(new StringKey("p2aabc"));
		assertEquals(89L, c.getAddress().getOffset().longValue());
		trie.close();
		trie = new DiskTrie<AddressForStringElement, StringKey, CharAtom>(ARCHIVOS,128,128, new AddressForStringSerializer(), new CharAtomSerializer());
		c = trie.findElement(new StringKey("p2aabc"));
		assertEquals(89L, c.getAddress().getOffset().longValue());
		
	}
}
