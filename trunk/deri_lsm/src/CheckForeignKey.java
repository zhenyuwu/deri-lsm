import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import virtuoso.jdbc3.PreparedStatementWrapper;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;


import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.manager.SensorManager;


public class CheckForeignKey {
	 
	  public static Connection getVirtuosoConneciton(){
			try{
				String url = "jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2";
			    String username = "dba";
			    String password = "dba";
				Class.forName("virtuoso.jdbc4.Driver");
				Connection conn = DriverManager.getConnection(url,username,password);
				System.out.println("Load successfull");
				return conn;
			}catch (Exception e){
				System.out.println("Connection failed"+ e.getMessage());
				return null;
			}
	 }
	 
	  public static void TestSQL_Enable() throws SQLException{
		 Connection conn = getVirtuosoConneciton();
		 String query = "log_enable(2);"+
		 		"select top 10 id "+
		 		" from deri.dba.sensor";
		 PreparedStatement pre = conn.prepareStatement(query);
		 
		 Statement st = conn.createStatement();
		 boolean ok = st.execute(query);
		 java.sql.ResultSet rs = st.getResultSet();
		 while (rs.next()){
			 System.out.println(rs.getString(1));
		 }
		 conn.close();
	  }
	  
	 public static void TestHibernateGraph() throws SQLException{
		 
		//VirtGraph graph = new VirtGraph(VirtuosoConstantUtil.placeQuadGraphURI,"jdbc:virtuoso://localhost:1111","dba","dba");
		VirtGraph graph = new VirtGraph("jdbc:virtuoso://localhost:1111","dba","dba");
		String qry = "define input:storage "+ VirtuosoConstantUtil.placeQuadStorageURI+" \n"+
		"select * "+
			"where{ "+
			  "<http://lsm.deri.ie/resource/8a4481ca31f896060131f8976a403df0> ?p ?o." +
			"}limit 100";
		graph.setReadFromAllGraphs(true);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (qry, graph);		
		ResultSet results = vqe.execSelect();
		ResultSetFormatter.outputAsJSON(System.out ,results);			
//		Connection conn = getVirtuosoConneciton();
//		PreparedStatement ps = conn.prepareStatement(qry);
//		java.sql.ResultSet rs = ps.executeQuery();
		//ResultSetFormatter.outputAsXML(System.out ,rs);
	 }
//	 
//	 public static void TestResultSetGraph() throws SQLException{
//		 Connection conn = getVirtuosoConneciton();
//		 System.out.println("Got Connection.");
//		 String query = "sparql "+
//		 		"select ?s ?p ?o "+
//		 		" from <http://testmetadata/data#> \n" +
//		 		"where "+
//		 		"{ ?s ?p ?o. "+ 
//		 		"}";
//
//		 Statement st = conn.createStatement();
//		 boolean ok = st.execute(query);
//		 ResultSetMetaData data = st.getResultSet().getMetaData();
//		 ResultSet rs = st.getResultSet();
//		 VirtuosoResultSet temp = (VirtuosoResultSet)rs;
//		 while(temp.next()){
//			 //System.out.println(temp.getObject(3));
//			 ArrayList<Node> arrNode = new ArrayList();
//			 for(int i = 1;i <= data.getColumnCount();i++)
//			    {				
//				 Node n=null;
//				 Object obj = temp.getObject(i);
//				 System.out.println(obj);
//				 System.out.println(obj.getClass());
//				 if (obj instanceof VirtuosoExtendedString) // String representing an IRI
//				  {
//				    VirtuosoExtendedString vs = (VirtuosoExtendedString) obj;
//				    
//		            if (vs.iriType == VirtuosoExtendedString.IRI && (vs.strType & 0x01) == 0x01){
//		            	//System.out.println ("<" + vs.str +">");
//		            	n = Node.createURI(vs.str);		            	
//		            }
//				    else if (vs.iriType == VirtuosoExtendedString.BNODE)				    
//					System.out.println ("<" + vs.str +">");
//				  }
//				else if (obj instanceof VirtuosoRdfBox) // Typed literal
//				  {
//				    VirtuosoRdfBox rb = (VirtuosoRdfBox) obj;
//				    //System.out.println (rb.rb_box + " lang=" + rb.getLang() + " type=" + rb.getType());
//
//				  }				
//				else if (obj instanceof Double)
//					n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdouble);
//				else if (obj instanceof Date)
//					n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdateTime);
//				else if (obj instanceof String)
//					n = Node.createURI(obj.toString());
//				 try{
//					 String format=" ";
//				 	 if(n!=null&&n.toString().contains("^")){
//				 		 int indx = n.toString().lastIndexOf("^");
//				 		 String subN = n.toString().substring(0, indx+1);
//				 		 System.out.println(subN);
//				 		 System.out.println(n.toString().substring(indx+1));
//				 		 format = n.toString().substring(0, indx+1)+"<"+n.toString().substring(indx+1)+">";
//				 		
//				 		//format="<"+ + ">";
//				 		System.out.println(format);
//				 		System.out.println(obj);
//				 	}
//				 }catch(Exception e){
//					 e.printStackTrace();
//					 continue;
//				 }
//				 
//					 
//				 arrNode.add(n);
//			}
//			
//			 //System.out.println(arrNode);
//		 }	 
//		 
//		 conn.close();
//	 }
	 
