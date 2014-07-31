package es.oeg.txtAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
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
	
	public static List<String> search(Directory directory, Analyzer analyzer, Query query){
		if (directory == null || analyzer == null || query == null){
			System.out.println("Null paremeters ");
			return null;
		}
		List<String> results = new ArrayList<String>();
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
		    		results.add(url);
		    	}
		    	ireader.close();
		    	directory.close();
		    	return results;
		    }
		} catch (IOException e) {
			System.out.println("ERROR: "+e.getMessage());	
			return null;
		} 
	    return null;
	    
	}
	
	
	
}
