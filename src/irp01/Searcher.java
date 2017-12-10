package irp01;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
//import org.apache.lucene.search.similarities.TFIDFSimilarity;

public class Searcher {
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		// Initialize the directory which has to be searched and attach the Index
		Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
		IndexReader reader = DirectoryReader.open(dir);
		this.indexSearcher = new IndexSearcher(reader);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}

	public TopDocs search(String searchQuery) throws IOException, ParseException {
		// Query the index for the search result and return the ranked results
		queryParser = new QueryParser(LuceneInitializr.CONTENTS, new StandardAnalyzer());
		query = queryParser.parse(searchQuery);
		return indexSearcher.search(query, LuceneInitializr.MAX_SEARCH);
	}

	public void tdf_BM25_search(String searchQuery, Boolean isBM25) throws IOException, ParseException {
		// Query the index for the search result and return the ranked results
		if (isBM25) {
			this.indexSearcher.setSimilarity(new BM25Similarity());
		} else {
			this.indexSearcher.setSimilarity(new ClassicSimilarity());
		}
		queryParser = new QueryParser(LuceneInitializr.CONTENTS, new StandardAnalyzer());
		query = queryParser.parse(searchQuery);
		ScoreDoc[] hits = this.indexSearcher.search(query, 20).scoreDocs;
	    int hitCount = hits.length;
	    int rank=1;
	    System.out.println(hitCount + " documents found.");
	    for(ScoreDoc scoreDoc : hits) {
	    	Document doc = this.indexSearcher.doc(scoreDoc.doc);
	        System.out.println("Rank(Score):"+rank+"("+scoreDoc.score+") File Name(title):"+
	        		doc.get(LuceneInitializr.FILE_NAME)+"("+
	        		doc.get(LuceneInitializr.FILE_HTML_TITLE)+") File Path:" +
	        		doc.get(LuceneInitializr.FILE_PATH)+"   Time Stamp:"+
	        		doc.get(LuceneInitializr.FILE_TIMESTAMP));
	        rank++;
	    }
	}

	// public void tfidf (String indexDirectoryPath) throws IOException,
	// ParseException {
	// Analyzer analyzer = new StandardAnalyzer();
	// String tb1 = "Samsung";
	// Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
	// IndexReader reader = DirectoryReader.open(dir);
	//
	// this.indexSearcher = new IndexSearcher(reader);
	//// System.out.println(this.indexSearcher.search(query,
	// LuceneInitializr.MAX_SEARCH));
	// this.indexSearcher.setSimilarity(new ClassicSimilarity());
	// _search(indexSearcher, tb1, analyzer);
	//
	// }
	// public static void _search(IndexSearcher searcher, String queryString,
	// Analyzer analyzer) throws IOException, ParseException {
	// QueryParser queryParser = new QueryParser(LuceneInitializr.CONTENTS, new
	// StandardAnalyzer());
	// Query query = queryParser.parse(queryString);
	// ScoreDoc[] hits = searcher.search(query, 20).scoreDocs;
	// int hitCount = hits.length;
	// System.out.println(hits.length);
	// for (int i = 0; i < hitCount; i++) {
	// Document doc = searcher.doc(hits[i].doc);
	// System.out.println(doc.get("contents"));
	// }
	//
	//// QueryParser parser = new QueryParser("tb", analyzer);
	//// Query query = parser.parse(queryString);
	//// ScoreDoc[] hits = searcher.search(query, 20).scoreDocs;
	//// int hitCount = hits.length;
	//// System.out.println(query);
	//// for (int i = 0; i < hitCount; i++) {
	//// Document doc = searcher.doc(hits[i].doc);
	//// System.out.println("X :: "+i);
	//// System.out.println(doc.get("id"));
	//// }
	// }

}
