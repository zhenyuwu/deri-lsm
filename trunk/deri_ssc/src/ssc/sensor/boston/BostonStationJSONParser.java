package ssc.sensor.boston;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import ssc.beans.AbstractProperty;
import ssc.beans.Sensor;
import ssc.beans.SensorManager;
import ssc.utils.DateUtil;
import ssc.utils.MappingMap;
import ssc.utils.NumberUtil;
import ssc.utils.VirtuosoConstantUtil;
import ssc.wrapper.TriplesDataRetriever;
import ssc.wrapper.WebServiceURLRetriever;

public class BostonStationJSONParser {

	@SuppressWarnings("unchecked")
	public void getPredictionDetailedForOneStation(Sensor sensor){		
		SensorManager sensorManager = new SensorManager();
		try{
			String jsonStr = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());
			JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonStr);
			Date time = new Date();			
			
			String platform="";
			int trainNum = 0;
			int secondTo = 0;
			int timeTo = 0;
			boolean isOk = true;
			String currentLocation = "";
			String finaldestination="";
			
			HashMap hsm = new HashMap<String, String>();
    		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("railwaystation");
    		for(int i=0;i<lstObsP.get(0).size();i++){
    			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
    		}
    		
			JSONObject tripList = json.getJSONObject("TripList");
			platform = tripList.get("Line").toString();
//	    	System.out.println(platform);
	    	
	    	JSONArray trips = tripList.getJSONArray("Trips");
//	    	System.out.println(trips);
	    	for(int i=0;i<trips.size();i++){
	    		JSONObject trip = trips.getJSONObject(i);
	    		finaldestination = trip.get("Destination").toString();
	    		if (trip.containsKey("Position")){
	    			JSONObject position = trip.getJSONObject("Position");
	    			trainNum = position.getInt("Train");
	    			currentLocation = "("+ position.getString("Lat") +","+ position.getString("Long") +")";
	    		}else continue;
	    		JSONArray predictions = trip.getJSONArray("Predictions");
	    		for(int j=0;j<predictions.size();j++){
	    			JSONObject prediction = predictions.getJSONObject(j);
	    			if(sensor.getName().contains(prediction.getString("Stop"))){
	    				try{
	    					secondTo = prediction.getInt("Seconds");
	    				}catch(Exception e){
							isOk = false;
						}	
	    			}else continue;
	    			
		    		if(isOk==false) continue;
		    		String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
							Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
							Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");

					String observationId = System.nanoTime()+"";
					String triples = TriplesDataRetriever.getObservationTripleData(observationId,sensor.getId(), foi, time);
					
					AbstractProperty stationPlatform = new AbstractProperty();
					stationPlatform.setValue(platform);
					stationPlatform.setTimes(time);
	                String className = MappingMap.table2ClassURI.get("stationplatform");						                
	                stationPlatform.setPropertyName(className);
	                className = className.substring(className.lastIndexOf("#")+1);
	                stationPlatform.setObservedURL(hsm.get(className).toString());
	                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(stationPlatform.getPropertyName(), stationPlatform.getValue(), observationId, 
	                		stationPlatform.getObservedURL(), time);
	                
	                AbstractProperty train = new AbstractProperty();
					train.setValue(trainNum);
					train.setTimes(time);
	                className = MappingMap.table2ClassURI.get("trainnumber");						                
	                train.setPropertyName(className);
	                className = className.substring(className.lastIndexOf("#")+1);
	                train.setObservedURL(hsm.get(className).toString());
	                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(train.getPropertyName(), train.getValue(), observationId, 
	                		train.getObservedURL(), time);
	                
	                AbstractProperty se2Sta = new AbstractProperty();
	                se2Sta.setValue(secondTo);
	                se2Sta.setUnit("second");
	                se2Sta.setTimes(time);
	                className = MappingMap.table2ClassURI.get("secondtostation");						                
	                se2Sta.setPropertyName(className);
	                className = className.substring(className.lastIndexOf("#")+1);
	                se2Sta.setObservedURL(hsm.get(className).toString());
	                triples+=TriplesDataRetriever.getTripleDataHasUnit(se2Sta.getPropertyName(), se2Sta.getValue(), se2Sta.getUnit(),
	                		observationId, se2Sta.getObservedURL(), time);
	               	                              
	                AbstractProperty status = new AbstractProperty();
	                status.setValue(currentLocation);
	                status.setTimes(time);
	                className = MappingMap.table2ClassURI.get("status");						                
	                status.setPropertyName(className);
	                className = className.substring(className.lastIndexOf("#")+1);
	                status.setObservedURL(hsm.get(className).toString());
	                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(status.getPropertyName(), status.getValue(), observationId, 
	                		status.getObservedURL(), time);
	                
	                AbstractProperty destination = new AbstractProperty();
	                destination.setValue(finaldestination);
	                destination.setTimes(time);
	                className = MappingMap.table2ClassURI.get("destination");
	                destination.setPropertyName(className);
	                className = className.substring(className.lastIndexOf("#")+1);
	                destination.setObservedURL(hsm.get(className).toString());
	                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(destination.getPropertyName(), destination.getValue(), observationId, 
	                		destination.getObservedURL(), time);
	                System.out.println(triples);
	                sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
	    		}
	    	}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SensorManager sensorManager = new SensorManager();
		Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId("http://lsm.deri.ie/resource/220231513400915");
		BostonStationJSONParser pre = new BostonStationJSONParser();
		pre.getPredictionDetailedForOneStation(sensor);
//		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());
		//PredictionDetailedXMLParser.getPredictionDetailedElementFromXMLUrl(xml);
		
	}
}
