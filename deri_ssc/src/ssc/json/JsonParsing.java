package ssc.json;
import java.io.InputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

public class JsonParsing {

    public static void main(String[] args) throws Exception {
    	InputStream is = 
    		JsonParsing.class.getResourceAsStream( "sample-json.txt");
    	String jsonTxt = IOUtils.toString( is );
    	
    	JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );    	
    	JSONObject tripList = json.getJSONObject("TripList");
    	System.out.println(tripList.get("Line"));
    	JSONArray trips = tripList.getJSONArray("Trips");
    	System.out.println(trips);
    	for(int i=0;i<trips.size();i++){
    		JSONObject trip = trips.getJSONObject(i);
    		System.out.println(trip.get("Destination"));
    		System.out.println(trip.getJSONArray("Predictions"));
    	}
    }
}