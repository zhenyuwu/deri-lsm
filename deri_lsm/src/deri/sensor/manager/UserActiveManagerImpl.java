package deri.sensor.manager;


import java.util.List;

import deri.sensor.dao.UserActiveDAO;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.UserRabbitMQ;
import deri.sensor.javabeans.UserView;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserActiveManagerImpl implements UserActiveManager {

	private UserActiveDAO userActiveDao;
	
	public UserActiveManagerImpl(){
		super();
	}
	
	public UserActiveManagerImpl(UserActiveDAO userActiveDao){
		this.userActiveDao = userActiveDao;
	}
	
	@Override
	public boolean addUser(User user) {
		return this.userActiveDao.addUser(user);
	}

	@Override
	public void addUserRabbitMQ(UserRabbitMQ userR) {
		this.userActiveDao.addUserRabbitMQ(userR);
	}
	
	@Override
	public void deleteUser(User user) {
		this.deleteUser(user);
	}

	@Override
	public User getUser(String username) {
		return this.userActiveDao.getUser(username);
	}

	@Override
	public boolean isUser(User user) {
		return this.userActiveDao.isUser(user);
	}

	public UserActiveDAO getUserActiveDao() {
		return userActiveDao;
	}

	public void setUserActiveDao(UserActiveDAO userActiveDao) {
		this.userActiveDao = userActiveDao;
	}

	@Override
	public void addUserView(UserView userView) {
		this.userActiveDao.addUserView(userView);
	}
	
	@Override
	public void updateUserView(UserView userView) {
		this.userActiveDao.updateUserView(userView);
	}

	@Override
	public void deleteUserView(UserView userView) {
		this.userActiveDao.deleteUserView(userView);
	}

	@Override
	public List<UserView> getUserView(String username) {
		return this.userActiveDao.getUserView(username);
	}

	@Override
	public UserView getUserView(String username, String viewname) {
		return this.userActiveDao.getUserView(username, viewname);
	}

	@Override
	public boolean isUserviewExistedForSpecifiedUser(String username,
			String viewname) {
		return this.userActiveDao.isUserviewExistedForSpecifiedUser(username, viewname);
	}

	@Override
	public UserRabbitMQ getUserRabbitMQFromUserName(String userName) {
		// TODO Auto-generated method stub
		return this.userActiveDao.getUserRabbitMQFromUserName(userName);
	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub
		this.userActiveDao.updateUser(user);
	}

	@Override
	public User getUserWithUserId(String userId) {
		// TODO Auto-generated method stub
		return this.userActiveDao.getUserWithUserId(userId);
	}

}
