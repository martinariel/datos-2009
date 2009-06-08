package ar.com.datos.compressor;

public interface SuperChar extends Comparable<SuperChar> {
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
