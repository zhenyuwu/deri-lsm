package deri.sensor.boston;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;
import deri.sensor.wrapper.TriplesDataRetriever;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class BostonCycleHireParser {
	private static String url = "http://capitalbikeshare.com/data/stations/bikeStations.xml";
//	private static String url = "http://www.thehubway.com/data/stations/bikeStations.xml";
	
	@SuppressWarnings("unchecked")
	public static void getCycleHireElementFromUrl(String xml){
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		User user = userManager.getUser("admin");
		String triples = "";
		try{
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();			
			Date time = new Date();
			Attribute attribute = root.attribute("lastUpdate");
			List<Element> inner_elements = root.elements();
			for(Element inner_element : inner_elements){
				try{													
					//System.out.println(inner_element.getName());
					if(inner_element.getName().equals(BostonCycleHireSign.station.toString())){
						String name = "";								
						String description = "";
						boolean isOk = true;
						String location = "";
						String sourceType = "Washington DC BikeHire";
						double lat = 0;
						double lng = 0;
						int bikeAvailable = 0;
						int bikedockAvailable = 0;
						
						List<Element> item_elements = inner_element.elements();
						for(Element item_element:item_elements){
							//System.out.println(item_element.getPath());
							String content = item_element.getTextTrim();
							//System.out.println(content);									
							if(item_element.getName().equals(BostonCycleHireSign.name.toString())){
								name = item_element.getTextTrim();								
							}else if(item_element.getName().equals(BostonCycleHireSign.terminalName.toString())){
								description+="TerminalName: "+content+", ";
							}else if(item_element.getName().equals(BostonCycleHireSign.installed.toString())){										
								description +="installed:"+content+", ";
							}else if(item_element.getName().equals(BostonCycleHireSign.locked.toString())){										
								description+="locked:"+content+", ";
							}else if(item_element.getName().equals(BostonCycleHireSign.temporary.toString())){										
								description+="temporary:"+content;
							}else if(item_element.getName().equals(BostonCycleHireSign.lat.toString())){										
								if(NumberUtil.isDouble(content)){
									lat = Double.parseDouble(content);
								}else
									isOk = false;
							}else if(item_element.getName().equals("long")){										
								if(NumberUtil.isDouble(content)){
									lng = Double.parseDouble(content);
								}else
									isOk = false;											
							}else if(item_element.getName().equals(BostonCycleHireSign.nbBikes.toString())){										
								if(NumberUtil.isInteger(content)){
									bikeAvailable = Integer.parseInt(content);
								}else
									isOk = false;
							}else if(item_element.getName().equals(BostonCycleHireSign.nbEmptyDocks.toString())){										
								if(NumberUtil.isInteger(content)){
									bikedockAvailable = Integer.parseInt(content);
								}else
									isOk = false;
							}
						}
						if(isOk == false) continue;								
						Place place = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
						boolean isNewSensor = false;
						boolean isNewPlace = false;
						Sensor sensor = null;
						if(place == null){
							isNewPlace = true;
							String city = "Washington DC";
							String province = "Washington DC";
							String country = "USA";
							String continent = "America";
						
							place = new Place();
							place.setAuthor("admin");
							place.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
							place.setLat(lat);
							place.setLng(lng);
							place.setCity(city);
							place.setProvince(province);
							place.setCountry(country);
							place.setContinent(continent);
							place.setZipcode("");
							place.setInfor(location);
						}else
							sensor = sensorManager.getSpecifiedSensorWithPlaceId(place.getId());
						if(sensor==null){
							isNewSensor = true;
							sensor = new Sensor();
							sensor.setName(name);
							sensor.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
							sensor.setAuthor("admin");
							sensor.setSensorType("bikehire");
							sensor.setSourceType(sourceType);
							sensor.setInfor(description);
							sensor.setSource(url);
							sensor.setTimes(time);
							sensor.setPlace(place);
							sensor.setUser(user);
						}
						HashMap hsm = new HashMap<String, String>();
			    		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("bikehire");
			    		for(int i=0;i<lstObsP.get(0).size();i++){
			    			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
			    		}
						
			    		String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
								Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
								Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");
						String observationId = System.nanoTime()+"";
						triples = TriplesDataRetriever.getObservationTripleData(observationId,sensor.getId(), foi, time);
												
						AbstractProperty bikeAvail = new AbstractProperty();
						bikeAvail.setValue(bikeAvailable);
						bikeAvail.setTimes(time);
		                String className = MappingMap.table2ClassURI.get("bikeavailability");
		                bikeAvail.setPropertyName(className);
		                
//		                className = className.substring(className.lastIndexOf("#")+1);
		                bikeAvail.setObservedURL(hsm.get(className).toString());
		                bikeAvail.setUnit("bike");
		                triples+=TriplesDataRetriever.getTripleDataHasUnit(bikeAvail.getPropertyName(), bikeAvail.getValue(),bikeAvail.getUnit(),
		                					observationId, bikeAvail.getObservedURL(), time);
						
		                AbstractProperty bikedockAvail = new AbstractProperty();
		                bikedockAvail.setValue(bikedockAvailable);
		                bikedockAvail.setUnit("dock");
		                bikedockAvail.setTimes(time);
		                className = MappingMap.table2ClassURI.get("bikedockavailability");		                
		                bikedockAvail.setPropertyName(className);
//		                className = className.substring(className.lastIndexOf("#")+1);
		                bikedockAvail.setObservedURL(hsm.get(className).toString());
		                triples+=TriplesDataRetriever.getTripleDataHasUnit(bikedockAvail.getPropertyName(), bikedockAvail.getValue(),bikedockAvail.getUnit(),
            					observationId, bikedockAvail.getObservedURL(), time);
		                
			            Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
						if(newest == null || DateUtil.isBefore(newest.getTimes(), time)){							
							if(isNewSensor){							    	 
								 String newSensorTriples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
//								 System.out.println(newSensorTriples);
								 sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, newSensorTriples);								    	 								    	 
//						    	 sensorManager.runSpatialIndex();
						     }
//							System.out.println(triples);
							sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI,triples);
						}		
					}
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
			String url = "http://capitalbikeshare.com/data/stations/bikeStations.xml";
			String dataIn = WebServiceURLRetriever.RetrieveFromURL(url);
			//System.out.println(dataIn);
			BostonCycleHireParser.getCycleHireElementFromUrl(dataIn);
	}
}
