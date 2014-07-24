package es.oeg.txtAnalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
/**
 * 
 * @author Almudena Ruiz-Iniesta almudenari@fi.upm.es
 *
 */
public class LuceneIndexer {

	private Directory directory;

	private List<File> list = new ArrayList<File>();

	public List<File> getList() {
		return list;
	}

	// all the files to be indexed are in @directory
	public List<File> indexAllFilesInDirectory(Path directory){

		if(directory== null) 
			return null;

		File file = directory.toFile();
		if (!file.exists()) {
			System.out.println(directory + " does not exist.");
		}
		else 
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					indexAllFilesInDirectory(f.toPath());
				}
			} else {
				String filename = file.getName().toLowerCase();		      
				// Only index text files		      
				if (filename.endsWith(".txt")) {
					list.add(file);
				} else {
					System.out.println("Skipped " + filename);
				}
			}
		return list;
	}


	public IndexWriter createIndex(Analyzer analyzer, Path indexPath, List<File> filePath) {
		if (indexPath == null || filePath == null || analyzer== null)
			return null;

		Document doc = null;		

		IndexWriter iwriter = null;		

		try {
			// Store the index in a directory
			directory = FSDirectory.open(new File(indexPath.toUri()));

			// config
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_9, analyzer);		    
			iwriter = new IndexWriter(directory, config);
					    
			for(File p: filePath){
				// read the content of the file
				FileReader fr = new FileReader(p);
				doc = new Document();
				doc.add(new TextField("contents", fr));
				doc.add(new StringField("path", p.toString(), Field.Store.YES));
				doc.add(new StringField("filename", p.getName().toString(), Field.Store.YES));

				iwriter.addDocument(doc);
			}
			iwriter.close();

		} catch (IOException e) {
			System.out.println("Failed the indexer "+e.getMessage());
		}

		return iwriter;
	}

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

}
