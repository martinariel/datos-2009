package ar.com.datos.file.variableLength;

import java.util.Collection;
import java.util.Iterator;

import ar.com.datos.file.Address;
import ar.com.datos.file.BlockFile;
import ar.com.datos.file.BlockFileImpl;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.test.file.variableLength.Serializer2;

public class VariableLengthFileManager implements DynamicAccesor, BufferRealeaser {

	public VariableLengthFileManager(String nombreArchivo, Integer blockSize, Serializer2 serializador) {
		
	}
	@Override
	public Address addEntity(Collection<Object> campos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<Collection<Object>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> get(Address direccion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release(OutputBuffer ob) {
		// TODO Auto-generated method stub
		
	}
	public BlockFile constructFile(String nombreArchivo, Integer blockSize) {
		return new BlockFileImpl(nombreArchivo, blockSize);
	}
}
