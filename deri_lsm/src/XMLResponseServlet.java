
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import deri.sensor.hashmaps.util.VirtuosoConstantUtil;

public class XMLResponseServlet extends HttpServlet
{
 
  @Override
public void init(ServletConfig config) throws ServletException
  {
    super.init( config );
    try
      {
        // Initialization
      }
    catch ( Exception e )
      {
        e.printStackTrace();
      }
  }
  
  @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    processRequest( request, response );
  }

  @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    processRequest( request, response );
  }

  protected void processRequest(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException
  {
    String param = request.getParameter( "param" );
 
    // Implementation...

    VirtGraph graph = new VirtGraph(VirtuosoConstantUtil.placeQuadGraphURI,"jdbc:virtuoso://localhost:1111","dba","dba");
	String qry = "define input:storage "+ VirtuosoConstantUtil.placeQuadStorageURI+" \n"+
	"select * "+
		"where{ "+
		  "?s ?p ?o." +
		"}limit 100";
	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (qry, graph);
	ResultSet results = vqe.execSelect();
	//ResultSetFormatter.outputAsXML(System.out ,results);
	String xml = ResultSetFormatter.asXMLString(results);
    PrintWriter out = response.getWriter();
    response.setContentType( "text/xml" );
    response.setHeader( "Cache-Control", "no-cache" );
    out.println(xml);
//    out.println( "<response>" );
//    out.println( "<param>" + param + "</param>" );
//    out.println( "</response>" );
    out.close();
  }

}
