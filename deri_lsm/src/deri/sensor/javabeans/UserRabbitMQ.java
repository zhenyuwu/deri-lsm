package deri.sensor.javabeans;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserRabbitMQ {
	private String id;
	private User user;
	private String queue_name;
	private String exchange_name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQueue_name() {
		return queue_name;
	}
	public void setQueue_name(String queueName) {
		queue_name = queueName;
	}
	public String getExchange_name() {
		return exchange_name;
	}
	public void setExchange_name(String exchangeName) {
		exchange_name = exchangeName;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	
	
}
