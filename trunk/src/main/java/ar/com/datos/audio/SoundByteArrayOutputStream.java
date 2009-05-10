package ar.com.datos.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementación hiper rápida de {@link OutputStream}.
 * 
 * @author fvalido
 */
public class SoundByteArrayOutputStream extends OutputStream {
	private static int DEFAULT_INITIAL_SIZE = 128 * 1024;
	private static int GROWTH_MULTIPLIER = 2;
	private List<byte[]> byteList;
	private byte[] currentBytes;
	private int currentBytesPosition;
	private boolean acceptingData;
	
	public SoundByteArrayOutputStream() {
		this(DEFAULT_INITIAL_SIZE);
	}
	
	public SoundByteArrayOutputStream(int initialSize) {
		this.byteList = new LinkedList<byte[]>();
		this.currentBytes = new byte[initialSize];
		this.currentBytesPosition = 0;
		this.acceptingData = true;
	}

	
	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (!acceptingData) {
			return;
		}
		int availableCapacity = this.currentBytes.length - this.currentBytesPosition;
		int length = (availableCapacity > len) ? len : availableCapacity;
		
		System.arraycopy(b, off, this.currentBytes, this.currentBytesPosition, length);
		this.currentBytesPosition += length;
		ensureCapacity();
		
		if (length < len) {
			write(b, off + length, len - length);
		}
	}

	/**
	 * Se asegura que siga habiendo lugar en currentBytes. Si no lo hay
	 * agrega currentBytes a la lista, y crea un currentBytes nuevo de tamaño
	 * adecuado.
	 */
	private void ensureCapacity() {
		if (this.currentBytesPosition == this.currentBytes.length) {
			this.byteList.add(currentBytes);
			this.currentBytesPosition = 0;
			this.currentBytes = new byte[this.currentBytes.length * GROWTH_MULTIPLIER];
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		if (!acceptingData) {
			return;
		}
		
		this.currentBytes[this.currentBytesPosition] = (byte)b;
		this.currentBytesPosition++;
		
		ensureCapacity();
	}
	
	/**
	 * Devuelve el contenido de este OutputStream como una tira de bytes. Se devolverán
	 * los bytes disponibles en el momento exacto de la invocación y no posteriormente.
	 */
	public byte[] toByteArray() {
		int currentBytesPosition = this.currentBytesPosition;
		int byteListLength = this.byteList.size();
		byte[] currentBytes = this.currentBytes;
		
		int size = 0;
		int i = 0;
		Iterator<byte[]> it = this.byteList.iterator();
		while (i < byteListLength) {
			size += it.next().length;
			i++;
		}
		size += currentBytesPosition;
		
		byte[] returnValue = new byte[size]; 
		it = this.byteList.iterator();
		int currentReturnValuePos = 0;
		byte[] current;
		i = 0;
		while (i < byteListLength) {
			current = it.next();
			System.arraycopy(current, 0, returnValue, currentReturnValuePos, current.length);
			currentReturnValuePos += current.length;
			i++;
		}
		if (currentBytesPosition > 0) {
			System.arraycopy(currentBytes, 0, returnValue, currentReturnValuePos, currentBytesPosition);
		}
		
		return returnValue;
	}
	
	/**
	 * Permite especificar si se está permitiendo el ingreso de nuevos datos.
	 */
	public void setAcceptData(boolean acceptingData) {
		this.acceptingData = acceptingData;
	}
}
