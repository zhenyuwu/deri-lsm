package ssc.websocket.echo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import ssc.utils.JSONUtil;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


public class SparqlEndPointSocket extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
    private volatile int byteBufSize;
    private volatile int charBufSize;

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
    		
		    
	  	}catch(Exception e){
	    	e.printStackTrace();
	    }
    }
    
    private static final class EchoMessageInbound extends MessageInbound {

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
        	try{
	        	JSONObject json = (JSONObject) JSONSerializer.toJSON( message.toString() );
	        	
	        	String service = json.getString("url");
	        	String query = json.getString("sparql");
	        	
				QueryExecution vqe = new QueryEngineHTTP(service, query);
				ResultSet results = vqe.execSelect(); 
				
				ByteArrayOutputStream out = new ByteArrayOutputStream();; 
			    ResultSetFormatter.outputAsJSON(out,results);
			    
			    JSONObject jsonResult = (JSONObject) JSONSerializer.toJSON( out.toString() );
			    jsonResult.put("updated", (new Date()).getTime());
			    jsonResult.put("ntriples", JSONUtil.JSONToNTriple(jsonResult.getJSONObject("results")));
			    
				System.out.println(jsonResult.toString());   	
	        	
	        	Charset charset = Charset.forName("ISO-8859-1");
				CharsetDecoder decoder = charset.newDecoder();
				CharsetEncoder encoder = charset.newEncoder();
				CharBuffer uCharBuffer = CharBuffer.wrap(jsonResult.toString());
	            getWsOutbound().writeTextMessage(uCharBuffer);
        	}catch(Exception e){
        		e.printStackTrace();
        		getWsOutbound().writeTextMessage(null);
        	}
            
        }
    }
}
