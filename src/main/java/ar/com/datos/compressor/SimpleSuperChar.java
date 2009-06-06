package ar.com.datos.compressor;

public class SimpleSuperChar implements SuperChar {

	private int charCode;
	public SimpleSuperChar(Character c) {
		charCode = c.charValue();
	}
	public SimpleSuperChar(Integer charCode) {
		this.charCode = charCode;
	}
	@Override
	public Boolean matches(SuperChar other) {
		return this.charCode == ((SimpleSuperChar)other).charCode;
	}

}
