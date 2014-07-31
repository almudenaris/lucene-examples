package es.oeg.txtAnalyzer;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermState;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

public class TF_IDF {

	static float tf = 1;
	static float idf = 0;
	private float tfidf_score;

	static float[] tfidf = null;


	public void scoreCalculator(IndexReader reader,String field,String term) throws IOException{
		/** GET TERM FREQUENCY & IDF **/
		TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
		Bits liveDocs = MultiFields.getLiveDocs(reader);
		TermsEnum termEnum = MultiFields.getTerms(reader, field).iterator(null);
		BytesRef bytesRef;
		while ((bytesRef = termEnum.next()) != null)
		{          
			if(bytesRef.utf8ToString().trim() == term.trim())
			{                 
				if (termEnum.seekExact(bytesRef))
				{
					idf = tfidfSIM.idf(termEnum.docFreq(), reader.numDocs());
					DocsEnum docsEnum = termEnum.docs(liveDocs, null);
					if (docsEnum != null)
					{
						int doc;
						while((doc = docsEnum.nextDoc())!=DocIdSetIterator.NO_MORE_DOCS)
						{
							tf = tfidfSIM.tf(docsEnum.freq());
							tfidf_score = tf*idf;
							System.out.println(" -tfidf_score- " + tfidf_score);
						}
					}
				}
			}
		}
	}
	
	// this method reads the docNbr from the index and prints the result
		//@param index path to index directory
		//@param docNbr the document number in the index
		public static void readDocumentIndex(String index, int docNbr) throws IOException {
			
			// open the index
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
