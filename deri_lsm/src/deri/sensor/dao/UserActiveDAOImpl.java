package deri.sensor.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.UserRabbitMQ;
import deri.sensor.javabeans.UserView;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserActiveDAOImpl extends HibernateDaoSupport implements
		UserActiveDAO {

	public UserActiveDAOImpl(){
		super();
	}
	
	@Override
	public boolean addUser(User user) {
		if(!isUser(user)){
			this.getHibernateTemplate().save(user);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void addUserRabbitMQ(UserRabbitMQ userR) {
		this.getHibernateTemplate().save(userR);
	}

	@Override
	public void deleteUser(User user) {
		this.getHibernateTemplate().delete(user);
	}

	@Override
	public User getUser(String username) {
//		String hql = "from User u where u.username = '"+username+"'";
//		List<User> users = this.getSession(true).createQuery(hql).list();
//		return users.size()>0?users.get(0):null;
		User user = null;		
		String sql = "sparql select ?userId ?nickname ?pass "+
				"from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
				"where{ "+
				  "?userId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasUserName> \""+username+"\"."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasNickName> ?nickname."+
				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasPassword> ?pass."+
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(rs.getString("userId"));
					user.setNickname(rs.getString("nickname"));
					user.setUsername(username);
					user.setPass(rs.getString("pass"));					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public boolean isUser(User user) {
		User user_in_db = this.getUser(user.getUsername());
		if(user_in_db == null){
			return false;
		}else{
			if(user_in_db.getPass().equals(user.getPass())){
				return true;
			}else{
				return false;
			}
		}
	}

	@Override
	public void addUserView(UserView userView) {
		this.getHibernateTemplate().save(userView);
	}
	
	@Override
	public void updateUserView(UserView userView) {
		this.getHibernateTemplate().update(userView);
	}

	@Override
	public void deleteUserView(UserView userView) {
		this.getHibernateTemplate().delete(userView);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserView> getUserView(String username) {
		String hql = "from UserView u where u.username = '"+username+"'";
		List<UserView> list = this.getSession(true).createQuery(hql).list();
		return list;
	}

	@Override
	public UserView getUserView(String username, String viewname) {
		String hql = "from UserView u where u.username = '"+username+"' and u.viewname = '"+viewname+"'";
		List<UserView> userviews = this.getSession(true).createQuery(hql).list();
		return userviews.size()>0?userviews.get(0):null;
	}

	@Override
	public boolean isUserviewExistedForSpecifiedUser(String username,
			String viewname) {
		String hql = "from UserView u where u.username = '"+username+"' and u.viewname = '"+viewname+"'";
		UserView userview = (UserView)this.getSession(true).createQuery(hql).setMaxResults(1).uniqueResult();
		if(userview != null){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public UserRabbitMQ getUserRabbitMQFromUserName(String userName) {
		// TODO Auto-generated method stub
		String hql = "from UserRabbitMQ u where u.user.username = '"+userName+"'";
		List<UserRabbitMQ> users = this.getSession(true).createQuery(hql).list();
		return users.size()>0?users.get(0):null;
	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub
		String hql = "update User set pass = :newPass where id = :id";
        Query query = this.getSession().createQuery(hql);
        query.setString("newPass",user.getPass());
        query.setString("id",user.getId());
        int rowCount = query.executeUpdate();
	}

	@Override
	public User getUserWithUserId(String userId) {
		// TODO Auto-generated method stub
		User user = null;
		userId = "<"+userId+">";
		String sql = "sparql select ?nickname ?pass ?username "+
				"from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
				"where{ "+
				  userId +" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasUserName> ?username."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasNickName> ?nickname."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasPassword> ?pass."+
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(userId);
					user.setNickname(rs.getString("nickname"));
					user.setUsername(rs.getString("username"));
					user.setPass(rs.getString("pass"));					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}

}
