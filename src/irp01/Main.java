package irp01;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Main {
	String indexPath; // Index path
	String dataPath; // Data path
	Indexer indexer; // Object of Indexer
	Searcher searcher; // Object of Searcher

	// initialize the data and index path
	public Main() {
		String path = new File("").getAbsolutePath();
		dataPath = path + "\\data";
		indexPath = path + "\\index";
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void SetIndexPath(String Path) {
		indexPath = Path + "/index";
		// indexPath = Path + "\\index";
	}

	public static void hashLine() {
		System.out.println(
				"#########################################################################################################");
	}

	public static void main(String[] args) throws ParseException {
		Main app;
		try {
			app = new Main();
			String choice = "";
			Scanner scanner = new Scanner(System.in); // Reading from System.in
			// app.SetIndexPath(args[0]);
			app.SetIndexPath("/Users/ollostudio/Desktop/LuceneSearch/IR_P01");

			while (!choice.equalsIgnoreCase("3")) {
				hashLine();
				System.out.println("Press : ");
				System.out.println("1. to create new indexes.");
				System.out.println("2. to use existing indexes.");
				System.out.println("3. to exit.");
				choice = scanner.nextLine();
				hashLine();
				switch (choice) {
				case "1":
					System.out.println("Index path :: " + app.getIndexPath());
					deleteIndex(new File(app.getIndexPath()));	// Deletes all files in the path.
					// app.createIndex(args[0]);
					app.createIndex("/Users/ollostudio/Desktop/LuceneSearch/IR_P01");		// Create new indexes.
				case "2":
					try {
						hashLine();
						System.out.println("Enter Search Query ::");
						choice = scanner.nextLine();
						if (!choice.equalsIgnoreCase("")) {
							System.out.println("Searching Documents for query :: " + choice);
							app.search(choice + " " + Indexer.StringPorterStemmer(choice));
						} else {
							System.out.println("Query string empty. Please provide valid search query.");
						}
					} catch (IndexNotFoundException e) {
						System.out.println("Index path is empty.");
						System.out.println("Index documents to continue.");
					}
					break;
				case "3":
					choice = "3";
					System.out.println("End of Program.");
					break;
				default:
					System.out.println("Invalid Input. Please provide correct input.");
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void search(String searchQuery) throws IOException, ParseException {
		searcher = new Searcher(indexPath);
		long startTime = System.currentTimeMillis();
		TopDocs hits = searcher.search(searchQuery);
		long endTime = System.currentTimeMillis();
		int rank = 1;
		System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.getDocument(scoreDoc);
			if ((doc.get(LuceneInitializr.FILE_NAME).contains(".html"))
					|| (doc.get(LuceneInitializr.FILE_NAME).contains(".htm")))
				System.out.println("Rank(Score):" + rank + "(" + scoreDoc.score + ") File Name(title):"
						+ doc.get(LuceneInitializr.FILE_NAME) + "(" + doc.get(LuceneInitializr.FILE_HTML_TITLE)
						+ ") File Path:" + doc.get(LuceneInitializr.FILE_PATH) + "   Time Stamp:"
						+ doc.get(LuceneInitializr.FILE_TIMESTAMP));
			else
				System.out.println("Rank(Score):" + rank + "(" + scoreDoc.score + ") File Name:"
						+ doc.get(LuceneInitializr.FILE_NAME) + " File Path:" + doc.get(LuceneInitializr.FILE_PATH)
						+ "   Time Stamp:" + doc.get(LuceneInitializr.FILE_TIMESTAMP));
			rank++;
		}
	}

	static void deleteIndex(File dir) {
		try {
			for (File file : dir.listFiles()) {
				if (file.isDirectory())
					deleteIndex(file);
				file.delete();
			}
		} catch (NullPointerException e) {
			System.out.println("Given path does not has any files.");
		}

	}

	private void createIndex(String dataDirPath) throws IOException {
		indexer = new Indexer(indexPath);
		int numIndexed;
		long startTime = System.currentTimeMillis();
		numIndexed = indexer.createIndex(dataDirPath, new FileTypeGuard());
		long endTime = System.currentTimeMillis();
		indexer.close();
		hashLine();
		System.out.println("New indexes created.");
		System.out.println(numIndexed + " File indexed, time taken: " + (endTime - startTime) + " ms");
	}
}
