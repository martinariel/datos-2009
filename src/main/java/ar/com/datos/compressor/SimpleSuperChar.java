package ar.com.datos.compressor;

public class SimpleSuperChar implements SuperChar {
	private int charCode;

	public SimpleSuperChar(Character c) {
		charCode = c.charValue();
	}
	public SimpleSuperChar(Integer charCode) {
		this.charCode = charCode;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.SuperChar#matches(ar.com.datos.compressor.SuperChar)
	 */
	@Override
	public Boolean matches(SuperChar other) {
		boolean returnValue = (this.charCode == other.intValue());
		
		if (!returnValue) {
			// Si soy ESC matcheo con todos.
			returnValue = this.equals(ESC);
		}
		
		return returnValue;		
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(SuperChar o) {
		long difference = ((long)this.charCode) - ((long)o.intValue()); 
		return (difference > 0) ? 1 : ((difference < 0) ? -1 : 0); 
	}

	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.SuperChar#intValue()
	 */
	@Override
	public int intValue() {
		return this.charCode;
	}

	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new Integer(this.charCode).toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !SuperChar.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		
		SuperChar o = (SuperChar)obj; 
		return this.charCode == o.intValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new Integer(this.intValue()).hashCode();
	}
	
	/*
	 * (non-Javadoc)
	 * @see ar.com.datos.compressor.SuperChar#charValue()
	 */
	@Override
	public char charValue() {
		return (char)this.intValue();
	}
}
