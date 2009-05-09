package ar.com.datos.indexer;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ar.com.datos.btree.BTree;
import ar.com.datos.btree.BTreeSharpFactory;
import ar.com.datos.btree.sharp.impl.disk.ElementAndKeyListSerializerFactory;
import ar.com.datos.file.BlockAccessor;
import ar.com.datos.file.address.BlockAddress;
import ar.com.datos.file.variableLength.VariableLengthWithCache;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.lexic.LexicalManager;
import ar.com.datos.indexer.serializer.IndexerSerializerFactory;
import ar.com.datos.indexer.serializer.KeyCountSerializer;
import ar.com.datos.indexer.serializer.ListSerializer;
import ar.com.datos.indexer.tree.IndexerTreeElement;
import ar.com.datos.indexer.tree.IndexerTreeKey;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.NullableTupleSerializer;
import ar.com.datos.serializer.common.TupleSerializer;
import ar.com.datos.util.Tuple;
import ar.com.datos.utils.sort.external.FixedLengthKeyCounter;
import ar.com.datos.utils.sort.external.KeyCount;
import ar.com.datos.wordservice.exception.InactiveSessionException;
/**
 * Implementaci�n b�sica de un {@link SessionIndexer}
 * Consta de un �rbol B# para el �ndice de t�rminos. El cual tiene asociada una
 * direcci�n en el archivo de listas para ese t�rmino.
 * El archivo de listas tiene la cantidad de ocurrencias del t�rmino para cada objeto T.
 * La carga por sesiones funciona delegando en un sort externo el conteo de las ocurrencias
 * de la tupla (offset_lexico, T). Luego, al cerrar la sesi�n por cada palabra del l�xico
 * carga en el elemento los objetos T y la cantidad de relaciones que tiene el objeto con
 * el t�rmino. Para recuperar el t�rmino se utiliza un archivo que contiene todo el l�xico indexado  
 * @author jbarreneche
 *
 * @param <T>
 */
public class SimpleSessionIndexer<T> implements SessionIndexer<T> {

	public static final String LEXICON_SUFFIX = ".lex"; 
	public static final String INDEX_NODE_SUFFIX = ".idx.node"; 
	public static final String INDEX_LEAFS_SUFFIX = ".idx.leaf"; 
	public static final String LIST_SUFFIX = ".lst"; 
	
	public static final Integer LIST_BLOCK_SIZE = 512;
	public static final Integer NODE_BLOCK_SIZE = 512;

	// Indexador del elemento al que se le cuentan las palabras
	private Serializer<T> indexedSerializer;

	private LexicalManager lexicon;
	private BlockAccessor<BlockAddress<Long, Short>, Tuple<OffsetAddress, List<KeyCount<T>>>> listsForTerms;
	private BTree<IndexerTreeElement<T>, IndexerTreeKey> indexedElements;

	// Usado solamente durante la sesi�n de agregado de palabras
	// La tupla consta de la direcci�n en el archivo de l�xico (offset_termino) y el objeto T
	private FixedLengthKeyCounter<Tuple<OffsetAddress, T>> fixedLengthCounter = null;
	
	/**
	 * Genera un indexador que trabaja por sessiones de agregado.
	 * Durante la sesi�n se podr�n agregar t�rminos asociados a elementos de tipo T.
	 * El serializador <code>indexedSerializer</code> debe serializar siempre a estructuras del mismo tama�o
	 * @param fileName prefijo de los archivos usados por el indexador
	 * Los archivos que se levantar�n son: 
	 * </br>
	 * {@code filename + SimpleSessionIndexer#INDEX_SUFFIX //para el archivo indexador de t�rminos}</br>
	 * {@code filename + SimpleSessionIndexer#LEXICON_SUFFIX //para el archivo completo de l�xico}</br>
	 * {@code filename + SimpleSessionIndexer#LIST_SUFFIX //para la lista de elementos asociados a un mismo t�rmino}</br>
	 * 
	 * @param indexedSerializer
	 * </br>
	 * Serializador del elemento al que se asociar�n t�rminos indexados 
	 */
	public SimpleSessionIndexer(String fileName, Serializer<T> indexedSerializer) {
		this.lexicon = constructLexicon(fileName);
		this.indexedSerializer = indexedSerializer;
		this.indexedElements = constructIndexedElements(fileName);
		this.listsForTerms = constructListForTerms(fileName);
	}
	/**
	 * Inicia una sesi�n de agregado de t�rminos
	 */
	@Override
	public void startSession() {
		this.fixedLengthCounter = constructCounter();
		this.fixedLengthCounter.startSession();
	}

