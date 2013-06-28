package deri.sensor.RabbitMQ;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class ReceiveLogs {

  private static final String EXCHANGE_NAME = "logs";

  public static void main(String[] argv) throws Exception {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
    String queueName = channel.queueDeclare().getQueue();
    channel.queueBind(queueName, EXCHANGE_NAME, "");
    System.out.println("Queue1:"+ queueName);
    
//    Channel channel2 = connection.createChannel();
    String queueName2 = channel.queueDeclare().getQueue();
    channel.queueBind(queueName2, EXCHANGE_NAME, "");    
    System.out.println("Queue2:"+ queueName2);
    
    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

    QueueingConsumer consumer = new QueueingConsumer(channel);
    channel.basicConsume(queueName, true, consumer);
    
    QueueingConsumer consumer2 = new QueueingConsumer(channel);
    channel.basicConsume(queueName2, true, consumer2);

    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      String message = new String(delivery.getBody());
      System.out.println(" [x] Received 1'" + message + "'");
      
      QueueingConsumer.Delivery delivery2 = consumer2.nextDelivery();
      message = new String(delivery2.getBody());
      System.out.println(" [x] Received 2'" + message + "'");
      
    }
  }
}