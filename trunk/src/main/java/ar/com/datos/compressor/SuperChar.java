package ar.com.datos.compressor;

public interface SuperChar extends Comparable<SuperChar> {
	public static final SuperChar EOF = new SimpleSuperChar((1 << 16) + 1);
	public static final SuperChar ESC = new SimpleSuperChar((1 << 16) + 2);
	public static final SuperChar PRE_EOF_SUPER_CHAR = new SimpleSuperChar(1 << 16);
	
	/**
	 * Permite saber si el SuperChar pasado !!matchea!! (que no es lo mismo que ser igual)
     * con el actual.
	 */
	public Boolean matches(SuperChar other);
	/**
	 * Permite saber el c�digo correspondiente al caracter pasado.
	 */
	public int intValue();
	
	/**
	 * Devuelve el SuperChar correspondiente como si fuera un char.
	 * Si para el intValue() correspondiente no hab�a un char se devolver� un char
	 * resultado del casteo y, seguramente, no deseado. Usar con criterio.
	 */
	public char charValue();
}
