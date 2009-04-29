package ar.com.datos.file.variableLength;

import ar.com.datos.file.exception.NullableSerializerRequiredException;
import ar.com.datos.persistencia.variableLength.BlockWriter;
import ar.com.datos.persistencia.variableLength.ReplaceResponsable;
import ar.com.datos.serializer.NullableSerializer;
import ar.com.datos.serializer.Serializer;

public class ReplaceResponsableWithNull<T> implements ReplaceResponsable {

	private Short replaceEntity;
	private Boolean replacedOccurred = false;
	private Serializer<T> serializador;

	public ReplaceResponsableWithNull(Short objectNumber, Serializer<T> serializador) {
		this.replaceEntity = objectNumber;
		setSerializador(serializador);
	}

	@Override
	public void notifyExceed(BlockWriter blockWriter) {
		
		if (!(getSerializador() instanceof NullableSerializer)) throw new NullableSerializerRequiredException();
		NullableSerializer<T> serializer = (NullableSerializer<T>) getSerializador();

		serializer.dehydrateNull(blockWriter);
		blockWriter.closeEntity();

		replacedOccurred = true;
	}

	public Boolean hasReplacedOccurred() {
		return replacedOccurred;
	}

	@Override
	public Short replaceObjectNumber() {
		return replaceEntity;
	}

	protected Serializer<T> getSerializador() {
		return serializador;
	}

	protected void setSerializador(Serializer<T> serializador) {
		this.serializador = serializador;
	}

}
