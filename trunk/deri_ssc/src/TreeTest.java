import java.io.ByteArrayOutputStream;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import ssc.beans.Block;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


public class TreeTest {

	public static Block createTree(String jsonStr){
		JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonStr );
		Block block = new Block();
		block.setId(json.optString("id"));
		
		if (json.containsKey("sparql"))
			block.setSparql(json.getString("sparql")==null?"":json.getString("sparql"));
		block.setType(json.getString("type"));
		JSONArray childJArr = json.getJSONArray("childs");
		for(int i = 0;i<childJArr.size();i++){
			JSONObject jo = childJArr.getJSONObject(i);
			Block child = createTree(jo.toString());
			child.setParent(block);
			block.addChild(child);
		}
		return block;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String json = "{\"filterArr\":[],\"childs\":[{\"filterArr\":[],\"childs\":[],\"type\":\"COSM\",\"category\":\"LSMSensor\",\"x\":405,\"y\":754,\"id\":\"div-9\",\"socketURL\":\"ws://localhost:8080/SuperStreamCollider/websocket/LSMSensorMessage\",\"sparql\":\"\"},{\"filterArr\":[],\"childs\":[{\"filterArr\":[],\"childs\":[{\"filterArr\":[],\"childs\":[],\"type\":\"Location\",\"category\":\"Operators\",\"x\":84,\"y\":727,\"id\":\"div-4\"}],\"type\":\"EndPoint\",\"category\":\"Sources\",\"x\":88,\"y\":522,\"id\":\"div-6\",\"socketURL\":\"ws://localhost:8080/SuperStreamCollider/websocket/EndPointMessage\"}],\"type\":\"ToRDFStream\",\"category\":\"Operators\",\"x\":569,\"y\":342,\"id\":\"div-3\",\"socketURL\":\"ws://localhost:8080/SuperStreamCollider/websocket/CqelsMessage\"},{\"filterArr\":[],\"childs\":[{\"filterArr\":[],\"childs\":[],\"type\":\"Weather\",\"category\":\"LSMSensor\",\"x\":243,\"y\":14,\"id\":\"div-0\",\"socketURL\":\"ws://localhost:8080/SuperStreamCollider/websocket/LSMSensorMessage\",\"sparql\":\"\"},{\"filterArr\":[],\"childs\":[],\"type\":\"COSM\",\"category\":\"LSMSensor\",\"x\":83,\"y\":121,\"id\":\"div-1\",\"socketURL\":\"ws://localhost:8080/SuperStreamCollider/websocket/LSMSensorMessage\",\"sparql\":\"\"}],\"type\":\"Merge\",\"category\":\"Operators\",\"x\":569,\"y\":108,\"id\":\"div-2\"}],\"type\":\"Merge\",\"category\":\"Operators\",\"x\":966,\"y\":227,\"id\":\"div-8\"}";
//		Block root = createTree(json);
//		System.out.println(root.getChilds());
		
		String service = "http://lsm.deri.ie/sparql";
    	String query = "select * where{?s ?p ?o.}limit 10";
    	
		QueryExecution vqe = new QueryEngineHTTP(service, query);
		ResultSet results = vqe.execSelect(); 
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();; 
	    ResultSetFormatter.outputAsJSON(out,results);
	    System.out.println(out);
	}

}
