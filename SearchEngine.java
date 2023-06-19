
/**
 * Name:Riva Kansakar
 * Date: 15th May, 2023
 * CSC 202
 * Project 4--SearchEngine.java
 * 
 * A simplified document indexer and search engine.
 * 
 * Documents are added to the engine one-by-one, and uniquely identified by a DocumentId.
 *
 * Each document is matched with its "terms", which are lower-cased versions of each word 
 * in the document. 
 * 
 * Inquires for terms can also be made on the lower-cased version of the term. Terms are 
 * therefore case-insensitive.
 * 
 * Lookups for documents can be done by term. The most relevant document(s) to a specific term 
 * (as computed by tf-idf) can also be retrieved.
 * 
 * Document Assistance(who and describe what assistance was received; if no assistance, declare that fact):
 * Prof Mueller helped to improve the add document method. Used geeks for geeks to search how to use the 
 * comparator
 * 
 */

import java.util.*;

public class SearchEngine {
	private Map<String, Set<DocumentId>> termsToDoc;
	private Map<DocumentId, Map<String, Integer>> idToCount;

	/**
	 * Instantiates an empty map for each data field
	 */

	public SearchEngine() {
		termsToDoc = new HashMap<>();
		idToCount = new HashMap<>();
	}

	// addDocument method—but only the code to create the term to document id map
	
	/**
	 * Receive a word and adds to both search engine's maps
	 * 
	 * @param id to add in the map that is associated the word with 
	 * @param word to add in the map
	 */
	public void addDocument(DocumentId id, String word) {
		String[] terms = word.split("\\s+");

		Map<String, Integer> innerMap = new HashMap<>();

		for (String term : terms) {

			term = term.toLowerCase().replaceAll("^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$", "");

			if (!(termsToDoc.containsKey(term))) {
				Set<DocumentId> doc = new HashSet<>();
				doc.add(id);
				termsToDoc.put(term, doc);
			} else {
				Set<DocumentId> doc = termsToDoc.get(term);
				doc.add(id);
			}

			if (!(innerMap.containsKey(term))) {
				innerMap.put(term, 1);
			} else {
				int countInt = innerMap.get(term) + 1;
				innerMap.put(term, countInt);

			}
			idToCount.put(id, innerMap);
		}
	}
	
	/**
	 * Receives a term and returns the set of the 
	 * documents that contain the given term
	 * @param word to search in the search engine's maps
	 * @return set of documents that contain the term
	 */

	public Set<DocumentId> indexLookup(String word) {

		return termsToDoc.get(word);
	}
	
	/**
	 * Receives a document's id and a term returning the frequency of 
	 * the term in that document
	 * @param id that the word is associated to
	 * @param word to search the frequency of
	 * @return the frequency of the term in that document
	 */

	public int termFrequency(DocumentId id, String word) {
		if (!(idToCount.containsKey(id))) {
			throw new IllegalArgumentException("Document id not in search engine.");
		}

		Map<String, Integer> innerMap = idToCount.get(id);
		if (!(innerMap.containsKey(word))) {
			return 0;
		}
		return innerMap.get(word);
	}
	
	/**
	 * Receives a term and returns the term's idf
	 * @param word to calculate the idf of
	 * @return the idf of the word, a double 
	 */

	public double inverseDocumentFrequency(String word) {

		double termAppearance = 0;
		double numOfDoc = idToCount.keySet().size();

		if (termsToDoc.get(word) != null) {
			termAppearance = termsToDoc.get(word).size();
		}
		double idf = Math.log((1 + numOfDoc) / (1 + termAppearance));
		return idf;
	}
	
	/**
	 * Receives a term and returns the term's tidf
	 * @param id that the word is associated to
	 * @param word to calculate the idf of
	 * @returnthe tidf of the word, a double 
	 */

	public double tfIdf(DocumentId id, String word) {
		if (!(idToCount.containsKey(id))) {
			throw new IllegalArgumentException("Document id not in search engine.");
		}
		return termFrequency(id, word) * inverseDocumentFrequency(word);

	}
	
	/**
	 * Receives a term and returns a sorted list of document ids 
	 * from most relevant to least relevant for the given term
	 * @param word to search for in the search engine
	 * @return a list of sorted documents
	 */

	public List<DocumentId> relevanceLookup(String word) {
		List<DocumentId> docId = new ArrayList<>();
		Set<DocumentId> docIds = new HashSet<>();
		if (termsToDoc.get(word) != null) {
			for (DocumentId documentId : termsToDoc.get(word)) {
				docId.add(documentId);
			}
		}
		docId.sort(new TfIdfComparator(this, word));
		return docId;

	}

	@Override
	public String toString() {
		String string = "Term to doc id map:" + "\n" + termsToDoc.toString() + "\n" + "Doc id to term frequency map:"
				+ "\n" + idToCount.toString();
		return string;
	}

}
