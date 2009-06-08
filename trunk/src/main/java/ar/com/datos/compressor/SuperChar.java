package ar.com.datos.compressor;

public interface SuperChar extends Comparable<SuperChar> {
	public static final SuperChar EOF = new SimpleSuperChar((1 << 16) + 1);
	public static final SuperChar ESC = new SimpleSuperChar((1 << 16) + 2);
	
	/**
	 * Permite saber si el SuperChar pasado !!matchea!! (que no es lo mismo que ser igual)
     * con el actual.
	 */
	public Boolean matches(SuperChar other);
	/**
	 * Permite saber el código correspondiente al caracter pasado.
	 */
	public int intValue();
}
