package irp01;

import java.io.IOException;
import java.nio.file.Paths;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class Searcher {
	IndexSearcher indexSearcher;
	QueryParser queryParser;
	Query query;

	public Searcher(String indexDirectoryPath) throws IOException {
		// takes the directory path as input where to indexes are present
		// and initialize that
		Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
		IndexReader reader = DirectoryReader.open(dir);
		this.indexSearcher = new IndexSearcher(reader);
	}

	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		// returns the document as required in input's doc attribute
		return indexSearcher.doc(scoreDoc.doc);
	}

	public void tdf_BM25_search(String searchQuery, Boolean isBM25) throws IOException, ParseException {
		// parse the inputed query and rank with either Vector Space Model or Okapi BM25 
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
	        		doc.get(LuceneInitializr.FILE_HTML_TITLE)+")");
	        rank++;
	    }
	}

}
