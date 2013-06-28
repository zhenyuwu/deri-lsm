package ssc.graph;
import java.sql.SQLException;

import ssc.utils.ConstantsUtil;


public class Container {
	public static ConnectionPool connectionPool;
	/**
	 * @param args
	 */
	static{
		init();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		init();
		
	}
		
	public static void init() {
//	    String host = "jdbc:virtuoso://localhost:1111";		
	    String username = "dba";
	    String password = "dba"; 
	    String driver="virtuoso.jdbc4.Driver";
	    try {
	      connectionPool =
	        new ConnectionPool(ConstantsUtil.urlHost, username, password,
	                           initialConnections(),
	                           maxConnections(),
	                           true);
	    } catch(SQLException sqle) {
	      System.err.println("Error making pool: " + sqle);
	      connectionPool = null;
	    }
	  }
	
	
	protected static int initialConnections() {
	    return(3);
	  }

	  /** Override this in subclass to change maximum number of 
	   *  connections.
	   */

	  protected static int maxConnections() {
	    return(3);
	  }
}
