package ar.com.datos.indexer.serializer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import ar.com.datos.btree.sharp.impl.disk.interfaces.ListElementsSerializer;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.tree.IndexerTreeElement;
import ar.com.datos.indexer.tree.IndexerTreeKey;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.IntegerSerializer;
import ar.com.datos.serializer.common.SerializerCache;

public class ListIndexerTreeElementSerializer implements ListElementsSerializer<IndexerTreeElement<?>, IndexerTreeKey> {
	static {
		Serializer<OffsetAddress> addressInLexiconSerializer = SerializerCache.getInstance().getSerializer(OffsetAddressSerializer.class);
		if (addressInLexiconSerializer == null) SerializerCache.getInstance().addSerializer(new OffsetAddressSerializer());
		Serializer<BlockAddress<Long, Short>> dataCountAddress = SerializerCache.getInstance().getSerializer(VariableLengthAddressSerializer.class);
		if (dataCountAddress == null)  SerializerCache.getInstance().addSerializer(new VariableLengthAddressSerializer());
		FrontCodingSerializer fronCoderSerializer = SerializerCache.getInstance().getSerializer(FrontCodingSerializer.class);
		if (fronCoderSerializer == null) SerializerCache.getInstance().addSerializer(new FrontCodingSerializer());
	}
	private Serializer<OffsetAddress> addressInLexiconSerializer = SerializerCache.getInstance().getSerializer(OffsetAddressSerializer.class);
	private Serializer<BlockAddress<Long, Short>> dataCountAddress = SerializerCache.getInstance().getSerializer(VariableLengthAddressSerializer.class);
	private FrontCodingSerializer serializadorClaves = SerializerCache.getInstance().getSerializer(FrontCodingSerializer.class);
	private IntegerSerializer serializadorCantidad = SerializerCache.getInstance().getSerializer(IntegerSerializer.class);
	@SuppressWarnings("unchecked")
	@Override
	public void dehydrate(OutputBuffer output, List<IndexerTreeElement<?>> object) {
		this.serializadorClaves.dehydrate(output, (List<String>)CollectionUtils.collect(object, new Transformer(){

			@Override
			public Object transform(Object arg0) {
				return ((IndexerTreeElement<?>)arg0).getKey().getTerm();
			}
			
		}));
		for (IndexerTreeElement<?> treeElement : object) {
			this.addressInLexiconSerializer.dehydrate(output, treeElement.getAddressInLexicon());
			this.serializadorCantidad.dehydrate(output, treeElement.getNumberOfAssociatedData());
			this.dataCountAddress.dehydrate(output, treeElement.getDataCountAddress());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public long getDehydrateSize(List<IndexerTreeElement<?>> object) {
		Long acumulado = this.serializadorClaves.getDehydrateSize((List<String>)CollectionUtils.collect(object, new Transformer(){

			@Override
			public Object transform(Object arg0) {
				return ((IndexerTreeElement<?>)arg0).getKey().getTerm();
			}
			
		}));

		for (IndexerTreeElement<?> treeElement : object) {
			acumulado += this.addressInLexiconSerializer.getDehydrateSize(treeElement.getAddressInLexicon());
			acumulado += this.serializadorCantidad.getDehydrateSize(treeElement.getNumberOfAssociatedData());
			acumulado += this.dataCountAddress.getDehydrateSize(treeElement.getDataCountAddress());
		}
		return acumulado;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndexerTreeElement<?>> hydrate(InputBuffer input) {
		List<IndexerTreeElement<?>> collection = new ArrayList<IndexerTreeElement<?>>();
		List<String> b = this.serializadorClaves.hydrate(input);
		for (String termino : b) {
			IndexerTreeElement<?> treeElement = new IndexerTreeElement(new IndexerTreeKey(termino));
			treeElement.setAddressInLexicon(this.addressInLexiconSerializer.hydrate(input));
			treeElement.setNumberOfAssociatedData(this.serializadorCantidad.hydrate(input));
			treeElement.setDataCountAddress(this.dataCountAddress.hydrate(input));
			collection.add(treeElement);
		}

		return collection;
	}

}
