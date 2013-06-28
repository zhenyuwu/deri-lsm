package ssc.utils;

import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;

import ssc.graph.DatabaseUtilities;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class JSONUtil {
	
	public static String JSONToNTriple(JSONObject json){
		String triples = "";
//		String[] fields = {"s","p","o"};
		List fields = new ArrayList(); 
		JSONArray jArray = json.getJSONArray("bindings");
		for (int i = 0; i < jArray.size(); i++) {
			  JSONObject obj = jArray.getJSONObject(i);
			  if(i==0){
				  fields = Arrays.asList(obj.keySet().toArray()); 
			  }
			  String trip = "";
			  for(Object field:fields){
				  JSONObject j = obj.getJSONObject(field.toString());
				  if(j.getString("type").equals("uri")){
					  trip+="<" + j.getString("value") + "> ";
				  }else if(j.getString("type").equals("literal")){
					 trip+="\""+j.getString("value")+"\"^^<http://www.w3.org/2001/XMLSchema#string>";
				  }else if(j.getString("type").equals("typed-literal")){					  
					  trip+="\""+j.getString("value")+"\"^^<"+j.getString("datatype")+">";
				  }				  				  
			  }
			  trip+=".\n";
			  triples+=trip;
		}
//		System.out.println(triples);
		return triples;
	}
	
	public static void main(String[] agrs){
		String query = "select * from <http://lsm.deri.ie/sensordata#> where{?s ?p ?o.} limit 10";
		OutputStream out = DatabaseUtilities.getQueryResults(query, false);
		JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
		System.out.println(out);
		JSONObject j = json.getJSONObject("results");
		System.out.println(JSONToNTriple(json.getJSONObject("results")));
	}
	
}
