package es.oeg.txtAnalyzer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class LuceneSearcher {
	
	public static int getNumberOfDocuments(Directory directory) throws IOException{		
		DirectoryReader ireader;
		ireader = DirectoryReader.open(directory);		
		return ireader.numDocs();		
	}
	
	public static Query buildQuery(Analyzer analyzer, String field, String text) throws ParseException {
		
		QueryParser parser = new QueryParser(Version.LUCENE_4_9, field, analyzer);
		// Parse a simple query that searches for this text		
		Query query = parser.parse(text);
		return query;
	}
	
	public static void search(Directory directory, Analyzer analyzer, Query query){
		// Now search the index:
	    DirectoryReader ireader;
		try {
			ireader = DirectoryReader.open(directory);
			
			IndexSearcher isearcher = new IndexSearcher(ireader);		    
		    
		    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
		    
		    if ( hits.length == 0)
		    	System.out.println("No data found");
		    else{  		
		    		    // Iterate through the results:
		    for (int i = 0; i < hits.length; i++) {
		      Document hitDoc = isearcher.doc(hits[i].doc);		      
		      String url = hitDoc.get("path"); // get its path field
              System.out.println("Found in :: " + url);

		    }
		    
		    ireader.close();
		    directory.close();
		    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    
	}
	
	//@param index path to index directory
	//@param docNbr the document number in the index
	public static void readingIndex(String index, int docNbr) throws IOException {
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));

	    Document doc = reader.document(docNbr);         
	    System.out.println("Processing file: "+doc.get("filename"));

	    Terms termVector = reader.getTermVector(docNbr, "contents");
	    TermsEnum itr = termVector.iterator(null);
	    BytesRef term = null;

	    while ((term = itr.next()) != null) {               
		    String termText = term.utf8ToString();
		    Term termInstance = new Term("contents", term);                              
		    long termFreq = reader.totalTermFreq(termInstance);
		    long docCount = reader.docFreq(termInstance);

		    System.out.println("term: "+termText+", termFreq = "+termFreq+", docCount = "+docCount);
	    }            

	    reader.close();     
	}

	// @see http://filotechnologia.blogspot.it/search/label/tf-idf
	public static void testCalculateTfIdf(Path indexDirectory, Directory directory) throws IOException{
		TF_IDF tf_idf = new TF_IDF();
		
		DirectoryReader ireader = DirectoryReader.open(directory);		
		/** GET FIELDS **/
	    Fields fields = MultiFields.getFields(ireader); //Get the Fields of the index
	    System.out.println("Fields indexados:"+fields.toString());
	    
		for (String field: fields){
			System.out.println("Field actual: "+field);
			TermsEnum termEnum = MultiFields.getTerms(ireader, field).iterator(null);
			System.out.println("terms enum: "+ termEnum.toString());
	          BytesRef bytesRef;
	          while ((bytesRef = termEnum.next()) != null){
	              if (termEnum.seekExact(bytesRef)){
	               String term = bytesRef.utf8ToString();   
	               System.out.println("Termino: "+term);
	               tf_idf.scoreCalculator(ireader, field, term);
	              }
	          }
		}
		
		assertTrue(true);
		
	}
	
}
