package ar.com.datos.test.btree.sharp.mock.disk;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;


/**
 * Serializador para {@link TestKeyDisk}. Usado para tests de Arbol B# en disco.
 *
 * @author fvalido
 */
public class ListTestKeySerializer implements ListKeysSerializer<TestKeyDisk> {
	private CollectionSerializer<String> collectionSerializer;

	public ListTestKeySerializer() {
		this.collectionSerializer = new CollectionSerializer<String>(new StringSerializerDelimiter());
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, List<TestKeyDisk> object) {
		Collection<String> keyValues = new LinkedList<String>();
		Iterator<TestKeyDisk> it = object.iterator();
		while (it.hasNext()) {
			keyValues.add(it.next().getValue());
		}
		
		this.collectionSerializer.dehydrate(output, keyValues);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public List<TestKeyDisk> hydrate(InputBuffer input) {
		Collection<String> keyValues = this.collectionSerializer.hydrate(input);
		Iterator<String> it = keyValues.iterator();
		List<TestKeyDisk> returnValue = new LinkedList<TestKeyDisk>();
		while (it.hasNext()) {
			returnValue.add(new TestKeyDisk(it.next()));
		}
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(List<TestKeyDisk> object) {
		Collection<String> keyValues = new LinkedList<String>();
		Iterator<TestKeyDisk> it = object.iterator();
		while (it.hasNext()) {
			keyValues.add(it.next().getValue());
		}
		
		return this.collectionSerializer.getDehydrateSize(keyValues);
	}
}
