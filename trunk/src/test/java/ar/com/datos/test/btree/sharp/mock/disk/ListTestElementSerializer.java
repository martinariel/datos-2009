package ar.com.datos.test.btree.sharp.mock.disk;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;

/**
 * Serializador para {@link TestElementDisk}. Usado para tests de Arbol B# en disco.
 *
 * @author fvalido
 */
public class ListTestElementSerializer implements ListElementsSerializer<TestElementDisk, TestKeyDisk> {
	private IntegerSerializer integerSerializer;
	private ListTestKeySerializer listTestKeySerializer;
	
	public ListTestElementSerializer() {
		this.integerSerializer = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
		this.listTestKeySerializer = new ListTestKeySerializer();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	public void dehydrate(OutputBuffer output, List<TestElementDisk> object) {
		List<TestKeyDisk> keys = new LinkedList<TestKeyDisk>();
		Iterator<TestElementDisk> it = object.iterator();
		
		while (it.hasNext()) {
			keys.add(it.next().getKey());
		}
		
		this.listTestKeySerializer.dehydrate(output, keys);
		
		it = object.iterator();
		while (it.hasNext()) {
			this.integerSerializer.dehydrate(output, it.next().getPosition());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	public List<TestElementDisk> hydrate(InputBuffer input) {
		List<TestKeyDisk> keys = this.listTestKeySerializer.hydrate(input);
		List<TestElementDisk> returnValue = new LinkedList<TestElementDisk>();
		
		Iterator<TestKeyDisk> it = keys.iterator();
		while (it.hasNext()) {
			returnValue.add(new TestElementDisk(it.next().getValue(), this.integerSerializer.hydrate(input)));
		}
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	public long getDehydrateSize(List<TestElementDisk> object) {
		List<TestKeyDisk> keys = new LinkedList<TestKeyDisk>();
		Iterator<TestElementDisk> it = object.iterator();
		
		while (it.hasNext()) {
			keys.add(it.next().getKey());
		}
		
		return this.listTestKeySerializer.getDehydrateSize(keys) + keys.size() * this.integerSerializer.getDehydrateSize(null);
	}
}
