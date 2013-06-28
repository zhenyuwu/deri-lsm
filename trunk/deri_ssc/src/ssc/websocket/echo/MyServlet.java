package ssc.websocket.echo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import virtuoso.jena.driver.VirtGraph;

public class MyServlet extends HttpServlet {
	public static String realPath;

	  public void init(ServletConfig config) throws ServletException {
	    super.init(config);
	    realPath = this.getServletContext().getRealPath("WEB-INF");	   
	    
	  }	  
	  public void doGet(HttpServletRequest request,
              HttpServletResponse response)
			throws ServletException, IOException {
				String xmlResult = null;			
				//sensorManager = ServiceLocator.getSensorManager();
				String api = request.getParameter("api");				
				response.setContentType("text/xml");
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				PrintWriter out = response.getWriter();
				String title = "Connection Pool Test";
				out.println(xmlResult);
				out.close();
		}
}
