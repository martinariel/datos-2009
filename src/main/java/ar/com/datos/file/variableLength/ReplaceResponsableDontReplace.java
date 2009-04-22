package ar.com.datos.file.variableLength;

import ar.com.datos.persistencia.variableLength.BlockWriter;
import ar.com.datos.persistencia.variableLength.ReplaceResponsable;

public class ReplaceResponsableDontReplace implements ReplaceResponsable {

	private Short replaceEntity;
	private Boolean replacedOccurred = false;

	public ReplaceResponsableDontReplace(Short objectNumber) {
		this.replaceEntity = objectNumber;
	}

	@Override
	public void notifyExceed(BlockWriter blockWriter) {
		replacedOccurred = true;
	}

	public Boolean hasReplacedOccurred() {
		return replacedOccurred;
	}

	@Override
	public Short replaceObjectNumber() {
		return replaceEntity;
	}

}
