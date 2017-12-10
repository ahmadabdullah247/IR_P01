package irp01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

public class Indexer {
	// An IndexWriter creates and maintains an index.
	private IndexWriter writer;
	
	public Indexer(String indexDirectoryPath) throws IOException {
		/* 
		 Constructor will take directory path as input open it and configure it
		*/
		FSDirectory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
		// create the indexer
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(directory, config);
	}

	public void close() throws CorruptIndexException, IOException {
		/*
		 * close the indexwriter we created in Indexer Constructor
		*/
		writer.close();
	}
	private Document getDocument(File file) {
		// For every document we are storing its file name, title and content in our
		// index.
		Document document = new Document();
		try {
			// File Name
			FieldType fileNameField = new FieldType();
			fileNameField.setIndexOptions(IndexOptions.NONE);
			// setStored: Set to true to store this field.
			fileNameField.setStored(true);
			// True if normalization values should be omitted for the field.
			fileNameField.setOmitNorms(true);
			// setTokenized: Set to true to store this field.
			fileNameField.setTokenized(true);
			// File Title
			FieldType fileHtmlTitle = new FieldType();
			fileHtmlTitle.setIndexOptions(IndexOptions.DOCS);
			fileHtmlTitle.setStored(true);
			fileHtmlTitle.setTokenized(true);
			// Content of the file
			FieldType contentField = new FieldType();
			contentField.setIndexOptions(IndexOptions.DOCS);
			contentField.setStored(true);
			contentField.setTokenized(true);
			contentField.setStoreTermVectors(true);
			contentField.setStoreTermVectorPositions(true);
			contentField.setStoreTermVectorOffsets(true);
			contentField.setStoreTermVectorPayloads(true);
			Scanner scan = new Scanner(file);
			scan.useDelimiter("\\Z");
			scan.close();
			Field contentValue;
			// instantiate the field with Parameters defined in LuceneInitializr Class and put down the value in it
			Field fileNameValue = new Field(LuceneInitializr.FILE_NAME, file.getName(), fileNameField);
			
			if (file.getName().contains(".html")) {
				String htmlContent = readFile(file);
				htmlContent = htmlContent.toLowerCase();
				// search for title tag in file and get its content and put down as value in the field 
				// while instantiating it
				Field FileHtmlTitle = new Field(LuceneInitializr.FILE_HTML_TITLE,
						htmlContent.substring(htmlContent.indexOf("<title>") + 7, htmlContent.indexOf("</title")),
						fileHtmlTitle);
				document.add(FileHtmlTitle);
				
				// instantiate the field with Parameters defined in LuceneInitializr Class and its content
				contentValue = new Field(LuceneInitializr.CONTENTS,
						// porter stemmer is used to remove html tags from the content in body tag
						StringPorterStemmer(RemoveTags(htmlContent.substring(htmlContent.indexOf("<body>") + 6,
								htmlContent.indexOf("</body")))) + " "
								+ htmlContent.substring(htmlContent.indexOf("<title>") + 7,
										htmlContent.indexOf("</title")),
						contentField);
			} else {
				//if it's not an HTML value
				contentValue = new Field(LuceneInitializr.CONTENTS, porterstemmer(file), contentField);
			}
			// add fields in document
			document.add(contentValue);
			document.add(fileNameValue);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return document;
	}

	private void indexFile(File file) throws IOException {
		/*
		 * takes file as input and it will index the file and add it in writer
		 * */
		System.out.println("Indexing " + file.getCanonicalPath());
		Document document = getDocument(file);
		writer.addDocument(document);
	}

	public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
		/*
		 * accepts the directory path and creates index in it
		 * also gets filter as input to make sure what type of files to index
		 * will create the index.
		 * */
		// get list of files
		File[] files = new File(dataDirPath).listFiles();

		try {
			for (File file : files) {
				if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
					indexFile(file);
				} else if (file.isDirectory()) {
					createIndex(file.getAbsolutePath(), filter);
				}
			}
		} catch (Exception e) {
			System.out.println(
					"Can not find any data file at location : " + dataDirPath + " .Please provide correct path");
		}
		return writer.numDocs();
	}

	public List<String> getHtmlContent(File file) {
		/*
		 * gets File as input and gets html content from it
		 * then get tilte and body content out of it
		 * */
		List<String> htmlContent = new ArrayList<String>();
		String fileContent = new String();
		String htmlTitle = new String();
		try {
			fileContent = readFile(file);
			htmlTitle = fileContent.substring(fileContent.indexOf("<title>") + 7, fileContent.indexOf("</"));
			fileContent = fileContent.substring(fileContent.indexOf("<body>") + 6, fileContent.indexOf("</"));
			htmlContent.add(htmlTitle);
			htmlContent.add(fileContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return htmlContent;
	}

	public String readFile(File file) throws IOException {
		/*
		 * accept file as arguement, reads it line by line and append it in StringBuilder
		 * */
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(" ");
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}
	
	public String porterstemmer(File file) {
		/*
		 * gets files as input and stems every word in that file
		 * */
		Scanner sc;
		List<String> lines = new ArrayList<String>();
		List<String> stemmedLines = new ArrayList<String>();
		try {
			sc = new Scanner(file);
			while (sc.hasNextLine()) {
				lines.add(sc.nextLine());
			}
			sc.close();
			PorterStemmer stemmer = new PorterStemmer();
			for (String line : lines) {
				StringBuffer stemedLine = new StringBuffer();
				String[] words = line.split(" ");
				for (int i = 0; i < words.length; i++) {
					stemmer.setCurrent(words[i]);
					stemmer.stem();
					stemedLine.append(stemmer.getCurrent() + " ");
				}
				stemmedLines.add(stemedLine.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stemmedLines.toString();
	}
	
	public String RemoveTags(String content) {
		/*
		 * Remove HTML tags from HTML file
		 * */
		int startIndex = 0;
		int endIndex = 0;
		String toBeReplaced = "";
		while ((content.contains("<")) && (content.contains(">"))) {
			startIndex = content.indexOf("<");
			endIndex = content.indexOf(">");
			toBeReplaced = content.substring(startIndex, endIndex + 1);
			content = content.replace(toBeReplaced, " ");
		}

		return content;
	}

	static public String StringPorterStemmer(String QueryString) {
		PorterStemmer stemmer = new PorterStemmer();

		StringBuffer stemedLine = new StringBuffer();
		String[] words = QueryString.split(" ");
		for (int i = 0; i < words.length; i++) {
			stemmer.setCurrent(words[i]);
			stemmer.stem();
			stemedLine.append(stemmer.getCurrent() + " ");
		}
		return stemedLine.toString();
	}
}
