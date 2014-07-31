package es.oeg.txtAnalyzer;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTFIDFTest {
LuceneIndexer indexer = new LuceneIndexer();
	
	// analizador para texto en inglÃ©s
	EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9, EnglishAnalyzer.getDefaultStopSet());
	
	// files to be read
	public static final String FILE_PATH = "src/test/resources/";
	public static final String FILE_NAME="documents";
	
	@Test //failed test
	public void testGetMostFrequent() throws IOException{
		Path directory = Paths.get(FILE_PATH, "index");
		//build the list of all the files
		List<File> listfiles = indexer.indexAllFilesInDirectory(Paths.get(FILE_PATH, FILE_NAME));
		// index the file
		IndexWriter writer = (indexer.createIndex(analyzer, directory, listfiles));
		assertNotNull(writer);
		// get the reader
		int numDocs = LuceneSearcher.getNumberOfDocuments(indexer.getDirectory()
				);
		
		//for (int i=1; i<numDocs; i++) 
			//LuceneSearcher.readDocumentIndex(directory.toString(), i);
	
	}	
	
	@Test
	public void testCalculateTFIDF() throws IOException{
		Path directory = Paths.get(FILE_PATH, "index");		
		//build the list of all the files
		List<File> listfiles = indexer.indexAllFilesInDirectory(Paths.get(FILE_PATH, FILE_NAME));
		// index the file
		IndexWriter writer = (indexer.createIndex(analyzer, directory, listfiles));
		assertNotNull(writer);
		//LuceneSearcher.testCalculateTfIdf(directory, indexer.getDirectory());
		
	}
}
