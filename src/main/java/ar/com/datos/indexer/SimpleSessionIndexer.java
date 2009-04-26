package ar.com.datos.indexer;

import java.util.ArrayList;
import java.util.Collection;

import ar.com.datos.btree.BTree;
import ar.com.datos.file.variableLength.VariableLengthFileManager;
import ar.com.datos.file.variableLength.VariableLengthWithCache;
import ar.com.datos.file.variableLength.address.OffsetAddress;
import ar.com.datos.file.variableLength.address.OffsetAddressSerializer;
import ar.com.datos.indexer.keywordIndexer.FixedLengthKeyCounter;
import ar.com.datos.indexer.keywordIndexer.KeyCount;
import ar.com.datos.serializer.Serializer;
import ar.com.datos.serializer.common.CollectionSerializer;
import ar.com.datos.serializer.common.TupleSerializer;
import ar.com.datos.util.Tuple;

public class SimpleSessionIndexer<T> implements SessionIndexer<T> {

	public static final String LEXICON_SUFFIX = ".lex"; 
	public static final String INDEX_SUFFIX = ".idx"; 
	public static final String LIST_SUFFIX = ".lst"; 
	
	public static final Integer LIST_BLOCK_SIZE = 1024; 

	// Indexador del elemento al que se le cuentan las palabras
	private Serializer<T> indexedSerializer;

	private LexicalManager lexicon;
	private VariableLengthFileManager<Collection<KeyCount<T>>> listsForTerms;
	private BTree<IndexerTreeElement<T>, IndexerTreeKey> indexedElements;

	// Usado solamente durante la sesión de agregado de palabras 
	private FixedLengthKeyCounter<Tuple<OffsetAddress, T>> fixedLengthCounter = null;
	
	/**
	 * Genera un indexador que trabaja por sessiones de agregado.
	 * Durante la sesión se podrán agregar términos asociados a elementos de tipo T.
	 * El serializador <code>indexedSerializer</code> debe serializar siempre a estructuras del mismo tamaño
	 * @param fileName prefijo de los archivos usados por el indexador
	 * Los archivos que se levantarán son: 
	 * </br>
	 * {@code filename + SimpleSessionIndexer#INDEX_SUFFIX //para el archivo indexador de términos}</br>
	 * {@code filename + SimpleSessionIndexer#LEXICON_SUFFIX //para el archivo completo de léxico}</br>
	 * {@code filename + SimpleSessionIndexer#LIST_SUFFIX //para la lista de elementos asociados a un mismo término}</br>
	 * 
	 * @param indexedSerializer
	 * </br>
	 * Serializador del elemento al que se asociarán términos indexados 
	 */
	public SimpleSessionIndexer(String fileName, Serializer<T> indexedSerializer) {
		this.lexicon = constructLexicon(fileName);
		this.indexedSerializer = indexedSerializer;
		this.indexedElements = constructIndexedElements(fileName);
		this.listsForTerms = constructListForTerms(fileName);
	}

	@Override
	public void startSession() {
		this.fixedLengthCounter = constructCounter();
		this.fixedLengthCounter.startSession();
	}

	@Override
	public boolean isActive() {
		return fixedLengthCounter == null;
	}

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
			currentElement.addTemporalDataCount(newCountForWord.getKey().getSecond(), newCountForWord.getCount());
		}
		for (IndexerTreeElement<T> element: elements) {
			this.indexedElements.addElement(element);
		}
		this.fixedLengthCounter = null;
	}

	/**
	 * {@link Indexer#addTerms(Object, String...)} 
	 * La session tiene que estar activa
	 * <code>{@link #isActive()} == true</code>
	 */
	public void addTerms(T dato, String...terms) {
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
	public Collection<KeyCount<T>> findTerm(String string) {
		return this.indexedElements.findElement(new IndexerTreeKey(string)).getDataCounts();
	}

	public VariableLengthFileManager<Collection<KeyCount<T>>> getListsForTerms() {
		return listsForTerms;
	}

	/**
	 * Agrego el término tanto en el índice como en el léxico 
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

	protected VariableLengthFileManager<Collection<KeyCount<T>>> constructListForTerms(String fileName) {
		return new VariableLengthWithCache<Collection<KeyCount<T>>>(fileName + LIST_SUFFIX, LIST_BLOCK_SIZE, new CollectionSerializer<KeyCount<T>>(new KeyCountSerializer<T>(this.indexedSerializer)));
	}

	protected BTree<IndexerTreeElement<T>, IndexerTreeKey> constructIndexedElements(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	protected FixedLengthKeyCounter<Tuple<OffsetAddress, T>> constructCounter() {
		 return new FixedLengthKeyCounter<Tuple<OffsetAddress, T>>(new TupleSerializer<OffsetAddress, T>(new OffsetAddressSerializer(),this.indexedSerializer));
	}

}
