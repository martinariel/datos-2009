package ar.com.datos.file.address;


/**
 * 
 * @author jbarreneche
 *
 */
public interface BlockAddress<T extends Number, K extends Number> extends Address {

	public T getBlockNumber();
	public K getObjectNumber();

}
