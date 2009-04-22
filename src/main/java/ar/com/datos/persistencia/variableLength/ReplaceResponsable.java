package ar.com.datos.persistencia.variableLength;

public interface ReplaceResponsable {

	public void notifyExceed(BlockWriter blockWriter);
	public Short replaceObjectNumber();
	public Boolean hasReplacedOccurred();
	
}
