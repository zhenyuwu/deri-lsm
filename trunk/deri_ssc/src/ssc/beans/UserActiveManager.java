package ssc.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ssc.controller.UserController;
import ssc.utils.ConstantsUtil;
import ssc.utils.DateUtil;
import ssc.utils.VirtuosoConstantUtil;
import virtuoso.jena.driver.VirtGraph;

public class UserActiveManager {
	private Connection conn;
	
	public UserActiveManager(){
		
	}
	
	public User getUserWithUserId(String userId) {
		// TODO Auto-generated method stub
		User user = null;
		userId = "<"+userId+">";
		String sql = "sparql select ?nickname ?pass ?username "+
				"where{ "+
				  userId +" <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasUserName> ?username."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasNickName> ?nickname."+
				  userId +" <http://lsm.deri.ie/ont/lsm.owl#hasPassword> ?pass."+
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(rs.getString(userId));
					user.setNickname(rs.getString("nickname"));
					user.setUsername(rs.getString("username"));
					user.setPass(rs.getString("pass"));					
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return user;
	}

//	public User getUser(String username) {
//		// TODO Auto-generated method stub
//		User user = null;
//		String sql = "select * from " + ConstantsUtil.databaseName
//				+ "users u where u.username = '" + username +"'";
//		try{
//			VirtGraph graph = UserController.getConnectionPool().getConnection();
//			conn = graph.getConnection();
//			PreparedStatement ps = conn.prepareStatement(sql);
//			ResultSet rs = ps.executeQuery();
//			while(rs.next()){
//				user = new User();
//				user.setId(rs.getString("id"));
//				user.setNickname(rs.getString("nickname"));
//				user.setUsername(rs.getString("username"));
//				user.setPass(rs.getString("pass"));
//			}
//			UserController.getConnectionPool().free(graph);	
//		}catch(SQLException e){
//			e.printStackTrace();
//		}
//		return user;
//	}
	
	public User getUser(String username) {
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
			conn = UserController.getConnectionPool().getConnection();			
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
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return user;
	}
	
	public User userAuthentication(String username,String pass){
		User user = null;
//		String sql = "sparql select ?userId ?pass "+
//				"from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
//				"where{ "+
//				  "?userId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>."+
//				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasUserName> \""+username+"\"."+
//				  "?userId <http://lsm.deri.ie/ont/lsm.owl#hasPassword> \""+pass+"\"."+
//				"}";		
		String sql = "select id,username,email,pass from ssc.dba.users where username='"+username+"' and pass='"+pass+"'";				
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					user = new User();
					user.setId(rs.getString("id"));
					user.setPass(pass);
					user.setEmail(rs.getString("email"));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return user;		
	}

	public void addNewUser(String username,String email, String pass) {
		// TODO Auto-generated method stub
		String triples = "";
		VirtGraph graph = null;
		long id = System.nanoTime();
//		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>.\n"+ 
//				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasUserName> \""+username+"\"."+
//				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasPassword> \""+pass+"\".";		
		String sql = "insert into ssc.dba.users values('"+id+"','"+email+"','"+username+"','"+pass+"')";
		try{
			conn= UserController.getConnectionPool().getConnection();			
//			String sql = "sparql insert into graph <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI+ ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Insert new user");
			UserController.attemptClose(ps);			
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
	}
}
