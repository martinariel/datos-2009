package ar.com.datos.test.persistencia;

import java.util.Iterator;

import ar.com.datos.file.Address;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.variableLength.VariableLengthAddress;


import java.util.LinkedList;

public class FictFile<T> implements DynamicAccesor<T>{
	
	private LinkedList<T> lista = new LinkedList<T>();
	
	@Override
	public T get(Address<Long, Short> direccion) {
		
		return lista.get( direccion.getBlockNumber().intValue() );
	}

	@Override
	public Address<Long, Short> addEntity(T campos) {
		lista.add( campos );
		
		Long bk = new Long( lista.size()-1 );
		short ob = 0;

		return new VariableLengthAddress( bk, ob );
	}

	@Override
	public Iterator<T> iterator() {
		return lista.iterator();
	}

}
