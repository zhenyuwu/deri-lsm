package deri.sensor.components.user.notification;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.UserRabbitMQ;
import deri.sensor.manager.UserActiveManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserNotificationServerThread {
	private UserRabbitMQ userRabbit;
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String message;
	private String routingKey;
	private UserActiveManager userManager;
	
	
	public UserNotificationServerThread(String userName){
		userManager = ServiceLocator.getUserActiveManager();
		userRabbit = userManager.getUserRabbitMQFromUserName(userName); 
	}
	
	public void openConnection(){
		try{
			factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(userRabbit.getQueue_name(), true, false, false, null);
			channel.exchangeDeclare(userRabbit.getExchange_name(), "direct");
			channel.queueBind(userRabbit.getQueue_name(), userRabbit.getExchange_name(), userRabbit.getUser().getId());
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
	
	public void printMessage(){
		try{
		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");	
		    QueueingConsumer consumer = new QueueingConsumer(channel);
		    channel.basicConsume(userRabbit.getQueue_name(), true, consumer);
	
		    while (true) {
		      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		      String message = new String(delivery.getBody());
		      String routingKey = delivery.getEnvelope().getRoutingKey();	
		      System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] argv){
		UserNotificationServerThread server = new UserNotificationServerThread("nmqhoan");
		server.openConnection();
		server.printMessage();
		server.closeConnection();
	}
}
