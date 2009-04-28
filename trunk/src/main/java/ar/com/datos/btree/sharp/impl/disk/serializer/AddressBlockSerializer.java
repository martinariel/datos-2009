package ar.com.datos.btree.sharp.impl.disk.serializer;

import ar.com.datos.btree.sharp.BTreeSharp;
import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.LongSerializer;
import ar.com.datos.serializer.common.SerializerCache;

/**
 * Serializador de {@link Address} que ignora el número de objeto (lo maneja como 0).
 * Esto es usado por el {@link BTreeSharp} en disco puesto que en él un nodo es igual
 * a un bloque (es decir hay un solo nodo por bloque).
 * 
 * @author fvalido
 */
public class AddressBlockSerializer implements Serializer<BlockAddress<Long, Short>> {
	/** Serializador de Longs */
	private LongSerializer longSerializer;
	
	/**
	 * Al levantar la clase se inserta una instancia en el Cache de Serializers.
	 */
	static {
		SerializerCache.getInstance().addSerializer(new AddressBlockSerializer());
	}

	/**
	 * Permite crear un {@link AddressBlockSerializer}.
	 */
	public AddressBlockSerializer() {
		this.longSerializer = SerializerCache.getInstance().getSerializer(LongSerializer.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#dehydrate(ar.com.datos.buffer.OutputBuffer, java.lang.Object)
	 */
	@Override
	public void dehydrate(OutputBuffer output, BlockAddress<Long, Short> object) {
		Long blockNumber = -1L;
		if (object != null) {
			blockNumber = object.getBlockNumber();
		}
		this.longSerializer.dehydrate(output, blockNumber);
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#hydrate(ar.com.datos.buffer.InputBuffer)
	 */
	@Override
	public BlockAddress<Long, Short> hydrate(InputBuffer input) {
		Long blockNumber = this.longSerializer.hydrate(input);

		BlockAddress<Long, Short> returnValue = null;
		if (blockNumber != -1) {
			returnValue = new VariableLengthAddress(blockNumber, (short)0);
		}
		
		return returnValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.serializer.Serializer#getDehydrateSize(java.lang.Object)
	 */
	@Override
	public long getDehydrateSize(BlockAddress<Long, Short> object) {
		Long blockNumber = -1L;
		if (object != null) {
			blockNumber = object.getBlockNumber();
		}
		return this.longSerializer.getDehydrateSize(blockNumber);
	}
}
