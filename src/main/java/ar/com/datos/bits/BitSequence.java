package ar.com.datos.bits;

import java.util.Iterator;

/**
 * Manejo de una secuencia de bits.
 * 
 * @author fvalido
 */
public interface BitSequence extends BitReceiver, BitEmisor {
	/**
	 * Obtiene la cantidad de bits almacenada en esta BitSequence.
	 */
	public long getBitsCount();

	/**
	 * Obtiene los bits almacenados por esta secuencia. Cada byte del array devuelto
	 * representa un bit, por supuesto con valor 0 o 1. 
	 */
	public byte[] getBitsAsByteArray();

	/**
	 * Obtiene los bits almacenados por esta secuencia. Cada byte del array devuelto
	 * representa 8 bits (conversión a binario del número decimal representado en
	 * cada byte).
	 */
	public byte[] getBitsAsCondensedByteArray();
	
	/**
	 * El método next() devolverá un bit representado por un byte cuyo valor,
	 * por supuesto, será 1 o 0.
	 * El primer bit a devolver será el ubicado en startBitPosition, siendo 0
	 * la primer posición.
	 * 
	 * PRE: startBitPosition < getBitsCount()
	 */
	public Iterator<Byte> iterator(long startBitPosition);
	
	/**
	 * Agrega al final de la secuencia los bits guardados en la secuencia pasada.
	 */
	public void addBits(BitSequence bits);

	/**
	 * Agrega al final de la secuencia los bits guardados en el array pasado.
	 * Cada byte del array pasado representa un bit, por supuesto con valor 0 o 1.
	 */
	public void addBits(byte[] bits);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * (es decir, se agregan 64 bits).
	 */
	public void addBits(long bits);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * hasta alcanzar minCount bits (Si el número de por si tenía más de minCount
	 * bits significativos se agregarán más bits de lo indicado por minCount).
	 */	
	public void addBits(long bits, byte minCount);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * (es decir, se agregan 32 bits).
	 */
	public void addBits(int bits);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * hasta alcanzar minCount bits (Si el número de por si tenía más de minCount
	 * bits significativos se agregarán más bits de lo indicado por minCount).
	 */	
	public void addBits(int bits, byte minCount);
	
	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * (es decir, se agregan 16 bits).
	 */
	public void addBits(short bits);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * hasta alcanzar minCount bits (Si el número de por si tenía más de minCount
	 * bits significativos se agregarán más bits de lo indicado por minCount).
	 */	
	public void addBits(short bits, byte minCount);
	
	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * (es decir, se agregan 8 bits).
	 */
	public void addBits(byte bits);

	/**
	 * Agrega al final de la secuencia los bits que se obtienen convirtiendo a
	 * base binaria el número decimal pasado, incluidos los 0 que haya a la izquierda
	 * hasta alcanzar minCount bits (Si el número de por si tenía más de minCount
	 * bits significativos se agregarán más bits de lo indicado por minCount).
	 */	
	public void addBits(byte bits, byte minCount);
	
	/**
	 * Agrega al final de la secuencia los bits guardados en el array pasado.
	 * Cada byte del array pasado representa 8 bits (conversión a binario del número decimal
	 * representado en cada byte).
	 * Se incluyen todos los bits de cada byte del array (es decir, se agregarán
	 * bits.length * 8 bits).
	 */
	public void addCondensedBits(byte[] bits);
	
