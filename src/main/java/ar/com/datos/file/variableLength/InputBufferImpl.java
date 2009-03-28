package ar.com.datos.file.variableLength;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.buffer.exception.BufferException;
import ar.com.datos.buffer.variableLength.ArrayByte;

public class InputBufferImpl implements InputBuffer {

	@Override
	public byte[] read(byte[] data) throws BufferException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte read() throws BufferException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Agrega al final del buffer el ArrayByte recibido
	 * @param leftSubArray
	 * @return
	 */
	public InputBuffer fill(ArrayByte leftSubArray) {
		// TODO Auto-generated method stub
		return null;
	}

}
