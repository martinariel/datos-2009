package ar.com.datos.audio;

import java.io.ByteArrayInputStream;

public class AnotherInputStream extends ByteArrayInputStream implements Cloneable {

	private Integer size;
	public AnotherInputStream(byte[] buf) {
		super(buf);
		setSize(buf.length);
	}

	public Integer getSize() {
		return size;
	}
	protected void setSize(Integer size) {
		this.size = size;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new AnotherInputStream(this.buf);
	}
}