	@Override
	public boolean isActive() {
		return fixedLengthCounter != null;
	}

	/**
	 * Vuelca todos los t�rminos agregados al �ndice
	 */
	@Override
	public void endSession() {
		this.fixedLengthCounter.endSession();
		OffsetAddress currentWord = null;
		IndexerTreeElement<T> currentElement = null;
		Collection<IndexerTreeElement<T>> elements = new ArrayList<IndexerTreeElement<T>>();
		for (KeyCount<Tuple<OffsetAddress, T>> newCountForWord:this.fixedLengthCounter) {
			if (!newCountForWord.getKey().getFirst().equals(currentWord)) {
				currentWord = newCountForWord.getKey().getFirst();
				currentElement = new IndexerTreeElement<T>(new IndexerTreeKey(this.lexicon.get(currentWord)));
				elements.add(currentElement);
			}
			currentElement.setIndexer(this);
			currentElement.addTemporalDataCount(newCountForWord.getKey().getSecond(), newCountForWord.getCount());
		}
		for (IndexerTreeElement<T> element: elements) {
			this.indexedElements.addElement(element);
		}
		this.fixedLengthCounter = null;
	}
	public void addTerms(T dato, String...terms) {
		if (!this.isActive()) throw new InactiveSessionException();
		for (String term : terms) addTerm(dato, term);
	}
	protected void addTerm(T data, String term) {
		IndexerTreeElement<T> current = this.indexedElements.findElement(new IndexerTreeKey(term));
		if (current == null) {
			current = createIndexFor(term);
		}
		this.fixedLengthCounter.countKey(new Tuple<OffsetAddress, T>(current.getAddressInLexicon(), data));
	}
	@Override
	public IndexedTerm<T> findTerm(String string) {
		IndexerTreeElement<T> findElement = this.indexedElements.findElement(new IndexerTreeKey(string));
		if (findElement != null) findElement.setIndexer(this);
		return findElement;
	}

	public BlockAccessor<BlockAddress<Long, Short>, Tuple<OffsetAddress, List<KeyCount<T>>>> getListsForTerms() {
		return listsForTerms;
	}

	/**
	 * Agrego el t�rmino tanto en el �ndice como en el l�xico 
	 * @param term
	 * @return
	 */
	private IndexerTreeElement<T> createIndexFor(String term) {
		IndexerTreeElement<T> element = new IndexerTreeElement<T>(new IndexerTreeKey(term));
		element.setAddressInLexicon(this.lexicon.add(term));
		element.setIndexer(this);
		this.indexedElements.addElement(element);
		return element;
	}

	protected LexicalManager constructLexicon(String fileName) {
		return new LexicalManager(fileName + LEXICON_SUFFIX);
	}

	protected BlockAccessor<BlockAddress<Long, Short>, Tuple<OffsetAddress, List<KeyCount<T>>>> constructListForTerms(String fileName) {
		return new VariableLengthWithCache<Tuple<OffsetAddress, List<KeyCount<T>>>>(fileName + LIST_SUFFIX, LIST_BLOCK_SIZE, 
				new NullableTupleSerializer<OffsetAddress, List<KeyCount<T>>>(new OffsetAddressSerializer(), new ListSerializer<KeyCount<T>>(new KeyCountSerializer<T>(this.indexedSerializer))));
	}

	@SuppressWarnings("unchecked")
	protected BTree<IndexerTreeElement<T>, IndexerTreeKey> constructIndexedElements(String fileName) {
		Class<? extends ElementAndKeyListSerializerFactory<IndexerTreeElement<T>, IndexerTreeKey>> clazz = (Class<? extends ElementAndKeyListSerializerFactory<IndexerTreeElement<T>, IndexerTreeKey>>) IndexerSerializerFactory.class;
		return new BTreeSharpFactory<IndexerTreeElement<T>, IndexerTreeKey>().createBTreeSharpDisk(fileName + INDEX_NODE_SUFFIX, fileName + INDEX_LEAFS_SUFFIX, 
				NODE_BLOCK_SIZE, clazz, false);
	}

	protected FixedLengthKeyCounter<Tuple<OffsetAddress, T>> constructCounter() {
		 return new FixedLengthKeyCounter<Tuple<OffsetAddress, T>>(new TupleSerializer<OffsetAddress, T>(new OffsetAddressSerializer(),this.indexedSerializer));
	}
	@Override
	public void close() throws IOException {
		this.indexedElements.close();
		this.lexicon.close();
		this.listsForTerms.close();
	}
	@Override
	public Long getNumberOfIndexedTerms() {
		return this.lexicon.getNumberOfTerms();
	}

}
