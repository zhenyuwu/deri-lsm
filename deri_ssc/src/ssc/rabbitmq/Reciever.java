package ssc.rabbitmq;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;


import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;

import ssc.cqels.TriplesStream;

import com.hp.hpl.jena.sparql.core.Var;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class Reciever {

  private static final String EXCHANGE_NAME = "logs";

  public static java.sql.Connection connection;
  
  /**
   * open virtuoso connection with usename and password      *
   * @param  url  the url of virtuoso server
   */
  public static void getVirtuosoConneciton(){
		try{
			String url = "jdbc:virtuoso://localhost:1111/";
		    String username = "dba";
		    String password = "dba";
			Class.forName("virtuoso.jdbc4.Driver");
			connection = DriverManager.getConnection(url,username,password);
			System.out.println("Load successfull");			
		}catch (Exception e){
			System.out.println("Connection failed"+ e.getMessage());			
		}
  }
  
  /**
   * insert triples to specify virtuoso graph
   *
   * @param  graphName  an absolute URL of the graph
   * @param  nt the triples which will be insert to graph
   * the triples have to be in well format  
   */
  public static void insertTripleToGraph(String graphName, String nt) {
		// TODO Auto-generated method stub
		try{
//			System.out.println(s);
			String sql = "sparql insert into graph <" + graphName + ">{"+nt+"}";
//			System.out.println(sql);
			PreparedStatement ps = connection.prepareStatement(sql);
			boolean i = ps.execute(sql);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
  
  public static void CqelsTest() throws Exception {
	  String HOME ="/root/Java/Cqels/cqels_data/stream";
      final ExecContext context=new ExecContext(HOME, false);
//      context.loadDefaultDataset("/home/alpha/Cqels/cqels_data/10k.rdf");

//		 context.loadDataset("http://deri.org/floorplan/", "/home/alpha/Cqels/cqels_data/floorplan.rdf");
		 TriplesStream stream = new TriplesStream(context, "http://deri.org/streams/rfid");
		 String queryString ="PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> "+
				 "SELECT ?s ?type ?value "+ 								
					"WHERE { "+
					"STREAM <http://deri.org/streams/rfid> [NOW] "+
					"{?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type." +
						"?s <http://lsm.deri.ie/ont/lsm.owl#value> ?value.}" +
						"filter (?value>15)"+								
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
			  
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    com.rabbitmq.client.Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	
	    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
	    String queueName = channel.queueDeclare().getQueue();
	    channel.queueBind(queueName, EXCHANGE_NAME, "");  
	    System.out.println("Exchange name: " + EXCHANGE_NAME);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	
	    QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(queueName, true, consumer);

	    t.start();
	    while (true) {
	    	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	    	String message = new String(delivery.getBody());
//	    	System.out.println(message);
//	    	stream.addTriples(message);
	    	stream.queue.add(message);
//	    	System.out.println("receive:"+(++i));
	    }
	   
}
  
  /**
   * insert triples to specify virtuoso graph
   *
   * @param  graphName  an absolute URL of the graph
   * @param  nt the triples which will be insert into graph
   * the triples have to be in well format  
   */
  public static void getMessages(){
	  try{
		  	ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost("localhost");
		    com.rabbitmq.client.Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();
		
		    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		    String queueName = channel.queueDeclare().getQueue();
		    channel.queueBind(queueName, EXCHANGE_NAME, "");  
		    System.out.println("Exchange name: " + EXCHANGE_NAME);
		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(queueName, true, consumer);
		    int i=0;
		    while (true) {
		    	QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		    	String message = new String(delivery.getBody());
//		    	insertTripleToGraph("http://lsm.deri.ie/data#",message);
		    	System.out.println("receive:"+(++i));
		    }
	  	}catch(Exception e){
	    	e.printStackTrace();
	    }
  }
  
  public static void main(String[] argv) throws Exception {
//		getVirtuosoConneciton();	
//	    getMessages();
	    	  CqelsTest();
  }
}