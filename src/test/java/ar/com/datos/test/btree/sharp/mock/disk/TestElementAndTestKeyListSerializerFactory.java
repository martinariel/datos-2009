package ar.com.datos.test.btree.sharp.mock.disk;

import ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;

public class TestElementAndTestKeyListSerializerFactory implements ElementAndKeyListSerializerFactory<TestElementDisk, TestKeyDisk> {
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory#createListElementSerializer()
	 */
	public ListElementsSerializer<TestElementDisk, TestKeyDisk> createListElementSerializer() {
		return new ListTestElementSerializer();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory#createListKeySerializer()
	 */
	public ListKeysSerializer<TestKeyDisk> createListKeySerializer() {
		return new ListTestKeySerializer();
	}

}
