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

public class CqelsStreamSocket extends WebSocketServlet {

		private static final long serialVersionUID = 7L;
		public static QueueingConsumer  consumer;
	    private volatile int byteBufSize;
	    private volatile int charBufSize;
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
//	        	System.out.println(temp);        	  
			    
	        	String HOME ="/home/Cqels/cqels_data/stream";
	      	    context=new ExecContext(HOME, false);				
				
	        	JSONObject json = (JSONObject) JSONSerializer.toJSON( temp );
	        	String type = json.getString("type");
	        	if(type.equals("ToRDFStream")){
	        		String query = json.getString("sparql");
	        		String triplesInput = json.getString("ntriples");	        		
//	        		System.out.println(triplesInput);
	        		if(!query.equals("")){
	        			 TriplesStream stream = new TriplesStream(context, "http://deri.org/streams/rfid");
	        			 stream.addTriples(triplesInput);
        			     ContinuousSelect selQuery=context.registerSelect(query);
        				 selQuery.register(new ContinuousListener()
        				 {
        				       public void update(Mapping mapping){
        				          String result="";
        				          String n = "";
        				          for(Iterator<Var> vars=mapping.vars();vars.hasNext();){
        				        	  n =  context.engine().decode(mapping.get(vars.next())).toString();
        				        	  if(n.contains("^")){
        								  int indx = n.lastIndexOf("^");
        								  result+= " " + n.substring(0, indx+1)+"<"+n.substring(indx+1)+">";
        							  }else
        								  result+=" <"+ n +">";
        				          }
        				          try{      
        				        	  System.out.println(result);
	        						  CharBuffer uCharBuffer = CharBuffer.wrap(result);	        					      
	        					      getWsOutbound().writeTextMessage(uCharBuffer);		        				         
        				          }catch(Exception e){
        				        	  e.printStackTrace();
        				          }
        				       } 
        				 });        				 
        				 t = new Thread((Runnable) stream);
        				 t.start();  
        				 
        				 try {
        					t.join();
        				 } catch (InterruptedException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        				 }
	        		}
	        	}	
			}	        
	    }
	    
	    private String getCqelsResult(){
	    	
			return null;
	    	
	    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
