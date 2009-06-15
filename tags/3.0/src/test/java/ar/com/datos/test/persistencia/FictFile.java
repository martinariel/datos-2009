package ar.com.datos.test.persistencia;

import java.io.IOException;
import java.util.Iterator;

import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.address.VariableLengthAddress;
import ar.com.datos.serializer.Serializable;


import java.util.LinkedList;



/**
 * Responde a la misma interfaz que los VariableLengthFileManager pero implementa
 * la "persistencia" en memoria.Se usa para testear el funcionamiento de la clase
 * SoundPersistenceService.
 * */


public class FictFile<T extends Serializable<T>> implements DynamicAccesor<BlockAddress<Long, Short>, T>{
	
	private LinkedList<T> lista = new LinkedList<T>();
	
	@Override
	public T get(BlockAddress<Long, Short> direccion) {
		return lista.get( direccion.getBlockNumber().intValue() );
	}

	@Override
	public BlockAddress<Long, Short> addEntity(T campos) {
		lista.add( campos );
		
		Long bk = new Long( lista.size()-1 );
		short ob = 0;

		return new VariableLengthAddress( bk, ob );
	}

	@Override
	public Iterator<T> iterator() {
		return lista.iterator();
	}

	@Override
	public Boolean isEmpty() {
		return lista.isEmpty();
	}

	@Override
	public void close() throws IOException {
		
	}

}
