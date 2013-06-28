package deri.sensor.coreservlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import virtuoso.jena.driver.VirtGraph;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class ImageRequestServlet extends HttpServlet {
	  private ConnectionPool connectionPool;	  
	  @Override
	public void doGet(HttpServletRequest request,HttpServletResponse response)
	      throws ServletException, IOException {
	    try {
		    VirtGraph graph = connectionPool.getConnection();
		    String imageName = request.getParameter("name");
		    String imageType = request.getParameter("type");
		    String filename = (imageType.equals(SensorTypeEnum.webcam.toString())?
		    				ConstantsUtil.imageWebcam_path:ConstantsUtil.imageTraffic_path) + imageName;
		  
		    // Set content size
		    File file = new File(filename);
		    
		    response.setContentType("image/jpg");
		    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		    response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		    response.setContentLength((int)file.length());

		    // Open the file and output streams
		    FileInputStream in = new FileInputStream(file);
		    OutputStream out = response.getOutputStream();

		    // Copy the contents of the file to the output stream
		    byte[] buf = new byte[1024];
		    int count = 0;
		    while ((count = in.read(buf)) >= 0) {
		        out.write(buf, 0, count);
		    }
		    in.close();
		    out.close();
		    
		    connectionPool.free(graph);     
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	  }
	  
	  @Override
	public void init() {
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
 
	protected int initialConnections() {
	    return(1);
	}

	protected int maxConnections() {
	    return(1);
	}

}