	 public static void testSparqlEndPointService() throws UnsupportedEncodingException{
		 String service ="http://linkedgeodata.org/sparql";
		 QueryExecution qe;
		 String sparql;
		 
//		 String queryString = " select ?id bif:st_distance(?geo,<bif:st_point>(55.46667098999023,8.449999809265137)) as ?distance " 
//			 			+"From <http://linkedgeodata.org> "
//			 			+"where { "
//			 			+"?id geo:geometry ?geo. "
//			 			+"filter (<bif:st_intersects>(?geo,<bif:st_point>(55.46667098999023,8.449999809265137),350.0))."
//			 			+"} order by ?distance limit 5";
		 String queryString="select ?s ?p ?o from <http://linkedgeodata.org> where "+ 
		 						"{?s ?p ?o."+
		 						"}limit 20";
		 String queryEncode = URLEncoder.encode(queryString,"UTF-8");
		 System.out.println(queryEncode);
		 //Query query = QueryFactory.create(queryEncode);			
		 //Query query = QueryFactory.create(queryString);
		 //QueryExecution vqe = QueryExecutionFactory.sparqlService(service, query);
     	 //QueryExecution vqe = VirtuosoQueryExecutionFactory.sparqlService(service,query);
		 QueryExecution vqe = new QueryEngineHTTP(service, queryString);
		 ResultSet results = vqe.execSelect(); 
		 while(results.hasNext()){
			 QuerySolution qs = (QuerySolution)results.next();
			 System.out.println(qs.get("s"));
		 }

	}
	 
	 public static void main(String[] args) throws Exception {
		    //TestHibernateGraph();
		 //TestSQL_Enable();
		 	//TestResultSetGraph();
			//testSensormanagerHibernateGraph();			
//		    testSparqlEndPointService();
//		Connection conn = getVirtuosoConneciton();
//		conn.close();
//		 String url;
//			if(args.length == 0)
//			    url = "jdbc:virtuoso://localhost:1111";
//			else
//			    url = args[0];
//
//	/*			STEP 1			*/
//			VirtGraph set = new VirtGraph (url, "dba", "dba");
//
//	/*			STEP 2			*/
//
//
//	/*			STEP 3			*/
//	/*		Select all data in virtuoso	*/
//			Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 10");
//
//	/*			STEP 4			*/
//			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, set);
//
//			ResultSet results = vqe.execSelect();
//			while (results.hasNext()) {
//				QuerySolution result = results.nextSolution();
//			    RDFNode graph = result.get("graph");
//			    RDFNode s = result.get("s");
//			    RDFNode p = result.get("p");
//			    RDFNode o = result.get("o");
//			    System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
//			}
		 SensorManager sensormanager = ServiceLocator.getSensorManager();
		}
	 
	 
}
