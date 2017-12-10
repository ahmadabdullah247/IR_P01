package irp01;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

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

	// INPUT : Null
	// OUTPUT : String
	// DESCRIPTION : returns string path of index folder
	public String getIndexPath() {
		return indexPath;
	}

	// INPUT : String
	// OUTPUT : Null
	// DESCRIPTION : Set string path of index folder
	public void SetIndexPath(String Path) {
		indexPath = Path;
		// indexPath = Path + "\\index";
	}

	// INPUT : Null
	// OUTPUT : Null
	// DESCRIPTION : Print line on console
	public static void hashLine() {
		System.out.println(
				"#########################################################################################################");
	}

	public static void main(String[] args) throws ParseException {
		Main app;
		try {
			app = new Main();
			String query = args[3];

			app.SetIndexPath(args[1]); // Set Index path
			// app.SetIndexPath("/Users/ollostudio/Desktop/LuceneSearch/IR_P01/Index");
			deleteIndex(new File(app.getIndexPath())); // Delete existing files
			// app.createIndex("/Users/ollostudio/Desktop/LuceneSearch/IR_P01/Data");
			app.createIndex(args[0]); // Set Data path
			if (args[2].equals("VS")) {
				app.search(Indexer.StringPorterStemmer(query), false);
			} else if (args[2].equals("OR")) {
				app.search(Indexer.StringPorterStemmer(query), true);
			} else {
				System.out.println("Invalid model name");
			}

		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	// INPUT : String, Boolean
	// OUTPUT : NULL
	// DESCRIPTION : Print ranked documents to console
	private void search(String searchQuery, Boolean tdf_BM2) throws IOException, ParseException {
		searcher = new Searcher(indexPath);
		searcher.tdf_BM25_search(searchQuery, tdf_BM2);
	}

	// INPUT : Null
	// OUTPUT : Null
	// DESCRIPTION : Delete existing indexes
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

	// INPUT : String
	// OUTPUT : Null
	// DESCRIPTION : create new indexes for documents
	private void createIndex(String dataDirPath) throws IOException {
		indexer = new Indexer(indexPath);
		indexer.createIndex(dataDirPath, new FileTypeGuard());
		indexer.close();
		hashLine();
		System.out.println("New indexes created.");
	}
}
