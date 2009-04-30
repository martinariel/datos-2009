package ar.com.datos.file.variableLength;

import java.io.IOException;
import java.util.Iterator;

import ar.com.datos.buffer.FileInputBuffer;
import ar.com.datos.buffer.FileOutputBuffer;
import ar.com.datos.file.DynamicAccesor;
import ar.com.datos.file.StandardFileWrapper;
import ar.com.datos.file.exception.OutOfBoundsException;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.exception.InvalidUpdateException;
import ar.com.datos.serializer.Serializer;

public class StraightVariableLengthFile<T> implements DynamicAccesor<OffsetAddress, T> {

	private Serializer<T> serializer;
	private StandardFileWrapper file;
	public StraightVariableLengthFile(String fileName, Serializer<T> serializer) {
		this.file = constructFile(fileName);
		this.serializer = serializer;
    }
	public StraightVariableLengthFile(Serializer<T> serializer) {
		this.file = constructTempFile();
		this.serializer = serializer;
    }
	/**
	 * Para que la actualización se pueda realizar, la longitud de la entidad existente debe
	 * ser idéntica a la longitud de la entidad por la cual se la va a actualizar
	 */
	public void updateEntity(OffsetAddress address, T updatedEntity) {
		
		T existent = get(address);
		if (this.serializer.getDehydrateSize(updatedEntity) != this.serializer.getDehydrateSize(existent)) throw new InvalidUpdateException();
		getSerializer().dehydrate(new FileOutputBuffer(this.getFile(),address.getOffset()), updatedEntity);
		
	}
	@Override
	public T get(OffsetAddress address) {
		if (address.getOffset() >= this.file.getSize()) throw new OutOfBoundsException();
		return getFromBuffer(new FileInputBuffer(this.getFile(), address.getOffset()));
	}
	protected T getFromBuffer(FileInputBuffer input) {
		return getSerializer().hydrate(input);
	}

	@Override
	public OffsetAddress addEntity(T campos) {
		OffsetAddress address = new OffsetAddress(this.getFile().getSize());
		getSerializer().dehydrate(new FileOutputBuffer(this.getFile()), campos);
		return address;
	}

	@Override
	public Iterator<T> iterator() {
		return new StraightVariableLengthFileIterator(this);
	}

	@Override
	public Boolean isEmpty() {
		return getFile().isEmpty();
	}
	@Override
	public void close() throws IOException {
		this.getFile().close();
	}
	protected StandardFileWrapper constructFile(String fileName) {
		return new StandardFileWrapper(fileName);
	}
	protected StandardFileWrapper constructTempFile() {
		return new StandardFileWrapper();
	}
	protected StandardFileWrapper getFile() {
		return file;
	}
	protected void setFile(StandardFileWrapper file) {
		this.file = file;
	}
	protected Serializer<T> getSerializer() {
		return serializer;
	}
	protected void setSerializer(Serializer<T> serializer) {
		this.serializer = serializer;
	}
	protected void updateIterator(StraightVariableLengthFileIterator it) {
		if (it.hasNext()) {
			FileInputBuffer buffer = new FileInputBuffer(this.getFile(), it.getCurrentAddress().getOffset());
			it.setCurrent(getFromBuffer(buffer));
			it.setCurrentAddress(new OffsetAddress(buffer.getCurrentOffset()));
		}
	}
	private class StraightVariableLengthFileIterator implements Iterator<T> {

		private StraightVariableLengthFile<T> file;
		private OffsetAddress currentAddress;
		private T current;

		public StraightVariableLengthFileIterator(StraightVariableLengthFile<T> file) {
			super();
			this.file = file;
			this.currentAddress = new OffsetAddress(0L);
		}

		public OffsetAddress getCurrentAddress() {
			return this.currentAddress;
		}
		@Override
		public boolean hasNext() {
			return this.file.getFile().getSize() > this.currentAddress.getOffset();
		}

		@Override
		public T next() {
			this.file.updateIterator(this);
			return this.current;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}
		protected void setCurrent(T object) {
			this.current = object;
		}
		protected void setCurrentAddress(OffsetAddress address) {
			this.currentAddress = address;
		}
	}
}
