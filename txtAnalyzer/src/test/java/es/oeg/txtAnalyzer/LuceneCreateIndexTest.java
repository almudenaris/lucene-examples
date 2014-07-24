package es.oeg.txtAnalyzer;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 * creación de un índice para ficheros .txt contenidos en un directorio
 * @author Almudena Ruiz-Iniesta almudenari@fi.upm.es
 */
public class LuceneCreateIndexTest {
	
	LuceneIndexer indexer = new LuceneIndexer();
	
	// analizador para texto en inglÃ©s
	EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9, EnglishAnalyzer.getDefaultStopSet());
	
	// files to be read
	public static final String FILE_PATH = "src/test/resources/";
	public static final String FILE_NAME="documents";
	
	@Test
	public void testNullParameter(){		
		assertNull(indexer.createIndex(null, null, null));
	}
	
	@Test
	public void testNullDirectory(){
		Path path = null;
		path = Paths.get(FILE_PATH, new String[]{FILE_NAME});
		List<File> list = (new ArrayList<File>());
		list.add(path.toFile());
		assertNull(indexer.createIndex(analyzer, null, list));		
	}	

	@Test
	public void testNullIndexDirectory(){
		assertNull(indexer.createIndex(analyzer, null, null));
	}
	
	@Test
	public void testIndexDirectoryNull(){
		assertNull(indexer.indexAllFilesInDirectory(null));		
	}
	
	@Test
	public void testIndexDirectoryEqualNumberFiles(){
		Path directory = Paths.get(FILE_PATH);
		List<File> list = indexer.indexAllFilesInDirectory(directory);
		System.out.println("All the indexed files are: "+list.toString());
		assertEquals(61,list.size());
	}

	//@Test null query en la busqueda
	//@Test distinto numero de resultados que los esperados
	//@Test no resultados cuando tiene que haber
	@Test
	public void testCreateIndexAndSearch() throws ParseException{		
		Path directory = Paths.get(FILE_PATH, "index");
		//build the list of all the files
		List<File> listfiles = indexer.indexAllFilesInDirectory(Paths.get(FILE_PATH, FILE_NAME));
		// index the file
		assertNotNull(indexer.createIndex(analyzer, directory, listfiles));
		//build the query
		Query query = LuceneSearcher.buildQuery(analyzer, "contents", "receiving");
		// search over the index
		LuceneSearcher.search(indexer.getDirectory(), analyzer, query);
		
	}
	
	@Test
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
		
		for (int i=1; i<numDocs; i++) 
			LuceneSearcher.readingIndex(directory.toString(), i);
		
	}
	
	
}
