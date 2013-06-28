/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ssc.websocket.echo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;


public class EchoMessage extends WebSocketServlet {

    private static final long serialVersionUID = 1L;
    private volatile int byteBufSize;
    private volatile int charBufSize;
    private static final String EXCHANGE_NAME = "logs";

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
		
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(queueName, true, consumer);			    
		    
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
        	
        	String temp = message.toString();
        	System.out.println(temp);
        	Charset charset = Charset.forName("ISO-8859-1");
			CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();
			CharBuffer uCharBuffer = CharBuffer.wrap("Java Code Geeks");			
		    ByteBuffer bbuf = encoder.encode(uCharBuffer);		   
		    CharBuffer cbuf = decoder.decode(bbuf);
		    String result="";
            getWsOutbound().writeTextMessage(message);
//            if (temp.equals("rabbit")) 
//            	result ="rabbit";
//            else if (temp.equals("sensor"))
//            	result = "sensor";
//            else result = message.toString();
//		    	QueueingConsumer.Delivery delivery = StreamInbound.consumer.nextDelivery();
//		    	String mes = new String(delivery.getBody());
//		    	uCharBuffer = CharBuffer.wrap(result);
//		    	bbuf = encoder.encode(uCharBuffer);
//		    	cbuf = decoder.decode(bbuf);
//		    	getWsOutbound().writeTextMessage(cbuf);
//		    	System.out.println(mes);
//		    }
            
        }
        
        private void onRabbitMessage(){
        	
        }
    }
}
