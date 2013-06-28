package deri.virtuoso.manager;

import virtuoso.jena.driver.VirtGraph;

public class QuadStorageManager {
	private final String urlHost ="jdbc:virtuoso://localhost:1111";
	private String username = "dba";
	private String password = "dba";
	private VirtGraph graph;
	private String type;
	
	public void initialize_graphConnection(){
//		graph = new VirtGraph(VirtuosoConstantUtil.quadGraphList.get(type),
//				urlHost,username,password);
		//graph = new VirtGraph("http://www.openlinksw.com/schemas/virtrdf#",urlHost,username,password);
	}
	
	public void closeGraphConnection(){
		graph.close();
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
//	public ResultSet getAllDataWithObservationId(String obsId){
//		String sparql = "define input:storage "+VirtuosoConstantUtil.sensormasherDataQuadURI + "\n"+
//		"select ?s ?p ?o "+
//			"where{ \n" + 
//			   "{?s ?p ?o. " +
//			   " ?s <http://purl.oclc.org/NET/ssnx/ssn#hasObservation> '"+obsId+"' \n" +
//			  "}\n" +
//			  "union{\n"+
//			    "?s ?p ?o.\n"+
//			    "filter regex(?s,'"+obsId+"')."+
//			  "}"+
//			"}"; 
//		graph = new VirtGraph(urlHost,username,password);
//		graph.setReadFromAllGraphs(true);        
//        QueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, new VirtModel(graph));
//		ResultSet results = vqe.execSelect();		
//		return results;
//	}
}
