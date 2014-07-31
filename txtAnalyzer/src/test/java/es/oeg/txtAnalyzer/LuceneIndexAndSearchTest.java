package es.oeg.txtAnalyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Test;

/**
 * creación de un índice para ficheros .txt contenidos en un directorio
 * @author Almudena Ruiz-Iniesta almudenari@fi.upm.es
 */
public class LuceneIndexAndSearchTest {
	
	LuceneIndexer indexer = new LuceneIndexer();
	
	// analizador para texto en inglés
	EnglishAnalyzer analyzer = new EnglishAnalyzer(Version.LUCENE_4_9, EnglishAnalyzer.getDefaultStopSet());
	
	// files to be read
	public static final String FILE_PATH = "src/test/resources/";
	public static final String FOLDER_NAME="documents";
	
	@Test
	public void testNullParameter(){		
		assertNull(indexer.createIndex(null, null, null));
	}
	
	@Test
	public void testNullDirectory(){
		Path path = null;
		path = Paths.get(FILE_PATH, new String[]{FOLDER_NAME});
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
		//FIXME: change the number according to your index
		assertEquals(61,list.size());
	}

	
	@Test
	public void testCreateIndexAndSearch() throws ParseException{		
		Path directory = Paths.get(FILE_PATH, "index");
		//build the list of all the files
		List<File> listfiles = indexer.indexAllFilesInDirectory(Paths.get(FILE_PATH, FOLDER_NAME));
		// index the file
		assertNotNull(indexer.createIndex(analyzer, directory, listfiles));
		//build the query
		Query query = LuceneSearcher.buildQuery(analyzer, "contents", "nrk");
		assertNotNull(query);
		assertEquals("contents:nrk",query.toString());
		// search over the index
		List<String> results = LuceneSearcher.search(indexer.getDirectory(), analyzer, query);
		assertNotNull(results);
		assertTrue(results.size() > 0);
	}
	
	@Test
	public void testSearchWithNullQuery() throws ParseException{		

		assertNull(LuceneSearcher.search(indexer.getDirectory(), analyzer, null));		
	}
	
	
}
