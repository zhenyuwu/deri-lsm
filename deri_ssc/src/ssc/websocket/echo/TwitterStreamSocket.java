package ssc.websocket.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import ssc.twitter.TwitterSession;

import com.rabbitmq.client.QueueingConsumer;

public class TwitterStreamSocket extends WebSocketServlet {

	private static final long serialVersionUID = 7L;
	public static QueueingConsumer  consumer;
    private volatile int byteBufSize;
    private volatile int charBufSize;
    private static final String EXCHANGE_NAME = "Twitter";
    
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
		return new EchoMessageInbound(byteBufSize,charBufSize);
	}
	
    static {
    	try{
//		  	ConnectionFactory factory = new ConnectionFactory();
//		    factory.setHost("localhost");
//		    com.rabbitmq.client.Connection connection = factory.newConnection();
//		    Channel channel = connection.createChannel();
//		
//		    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//		    String queueName = channel.queueDeclare().getQueue();
//		    channel.queueBind(queueName, EXCHANGE_NAME, "");  
//		    System.out.println("Exchange name: " + EXCHANGE_NAME);
//		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//		
//		    consumer = new QueueingConsumer(channel);
//		    channel.basicConsume(queueName, true, consumer);	

	  	}catch(Exception e){
	    	e.printStackTrace();
	    }
    }
    private static final class EchoMessageInbound extends MessageInbound {
    	static Thread t ;
    	TwitterSession tSession = null;
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
        	String filter = json.getString("url");
        		
		    String mes = "";
		    if(tSession==null)
		    	tSession = new TwitterSession();
		    tSession.setFilterInput(filter);
//		    tSession.init();
		    tSession.intitOneShotQuery();
		    
//	    	QueueingConsumer.Delivery delivery;
//	    	while(true){
				try {
//					delivery = TwitterStreamSocket.consumer.nextDelivery();
//					mes = new String(delivery.getBody());
					mes = tSession.getOneShotResult(filter);

					JSONObject jsonResult = new JSONObject();	
					jsonResult.put("updated", (new Date()).getTime());
					jsonResult.put("error", "false");
//					jsonResult.put("data", mes);					
					jsonResult.put("ntriples", mes);
					CharBuffer uCharBuffer = CharBuffer.wrap(jsonResult.toString());
		            getWsOutbound().writeTextMessage(uCharBuffer);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
//	    	}        	
		}
    }
}

