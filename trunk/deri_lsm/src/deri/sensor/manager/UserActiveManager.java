package deri.sensor.manager;



import java.util.List;

import deri.sensor.javabeans.User;
import deri.sensor.javabeans.UserRabbitMQ;
import deri.sensor.javabeans.UserView;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public interface UserActiveManager {
	public boolean addUser(User user);
	public void deleteUser(User user);
	public User getUser(String username);
	public boolean isUser(User user);
	public void updateUser(User user);
	
	
	public void addUserView(UserView userView);
	public void updateUserView(UserView userView);
	public void deleteUserView(UserView userView);
	public List<UserView> getUserView(String username);
	public UserView getUserView(String username, String viewname);
	public boolean isUserviewExistedForSpecifiedUser(String username, String viewname);
	public UserRabbitMQ getUserRabbitMQFromUserName(String userName);
	public void addUserRabbitMQ(UserRabbitMQ userR);
	public User getUserWithUserId(String userId);
}
