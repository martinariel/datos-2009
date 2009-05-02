package ar.com.datos.test.file.variableLength;

import java.util.List;

import ar.com.datos.file.variableLength.HydratedBlock;

public class HydratedBlockStub<T> extends HydratedBlock<T> {

	public HydratedBlockStub(List<T> datos, List<Long> bloquesOriginales, long l) {
		super(datos, bloquesOriginales, null);
		setNextBlockNumber(l);
	}

	public HydratedBlockStub(List<T> datos, long l, long l2) {
		super(datos, l, null);
		setNextBlockNumber(l2);
	}

	
}
