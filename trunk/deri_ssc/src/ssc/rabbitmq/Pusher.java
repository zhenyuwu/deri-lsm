package ssc.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Pusher {

	private String EXCHANGE_NAME = "logs";
	private Channel channel=null;
	private Connection connection=null;
	private String host="localhost";
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public void openRabbitConnection(){
		try{
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUsername("guest");
		    factory.setPassword("guest");
		    factory.setVirtualHost("/");
//		    factory.setHost("10.196.104.208");	
		    factory.setHost(host);
		    factory.setPort(5672);			
		    connection = factory.newConnection();
		    channel = connection.createChannel();
		    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void closeRabbitConnection(){
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
	
		
	public String getEXCHANGE_NAME() {
		return EXCHANGE_NAME;
	}
	public void setEXCHANGE_NAME(String eXCHANGE_NAME) {
		EXCHANGE_NAME = eXCHANGE_NAME;
	}
	
	
	public void push(String message) throws Exception {    
//		System.out.println(message.getBytes());
	    channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());
	    System.out.println(" [x] Sent '");
	}
}
