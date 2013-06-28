package deri.sensor.coreservlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import deri.sensor.utils.ConstantsUtil;

import virtuoso.jena.driver.VirtGraph;

import java.sql.*;
import java.util.ArrayList;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserRequestServlet extends HttpServlet {
  private ConnectionPool connectionPool;
  //private SensorManager sensorManager  = ServiceLocator.getSensorManager();
  
  @Override
public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
      throws ServletException, IOException {
    String xmlResult = null;
    try {
    	//sensorManager = ServiceLocator.getSensorManager();
    	String api = request.getParameter("api");
    	//UserFeed userFeed = sensorManager.getUserFeedWithFeedId(feedId);
    	//ServletContext sc = getServletContext();
    	//System.out.println(sc.getRealPath("image10092011061811.jpg"));
	    VirtGraph graph = connectionPool.getConnection();
	    if(api.equals("xmlfeed")){
	    	String feedId = request.getParameter(ConstantsUtil.data_feed_id);
		    ArrayList<String> para = DatabaseUtilities.getQueryResults(graph.getConnection(), feedId, false);
		    xmlResult = DatabaseUtilities.getQueryResults(graph,para, false);
	    }else if(api.equals("discover")){
	    	String userId = request.getParameter("userId");
	    	String strLat = request.getParameter("lat");
	    	String strLong = request.getParameter("long");
	    	xmlResult = DatabaseUtilities.getSensorAroundLocation(graph.getConnection(),strLat,strLong,false);
	    }else if(api.equals("latestdata")){
	    	String sensorId = request.getParameter("id");
	    	String sensorType = request.getParameter("type");	    	
	    	xmlResult = DatabaseUtilities.getLatestSensorDataForFeed(graph.getConnection(),sensorId, sensorType);
	    }
	    connectionPool.free(graph);     
    } catch(Exception e) {
    	e.printStackTrace();
    	xmlResult = "Error: " + e;
    }
    response.setContentType("text/xml");
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
    PrintWriter out = response.getWriter();
    String title = "Connection Pool Test";
    out.println(xmlResult);
    out.close();
  }

  /** Initialize the connection pool when servlet is
   *  initialized. To avoid a delay on first access, load
   *  the servlet ahead of time yourself or have the
   *  server automatically load it after reboot.
   */
  
  @Override
public void init() {
    //String host = "jdbc:virtuoso://localhost:1111";
	String host = ConstantsUtil.urlHost;
    String username = "dba";
    String password = "dba";
    try {
      connectionPool =
        new ConnectionPool(host, username, password,
                           initialConnections(),
                           maxConnections(),
                           true);
    } catch(SQLException sqle) {
      System.err.println("Error making pool: " + sqle);
      getServletContext().log("Error making pool: " + sqle);
      connectionPool = null;
    }
  }

  @Override
public void destroy() {
    connectionPool.closeAllConnections();
  }

  /** Override this in subclass to change number of initial
   *  connections.
   */
  
  protected int initialConnections() {
    return(1);
  }

  /** Override this in subclass to change maximum number of 
   *  connections.
   */

  protected int maxConnections() {
    return(1);
  }
}