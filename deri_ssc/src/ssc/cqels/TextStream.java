package ssc.cqels;

import java.io.InputStream;
import java.util.Iterator;

import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;
import org.deri.cqels.engine.RDFStream;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.util.FileManager;

public class TextStream extends RDFStream implements Runnable{
        String txtFile;
        boolean stop=false;
        long sleep=50;
        public TextStream(ExecContext context, String uri,String txtFile) {
                super(context, uri);
                this.txtFile=txtFile;
        }

        @Override
        public void stop() {
                stop=true;
        }
        public void setRate(int rate){
                sleep=1000/rate;
        }
        
        public void run() {
                // TODO Auto-generated method stub
                try {
//                        BufferedReader reader = new BufferedReader(new FileReader(txtFile));
//                        String strLine;
//                        while ((strLine = reader.readLine()) != null &&(!stop))   {
//                            String[] data=strLine.split(" ",3);
////                            System.out.println(data[0]+" "+ data[1]+" "+data[2]);
//                                stream(n(data[0]),n(data[1]),n(data[2])); // For streaming RDF triples
//                                
//                                if(sleep>0){
//                                        try {
//                                                Thread.sleep(sleep);
//                                        } catch (InterruptedException e) {
//                                                // TODO Auto-generated catch block
//                                                e.printStackTrace();
//                                        }
//                                }
//                        }
                	InputStream in = FileManager.get().open("/root/Java/Cqels/cqels_data/stream/rfid_500.stream");
                    Model model2 = ModelFactory.createDefaultModel();
                    model2.read(in,"","TURTLE");
                    StmtIterator iter = model2.listStatements();
                    while(iter.hasNext()){
                    	Statement stm = iter.next();
                    	Node s = stm.getSubject().asNode();
                    	Node o = stm.getObject().asNode();
                    	Node p = stm.getPredicate().asNode();
//                    	System.out.println(s+" "+p+" "+o);
                    	stream(s,p,o);
                    }
                
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }
        
//        public static  Node n(String st){
//                return Node.createURI(st);
//        }


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String HOME ="/root/Java/Cqels/cqels_data/stream";
		final ExecContext context=new ExecContext(HOME, false);
		//load the default dataset from the a directory

//	context.loadDefaultDataset("/home/alpha/Cqels/cqels_data/10k.rdf");
//
//		//load datasets for named graph
//
//		 context.loadDataset("http://deri.org/floorplan/", "/home/alpha/Cqels/cqels_data/floorplan.rdf");
		 RDFStream stream = new TextStream(context, "http://deri.org/streams/rfid", HOME+"/rfid_500.stream");
//		 String queryString ="PREFIX lv: <http://deri.org/floorplan/> "+
//								"PREFIX dc: <http://purl.org/dc/elements/1.1/> "+ 
//								"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
//								"SELECT ?locName ?name "+ 
//								"FROM NAMED <http://deri.org/floorplan/> "+
//								"WHERE { "+
//								"STREAM <http://deri.org/streams/rfid> [NOW] "+
//								"{?person lv:detectedAt ?loc} "+
//								"{?person foaf:name ?name} "+
//								"GRAPH <http://deri.org/floorplan/> "+
//								"{?loc lv:name ?locName} "+
//								"}"; 

		 String queryString ="PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> "+
				 "SELECT ?s ?p ?o "+ 								
					"WHERE { "+
					"STREAM <http://deri.org/streams/rfid> [TRIPLES 100] "+
					"{?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation." +
					"?s ?p ?o.}" +													
					"}";
//		 Model model = ModelFactory.createDefaultModel();
		 ContinuousSelect selQuery=context.registerSelect(queryString);
		 
		 selQuery.register(new ContinuousListener()
		 {
		       public void update(Mapping mapping){
		    	  String result="";
		          for(Iterator<Var> vars=mapping.vars();vars.hasNext();){
		          //Use context.engine().decode(...) to decode the encoded value to RDF Node
		                 result+=" "+ context.engine().decode(mapping.get(vars.next()));		                 
		                 //		          		 Node node = context.engine().decode(mapping.get(vars.next()));
		          		 
		          }
		          System.out.println(result);
		       } 
		 });
		 Thread t = new Thread((Runnable) stream);
		 t.start();
	}

}
