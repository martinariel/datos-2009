package ar.com.datos.btree.sharp.impl.disk.node;

/**
 * Tipos de nodos.
 */
public enum NodeType {
	LEAF((byte)1),			// Hoja 
	INTERNAL((byte)2),		// Interno
	ROOT((byte)3),			// Raiz Definitiva
	ESPECIALROOT((byte)4);	// Raiz Inicial
	
	private byte type;
	
	private NodeType(byte type) {
		this.type = type;
	}
	
	/**
	 * Obtiene el tipo de nodo como un byte.
	 */
	public byte getType() {
		return this.type;
	}
	
	/**
	 * Obtiene el tipo de nodo para el byte pasado.
	 */
	public static NodeType getNodeType(byte type) {
		NodeType returnValue = null;
		switch (type) {
			case 1: returnValue = LEAF; break;
			case 2: returnValue = INTERNAL; break;
			case 3: returnValue = ROOT; break;
			case 4: returnValue = ESPECIALROOT; break;
		}
		
		return returnValue;
	}
}
