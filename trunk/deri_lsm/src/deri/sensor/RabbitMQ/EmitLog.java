package deri.sensor.RabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import deri.sensor.xslt.XSLTParser;

public class EmitLog {

  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    
    argv = new String[2];
    argv[0]="Nguyen..";
    argv[1]="Mau...";
    String message = getMessage();
    
    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
    System.out.println(" [x] Sent '" + message + "'");

    channel.close();
    connection.close();
  }
  
  private static String getMessage(){
	  System.setProperty("javax.xml.transform.TransformerFactory",
              "net.sf.saxon.TransformerFactoryImpl");
    return XSLTParser.simpleTransform("xslt/wunderground.xml",
    		   "xslt/WUnderground.xsl","xslt/result.txt");
  }
}
