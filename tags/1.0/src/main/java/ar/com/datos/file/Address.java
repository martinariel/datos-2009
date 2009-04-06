package ar.com.datos.file;
/**
 * 
 * @author dev
 *
 */
public interface Address<T extends Number, K extends Number> {

	public T getBlockNumber();
	public K getObjectNumber();

}
