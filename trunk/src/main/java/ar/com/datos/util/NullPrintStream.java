package ar.com.datos.util;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * {@link PrintStream} para "/dev/null".
 * 
 * @author fvalido
 */
public class NullPrintStream extends PrintStream {
	/**
	 * Constructor
	 */
	public NullPrintStream() {
		super(new OutputStream() {
			/*
			 * (non-Javadoc)
			 * @see java.io.OutputStream#write(int)
			 */
			@Override
			public void write(int b) {
			}
		});
	}
}
