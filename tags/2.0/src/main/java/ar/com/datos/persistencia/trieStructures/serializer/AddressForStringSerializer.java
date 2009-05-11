package ar.com.datos.persistencia.trieStructures.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.persistencia.trieStructures.AddressForStringElement;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.exception.SerializerException;

public class AddressForStringSerializer implements NullableSerializer<AddressForStringElement> {

	private OffsetAddressSerializer addressSerializer = new OffsetAddressSerializer();
	@Override
	public void dehydrate(OutputBuffer output, AddressForStringElement object) throws SerializerException {
		if (object == null) {
			this.dehydrateNull(output);
			return;
		}
		addressSerializer.dehydrate(output, object.getAddress());
	}

	@Override
	public long getDehydrateSize(AddressForStringElement object) {
		if (object == null) return addressSerializer.getDehydrateSize(null);
		return addressSerializer.getDehydrateSize(object.getAddress());
	}

	@Override
	public AddressForStringElement hydrate(InputBuffer input) throws SerializerException {
		return new AddressForStringElement(null, addressSerializer.hydrate(input));
	}

	@Override
	public void dehydrateNull(OutputBuffer buffer) {
		this.addressSerializer.dehydrateNull(buffer);
	}

}
