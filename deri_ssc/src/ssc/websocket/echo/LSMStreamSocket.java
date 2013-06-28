package ssc.websocket.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;

import ssc.cqels.TriplesStream;

import com.hp.hpl.jena.sparql.core.Var;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class LSMStreamSocket extends WebSocketServlet {

	private static final long serialVersionUID = 7L;
	public static QueueingConsumer  consumer;
    private volatile int byteBufSize;
    private volatile int charBufSize;
    private static final String EXCHANGE_NAME = "logs";
    public static ExecContext context;
    public static TriplesStream stream;
    @Override
    public void init() throws ServletException {
        super.init();
        byteBufSize = getInitParameterIntValue("byteBufferMaxSize", 2097152);
        charBufSize = getInitParameterIntValue("charBufferMaxSize", 2097152);
    }

    public int getInitParameterIntValue(String name, int defaultValue) {
        String val = this.getInitParameter(name);
        int result;
        if(null != val) {
            try {
                result = Integer.parseInt(val);
            }catch (Exception x) {
                result = defaultValue;
            }
        } else {
            result = defaultValue;
        }

        return result;
    }



    @Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		return new EchoMessageInbound(byteBufSize,charBufSize);
	}

    static {
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
		
		    consumer = new QueueingConsumer(channel);
		    channel.basicConsume(queueName, true, consumer);	
		    
		    
		    String HOME ="/root/Java/Cqels/cqels_data/stream";
      	    context=new ExecContext(HOME, false);
			stream = new TriplesStream(context, "http://deri.org/streams/rfid");

	  	}catch(Exception e){
	    	e.printStackTrace();
	    }
    }
    private static final class EchoMessageInbound extends MessageInbound {
    	static Thread t ;
        public EchoMessageInbound(int byteBufferMaxSize, int charBufferMaxSize) {
            super();
            setByteBufferMaxSize(byteBufferMaxSize);
            setCharBufferMaxSize(charBufferMaxSize);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
        	System.out.println(message);
            getWsOutbound().writeBinaryMessage(message);
            
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
        	
        	String temp = message.toString();
//        	System.out.println(temp);        	  
		    
        	JSONObject json = (JSONObject) JSONSerializer.toJSON( temp );
        	String type = json.getString("type");
        	if(type.equals("LSMStream")){
        		String query = json.getString("sparql");
        		if(!query.equals("")){
        			     ContinuousSelect selQuery=context.registerSelect(query);
        				 selQuery.register(new ContinuousListener()
        				 {
        				       public void update(Mapping mapping){
        				          String result="";
        				          for(Iterator<Var> vars=mapping.vars();vars.hasNext();)
        				          //Use context.engine().decode(...) to decode the encoded value to RDF Node
        				              result+=" "+ context.engine().decode(mapping.get(vars.next()));
        				          try{
        				        	  Charset charset = Charset.forName("ISO-8859-1");
	        				          CharsetDecoder decoder = charset.newDecoder();
	        						  CharsetEncoder encoder = charset.newEncoder();
	        						  CharBuffer uCharBuffer = CharBuffer.wrap("Java Code Geeks");			
	        						  ByteBuffer bbuf = encoder.encode(uCharBuffer);		   
	        						  CharBuffer cbuf = decoder.decode(bbuf);
	        						    
	        				          uCharBuffer = CharBuffer.wrap(result);
	        					      bbuf = encoder.encode(uCharBuffer);
	        					      cbuf = decoder.decode(bbuf);
	        					      getWsOutbound().writeTextMessage(cbuf);
//	        				          System.out.println(result);
        				          }catch(Exception e){
        				        	  e.printStackTrace();
        				          }
        				       } 
        				 });        				 
        				 if(t==null){
        					 t = new Thread((Runnable) stream);
        					 t.start();
        				 }
        		}
        	}
        	
		    String mes = "";
	    	QueueingConsumer.Delivery delivery;
//	    	while(true){
				try {
					delivery = LSMStreamSocket.consumer.nextDelivery();
					mes = new String(delivery.getBody());
					stream.queue.add(mes);
//					Charset charset = Charset.forName("ISO-8859-1");
//					CharsetDecoder decoder = charset.newDecoder();
//					CharsetEncoder encoder = charset.newEncoder();
//					CharBuffer uCharBuffer = CharBuffer.wrap(mes);			
//				    ByteBuffer bbuf = encoder.encode(uCharBuffer);		   
//				    CharBuffer cbuf = decoder.decode(bbuf);		    
//		            getWsOutbound().writeTextMessage(cbuf);
//			    	System.out.println(mes);
				} catch (ShutdownSignalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConsumerCancelledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  	
//	    	}
		}
        
    }
}
