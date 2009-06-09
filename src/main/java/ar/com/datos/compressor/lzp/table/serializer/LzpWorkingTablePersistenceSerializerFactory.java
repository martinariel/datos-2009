package ar.com.datos.compressor.lzp.table.serializer;

import ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.compressor.lzp.table.LzpContext;
import ar.com.datos.compressor.lzp.table.LzpContextPosition;

public class LzpWorkingTablePersistenceSerializerFactory implements ElementAndKeyListSerializerFactory<LzpContextPosition, LzpContext>{
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory#createListElementSerializer()
	 */
	@Override
	public ListElementsSerializer<LzpContextPosition, LzpContext> createListElementSerializer() {
		return new LzpContextPositionListSerializer();
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory#createListKeySerializer()
	 */
	@Override
	public ListKeysSerializer<LzpContext> createListKeySerializer() {
		return new LzpContextListSerializer();
	}
	
}
