package ar.com.datos.indexer.lexic.serializer;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.OutputBuffer;
import ar.com.datos.indexer.lexic.LexicalData;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.exception.SerializerException;

public class LexicalSerializer implements Serializer<LexicalData> {
	protected static final LexicalCounterSerializer serializadorCantidad = new LexicalCounterSerializer();
	protected static final LexicalTermSerializer serializadorTermino = new LexicalTermSerializer();

	private Serializer<LexicalData> current;
	public LexicalSerializer() {
		setCurrentToTerm();
	}
	public void setCurrentToCounter() {
		this.current = serializadorCantidad;
	}
	public void setCurrentToTerm() {
		this.current = serializadorTermino;
	}
	@Override
	public void dehydrate(OutputBuffer output, LexicalData object) throws SerializerException {
		this.current.dehydrate(output, object);
	}
	@Override
	public long getDehydrateSize(LexicalData object) {
		return this.current.getDehydrateSize(object);
	}
	@Override
	public LexicalData hydrate(InputBuffer input) throws SerializerException {
		return this.current.hydrate(input);
	}
	
}
