package ar.com.datos.compressor.ppmc.file;

import java.io.PrintStream;

import ar.com.datos.buffer.InputBuffer;
import ar.com.datos.compressor.CompressorException;
import ar.com.datos.compressor.FileDeCompressor;
import ar.com.datos.compressor.ppmc.PPMCDecompressor;
import ar.com.datos.documentlibrary.Document;

public class PPMCFileDecompressor extends PPMCDecompressor implements FileDeCompressor{

	public PPMCFileDecompressor(int order, PrintStream trace){
		super(order, trace);
	}
	public PPMCFileDecompressor(PrintStream trace){
		super(trace);
	}
	public PPMCFileDecompressor(int order){
		super(order);
	}
	public PPMCFileDecompressor(){
		super();
	}
	
	@Override
	public void decompress(InputBuffer input, Document document)
			throws CompressorException {
		document.addLine(super.decompress(input));
	}

	@Override
	public String getCompressorName() {
		return "PPMC";
	}

}
