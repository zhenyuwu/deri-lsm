package deri.sensor.boston;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.NumberUtil;
import deri.sensor.wrapper.TriplesDataRetriever;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class BostonTrafficCamParser {

	private static String source = "http://www1.eot.state.ma.us/xmltrafficfeed/camsXML.aspx";
	
	
	@SuppressWarnings("unchecked")
	public static void getTrafficElementFromUrl(String xml){
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		User user = userManager.getUser("admin");
		try{
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			for(Element inner_element : elements){
				try{															
					if(inner_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.cam))){
						String name = "";
						String active="";
						String description = "";
						boolean isOk = true;
						String location = "";
						String sourceType = "Boston traffic camera";
						int updateInterval = 5;
						String pic = "";
						double lat = 0;
						double lng = 0;
						List<Element> item_elements = inner_element.elements();
						for(Element item_element:item_elements){
							//System.out.println(item_element.getPath());
							String content = item_element.getTextTrim();		
							if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.active))){
								active = item_element.getTextTrim();	
								if(active.equals("false")){
									isOk = false;
									break;
								}
							}else if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.name))){
								name = item_element.getTextTrim();								
							}else if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.image))){
								pic = content;
							}else if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.description))){
								int idx = content.lastIndexOf(">");
								description = content.substring(idx+1).trim();
							}else if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.geo_lat))){
								if(NumberUtil.isDouble(content)){
									lat = Double.parseDouble(content);
								}else{
									isOk = false;
									break;
								}
							}else if(item_element.getPath().equals(BostonTrafficCamXMLSignMap.assembleTrafficSign(BostonTrafficCamXMLSign.geo_long))){
								if(NumberUtil.isDouble(content)){
									lng = Double.parseDouble(content);
								}else{
									isOk = false;
									break;
								}
							}	
						}
						if(isOk == false) continue;								
						Place place = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
						boolean isNewSensor = false;								
						Sensor sensor = null;
						if(place == null){
							String city = "Boston";
							String province = "Massachusetts";
							String country = "USA";
							String continent = "America";
						
							place = new Place();
							place.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
							place.setAuthor("admin");
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
						if(sensor==null||!sensor.getSensorType().equals("traffic")){
							isNewSensor = true;
							sensor = new Sensor();
							sensor.setId(VirtuosoConstantUtil.sensorObjectDataPrefix+System.nanoTime());
							sensor.setName(name);
							sensor.setAuthor("admin");
							sensor.setSensorType("traffic");
							sensor.setSourceType(sourceType);
							sensor.setInfor(description + ". update every " + Integer.toString(updateInterval) + " minutes");
							sensor.setSource(pic);
							sensor.setTimes(new Date());
							sensor.setPlace(place);
							sensor.setUser(user);
						}

						if(isNewSensor){							    	 
							 String newSensorTriples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
							 System.out.println(newSensorTriples);
							 sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, newSensorTriples);								    	 								    	 
							    	 sensorManager.runSpatialIndex();
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
			String url = "http://www1.eot.state.ma.us/xmltrafficfeed/camsXML.aspx";
			String dataIn = WebServiceURLRetriever.RetrieveFromURL(url);
			//System.out.println(dataIn);
			BostonTrafficCamParser.getTrafficElementFromUrl(dataIn);
	}

}
