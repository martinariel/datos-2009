package ar.com.datos.indexer.serializer;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListKeysSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.indexer.tree.IndexerTreeKey;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.StringSerializerDelimiter;

public class ListIndexerTreeKeySerializer implements ListKeysSerializer<IndexerTreeKey> {

	private CollectionSerializer<String> serializer;
	public ListIndexerTreeKeySerializer() {
		super();
		this.serializer = new CollectionSerializer<String>(new StringSerializerDelimiter());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dehydrate(OutputBuffer output, List<IndexerTreeKey> object) {
		this.serializer.dehydrate(output, (List<String>)CollectionUtils.collect(object, new Transformer() {

			@Override
			public Object transform(Object indexerTreeKey) {
				return ((IndexerTreeKey)indexerTreeKey).getTerm();
			}
			}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getDehydrateSize(List<IndexerTreeKey> object) {
		return this.serializer.getDehydrateSize((List<String>)CollectionUtils.collect(object, new Transformer() {

			@Override
			public Object transform(Object indexerTreeKey) {
				return ((IndexerTreeKey)indexerTreeKey).getTerm();
			}
			}));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndexerTreeKey> hydrate(InputBuffer input) {
		return (List<IndexerTreeKey>)CollectionUtils.collect(this.serializer.hydrate(input), new Transformer() {

			@Override
			public Object transform(Object string) {
				return new IndexerTreeKey((String) string);
			}
			});
	}
}
