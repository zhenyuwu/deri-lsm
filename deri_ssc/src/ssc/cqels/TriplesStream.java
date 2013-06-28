package ssc.cqels;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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

public class TriplesStream extends RDFStream implements Runnable{
        String txtFile;
        boolean stop=false;
        long sleep=50;
        public final ArrayList<String> queue;
        public TriplesStream(ExecContext context, String uri) {
                super(context, uri);
                queue = new ArrayList<String>();
        }

        @Override
        public void stop() {
                stop=true;
        }
        public void setRate(int rate){
                sleep=1000/rate;
        }
        
        public void addTriples(String triples){
        	queue.add(triples);
        
        }
        public void run() {
                // TODO Auto-generated method stub
                try {                       
                        String strLine;
                        Model model = ModelFactory.createDefaultModel();
                        while ((!stop))   {
//                        	System.out.println(queue.size());
                        	if(queue.size()<=0) {
                        		System.out.print("");
                        		continue;                        	
                        	}
                        	strLine = queue.get(0);
                        	if(strLine==null) continue;
                      	
                            InputStream inputStream =  new ByteArrayInputStream(strLine.getBytes());
                            model.read(inputStream,"","TURTLE");
                            StmtIterator iter = model.listStatements();
                            while(iter.hasNext()){
                            	Statement stm = iter.next();
                            	Node s = stm.getSubject().asNode();
                            	Node o = stm.getObject().asNode();
                            	Node p = stm.getPredicate().asNode();
//                            	System.out.println(s+" "+p+" "+o);
                            	stream(s,p,o);
                            }
                            queue.remove(0);   
                                if(sleep>0){
                                        try {
                                                Thread.sleep(sleep);
                                        } catch (InterruptedException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                        }
                                }
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

//	     context.loadDefaultDataset("/home/alpha/Cqels/cqels_data/10k.rdf");
//
//		//load datasets for named graph
//
//		 context.loadDataset("http://deri.org/floorplan/", "/home/alpha/Cqels/cqels_data/floorplan.rdf");
		 TriplesStream stream = new TriplesStream(context, "http://deri.org/streams/rfid");
		 String queryString ="PREFIX lv: <http://deri.org/floorplan/> "+
								"PREFIX dc: <http://purl.org/dc/elements/1.1/> "+ 
								"PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
								"SELECT ?locName ?name "+ 
								"FROM NAMED <http://deri.org/floorplan/> "+
								"WHERE { "+
								"STREAM <http://deri.org/streams/rfid> [NOW] "+
								"{?person lv:detectedAt ?loc} "+
								"{?person foaf:name ?name} "+
								"GRAPH <http://deri.org/floorplan/> "+
								"{?loc lv:name ?locName} "+
								"}"; 

		 ContinuousSelect selQuery=context.registerSelect(queryString);
		 selQuery.register(new ContinuousListener()
		 {
		       public void update(Mapping mapping){
		          String result="";
		          for(Iterator<Var> vars=mapping.vars();vars.hasNext();)
		          //Use context.engine().decode(...) to decode the encoded value to RDF Node
		                 result+=" "+ context.engine().decode(mapping.get(vars.next()));
		          System.out.println(result);
		       } 
		 });
		 
		 Thread t = new Thread((Runnable) stream);
		 t.start();
		 int i = 0;
		 while(i<10){
			 stream.addTriples(i+"");
			 i++;
		 }
		
	}

}
