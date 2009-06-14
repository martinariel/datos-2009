package ar.com.datos.compressor.ppmc.file;

import java.io.PrintStream;

import ar.com.datos.compressor.FileCompressor;
import ar.com.datos.compressor.ppmc.PPMCCompressor;

public class PPMCFileCompressor extends PPMCCompressor implements FileCompressor{

	public PPMCFileCompressor(int order, PrintStream trace){
		super(order, trace);
	}
	public PPMCFileCompressor(PrintStream trace){
		super(trace);
	}
	public PPMCFileCompressor(int order){
		super(order);
	}
	public PPMCFileCompressor(){
		super();
	}
}