	/**
	 * Agrega al final de la secuencia los bits guardados en el array pasado.
	 * Cada byte del array pasado representa 8 bits (conversión a binario del número decimal
	 * representado en cada byte).
	 * En el caso del último byte del array se incluyen los 0 que haya a la izquierda hasta
	 * alcanzar minCount bits (Si el número de por si tenía más de minCount bits significativos
	 * se agregarán más bits de los indicado por minCount).
	 */
	public void addCondensedBits(byte[] bits, byte minCount);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits 
	 * guardados en la secuencia pasada.
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(BitSequence bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * guardados en el array pasado.
	 * Cada byte del array pasado representa un bit, por supuesto con valor 0 o 1.
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(byte[] bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda (es decir, se agregan 64 bits).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(long bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda hasta alcanzar minCount bits (Si el número de por si tenía
	 * más de minCount bits significativos se agregarán más bits de lo indicado por minCount).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */	
	public void insertBits(long bits, byte minCount, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda (es decir, se agregan 32 bits).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(int bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda hasta alcanzar minCount bits (Si el número de por si tenía
	 * más de minCount bits significativos se agregarán más bits de lo indicado por minCount).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.	 
	 */
	public void insertBits(int bits, byte minCount, long position);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda (es decir, se agregan 16 bits).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(short bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda hasta alcanzar minCount bits (Si el número de por si tenía
	 * más de minCount bits significativos se agregarán más bits de lo indicado por minCount).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(short bits, byte minCount, long position);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda (es decir, se agregan 8 bits).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(byte bits, long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits
	 * que se obtienen convirtiendo a base binaria el número decimal pasado, incluidos los
	 * 0 que haya a la izquierda hasta alcanzar minCount bits (Si el número de por si tenía
	 * más de minCount bits significativos se agregarán más bits de lo indicado por minCount).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBits(byte bits, byte minCount, long position);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) un bit con valor 0.
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insert0(long position);

	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) un bit con valor 1.
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insert1(long position);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) un bit con valor bit
	 * que, por supuesto, debe ser 0 o 1.
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertBit(long position, byte bit);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits 
	 * guardados en el array pasado. 
	 * Cada byte del array pasado representa 8 bits (conversión a binario del número decimal
	 * representado en cada byte).
	 * Se incluyen todos los bits de cada byte del array (es decir, se agregarán
	 * bits.length * 8 bits).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertCondensedBits(byte[] bits, long position);
	
	/**
	 * Agrega en la posición pasada (siendo 0 la primer posición) de la secuencia los bits 
	 * guardados en el array pasado.
	 * Cada byte del array pasado representa 8 bits (conversión a binario del número decimal
	 * representado en cada byte).
	 * En el caso del último byte del array se incluyen los 0 que haya a la izquierda hasta
	 * alcanzar minCount bits (Si el número de por si tenía más de minCount bits significativos
	 * se agregarán más bits de los indicado por minCount).
	 * Los bits originales ubicados desde la posición pasada en adelante quedarán desplazados
	 * a la derecha en la secuencia.
	 */
	public void insertCondensedBits(byte[] bits, byte minCount, long position);
	
	/**
	 * Obtiene el bit de la posición pasada (siendo 0 la primer posición).
	 */
	public byte getBit(long position);

	/**
	 * Remueve el bit de la posición pasada (siendo 0 la primer posición).
	 * 
	 * @return
	 * El bit que fue removido.
	 */
	public byte removeBit(long position);
	
	/**
	 * Cambia el valor del bit de la posición pasada (siendo 0 la primer posición).
	 * 
	 * @return
	 * El valor que se estableció para el bit.
	 */
	public byte changeBit(long position);
	
	/**
	 * Establece el bit de la posición pasada (siendo 0 la primer posición) con valor 0.
	 */
	public void set0(long position);

	/**
	 * Establece el bit de la posición pasada (siendo 0 la primer posición) con valor 1.
	 */
	public void set1(long position);
	
	/**
	 * Establece el bit de la posición pasada (siendo 0 la primer posición) con valor bit
	 * que, por supuesto, debe ser 0 o 1.
	 */
	public void setBit(long position, byte bit);
	
	/**
	 * Convierte en decimal los bits count ubicados desde la posición pasada (siendo 0 la primer
	 * posición).
	 * Si count es menor que 64 entonces se considerarán bits con valor 0 a la izquierda
	 * de la porción de secuencia tomada.
	 * 
	 * PRE: count <= 64
	 */
	public long toDecimalLong(long position, byte count);
	
	/**
	 * Convierte en decimal los bits count ubicados desde la posición pasada (siendo 0 la primer
	 * posición).
	 * Si count es menor que 32 entonces se considerarán bits con valor 0 a la izquierda
	 * de la porción de secuencia tomada.
	 * 
	 * PRE: count <= 32
	 */
	public int toDecimalInt(long position, byte count);
	
	/**
	 * Convierte en decimal los bits count ubicados desde la posición pasada (siendo 0 la primer
	 * posición).
	 * Si count es menor que 16 entonces se considerarán bits con valor 0 a la izquierda
	 * de la porción de secuencia tomada.
	 * 
	 * PRE: count <= 16
	 */
	public short toDecimalShort(long position, byte count);
	
	/**
	 * Convierte en decimal los bits count ubicados desde la posición pasada (siendo 0 la primer
	 * posición).
	 * Si count es menor que 8 entonces se considerarán bits con valor 0 a la izquierda
	 * de la porción de secuencia tomada.
	 * 
	 * PRE: count <= 8
	 */
	public byte toDecimalByte(long position, byte count);
}
