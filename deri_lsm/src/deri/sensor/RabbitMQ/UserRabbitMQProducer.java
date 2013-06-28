package deri.sensor.RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import deri.sensor.javabeans.UserRabbitMQ;

public class UserRabbitMQProducer {
	private UserRabbitMQ userRabbit;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String message;
	private String routingKey;
	
	public UserRabbitMQProducer(){
		
	}
		
	public void openConnection(){
		try{
			factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(userRabbit.getQueue_name(), true, false, false, null);
			channel.exchangeDeclare(userRabbit.getExchange_name(), "direct");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Open connection failed");
		}
	}
	
	public void closeConnection(){
		try{
			channel.close();
	        connection.close();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Close connection failed");
		}
	}
	
	
	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getRoutingKey() {
		return routingKey;
	}


	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	
	

	public UserRabbitMQ getUserRabbit() {
		return userRabbit;
	}


	public void setUserRabbit(UserRabbitMQ userRabbit) {
		this.userRabbit = userRabbit;
	}


	public void publishMessage(){
		try{
			channel.basicPublish(userRabbit.getExchange_name(), routingKey, null, message.getBytes());
			System.out.println("Message publish successful");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
