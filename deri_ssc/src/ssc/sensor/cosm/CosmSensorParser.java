package ssc.sensor.cosm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import ssc.beans.Observation;
import ssc.beans.Place;
import ssc.beans.PlaceManager;
import ssc.beans.Sensor;
import ssc.beans.SensorManager;
import ssc.beans.User;
import ssc.beans.UserActiveManager;
import ssc.utils.DateUtil;
import ssc.utils.VirtuosoConstantUtil;
import ssc.wrapper.TriplesDataRetriever;
import ssc.wrapper.WebServiceURLRetriever;


public class CosmSensorParser {

	@SuppressWarnings("unchecked")
	public static void parseDataElementsFromCosmXML(String xml){
		if(xml == null || xml.trim().equals("")){
			return;
		}
		
		SensorManager sensorManager = new SensorManager();
		PlaceManager placeManager = new PlaceManager();
		UserActiveManager userManager = new UserActiveManager();
		User user = userManager.getUser("admin");
		Document document;
		try {
			document = DocumentHelper.parseText(xml);
		
		Element root = document.getRootElement();
		List<Element> elements = root.elements();
//		System.out.println(root.getName());
		for(Element element : elements){
			try{
//				System.out.println(element.getPath());
				if(element.getPath().equals(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.environment).toString())){
					String name = "";
					String source = "";
					Date time = DateUtil.standardString2Date(element.attribute(CosmSensorSign.updated.toString()).getStringValue());
					String creator = element.attribute(CosmSensorSign.creator.toString()).getStringValue() ;
					Date created = DateUtil.standardString2Date(element.attribute(CosmSensorSign.created.toString()).getStringValue());
					
					String sourceType = "";
					String tag="";
					String strValue="";
					String unit="";
					String website="";
					String description="";
					
					double lat = 0;
					double lng = 0;
					
					Node child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.title).toString());
					name = child.getStringValue();
					child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.feed).toString());
					if (child!=null) source = child.getStringValue();
					else return;
					child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.website).toString());
					if (child!=null) website = child.getStringValue();
					child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.description).toString());
					if (child!=null) description = child.getStringValue();
					
					child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.lat).toString());
					lat = Double.parseDouble(child.getStringValue());
					child = element.selectSingleNode(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.lng).toString());
					lng = Double.parseDouble(child.getStringValue());
										
					
					Place place = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
					Sensor sensor = sensorManager.getSpecifiedSensorWithSource(source);
					boolean isNewSensor = false;
					if(sensor==null){						
						if(place == null){			
							place = new Place();
							place.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
							place.setAuthor("admin");
							place.setLat(lat);
							place.setLng(lng);
							place.setCity("");
							place.setProvince("");
							place.setCountry("");
							place.setContinent("");
							place.setZipcode("");
						}
						isNewSensor = true;
						sensor = new Sensor();
						sensor.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
						sensor.setAuthor(creator);
						sensor.setName(name);
						sensor.setSensorType("cosm");
						sensor.setSourceType(sourceType);
						sensor.setInfor(description);
						sensor.setSource(source);
						sensor.setTimes(created);
						sensor.setPlace(place);
						sensor.setUser(user);						
					}
					
					if(isNewSensor){
				    	 String newSensorTriples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
				    	 System.out.println(newSensorTriples);
				    	 sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, newSensorTriples);								    	 								    	 
				    	 sensorManager.runSpatialIndex();								    	 
				     }
					
					HashMap hsm = new HashMap<String, String>();
		    		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("cosm");
		    		for(int i=0;i<lstObsP.get(0).size();i++){
		    			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
		    		}		    		
									
					String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
							Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
							Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");

					String observationId = System.nanoTime()+"";
					String triples = TriplesDataRetriever.getObservationTripleData(observationId,sensor.getId(), foi, time);
					String className = "http://purl.oclc.org/NET/ssnx/ssn#Property";
					className = className.substring(className.lastIndexOf("#")+1);
					String observedURL = hsm.get(className).toString();
					
					
					List<Element> inner_elements = element.elements(CosmSensorSign.data.toString());
					for(Element inner_element : inner_elements){
						List<Element> els = inner_element.elements();
						boolean isFirstTag=true;
						for(Element el : els){
							if(el.getPath().equals(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.tag).toString())&&isFirstTag){
								tag = el.getStringValue();
								isFirstTag=false;								
							}else if(el.getPath().equals(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.value).toString())){							
								strValue =  el.getStringValue();								
							}
							else if(el.getPath().equals(CosmSensorSignMap.getCosmXMLSign(CosmSensorSign.unit).toString())){
//								System.out.println(el.getStringValue());
								unit =  el.getStringValue().equals("°C")?"C":el.getStringValue();	
//								System.out.println(unit);
							}
						}
						triples+=TriplesDataRetriever.getTripleComplexTypeData(className, tag, strValue,unit,observationId, 
								observedURL, time);
					}			

		            Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
					if(newest == null || DateUtil.isBefore(newest.getTimes(), time)){
						System.out.println(triples);
						sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
					}						
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "https://api.cosm.com/v2/feeds/49701.xml";
		String xml = WebServiceURLRetriever.RetrieveFromURLWithAuthentication(url,"nmqhoan","nmqhoan");
		CosmSensorParser.parseDataElementsFromCosmXML(xml);
	}

}
