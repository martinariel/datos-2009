package ar.com.datos.indexer.serializer;

import ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.indexer.tree.IndexerTreeElement;
import ar.com.datos.indexer.tree.IndexerTreeKey;

public class IndexerSerializerFactory implements ElementAndKeyListSerializerFactory<IndexerTreeElement<?>, IndexerTreeKey> {
	
	@Override
	public ListElementsSerializer<IndexerTreeElement<?>, IndexerTreeKey> createListElementSerializer() {
		return new ListIndexerTreeElementSerializer();
	}

	@Override
	public ListKeysSerializer<IndexerTreeKey> createListKeySerializer() {
		return new ListIndexerTreeKeySerializer();
	}


}
