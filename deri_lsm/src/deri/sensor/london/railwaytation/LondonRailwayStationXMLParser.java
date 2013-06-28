package deri.sensor.london.railwaytation;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LondonRailwayStationXMLParser {

private static String source = "/root/SensorDatabase/Station locations.kml";
	

	@SuppressWarnings("unchecked")
	public static void getLondonRailwayStationElementFromXMLFile(){
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		User user = userManager.getUser("admin");
		try{
			Document document = null;
		    SAXReader reader = new SAXReader();
	        document = reader.read(source);
			
			Element root = document.getRootElement();			
			Date time = new Date();			
			List<Element> elements = root.elements();
			for(Element element:elements){
				if(element.getName().equals(LondonRailwayStationXMLSign.Document.toString())){
					List<Element> inner_elements = element.elements();
					for(Element inner_element : inner_elements){
						try{													
							System.out.println(inner_element.getName());
							if(inner_element.getName().equals(LondonRailwayStationXMLSign.Placemark.toString())){
								String name = "";								
								String description = "";
								String code="";
								boolean isOk = true;
								String sourceType = "";
								double lat = 0;
								double lng = 0;
							
								List<Element> item_elements = inner_element.elements();
								for(Element item_element:item_elements){
									//System.out.println(item_element.getPath());
									String content = item_element.getTextTrim();
									//System.out.println(content);									
									if(item_element.getName().equals(LondonRailwayStationXMLSign.name.toString())){
										name = item_element.getTextTrim();								
									}else if(item_element.getName().equals(LondonRailwayStationXMLSign.description.toString())){
										description=content;
									}else if(item_element.getName().equals(LondonRailwayStationXMLSign.code.toString())){
										code = content;
									}else if(item_element.getName().equals(LondonRailwayStationXMLSign.Point.toString())){
										List<Element> coordElements = item_element.elements();
										for(Element coordElement:coordElements){
											if(coordElement.getName().equals(LondonRailwayStationXMLSign.coordinates.toString())){												
												String[] coordinates = coordElement.getTextTrim().split(",");
												if(NumberUtil.isDouble(coordinates[0])){
													lng = NumberUtil.formatWithNDigits(coordinates[0],10);
												}else
													isOk = false;
												if(NumberUtil.isDouble(coordinates[1])){
													lat = NumberUtil.formatWithNDigits(coordinates[1],10);
												}else
													isOk = false;
											}			
										}
									}
								}
								if(isOk == false) continue;								
								Place place = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
//								place.setLat(NumberUtil.formatWithNDigits(Double.toString(lat),10));
//								place.setLng(NumberUtil.formatWithNDigits(Double.toString(lng),10));
//								place.setCity(name+",London");
//								placeManager.updatePlace(place);
								Sensor sensor = null;
								if(place == null){
									String city = "London";
									String province = "London";
									String country = "United Kingdom";
									String continent = "Europe";
								
									place = new Place();
									place.setAuthor("admin");
									place.setLat(lat);
									place.setLng(lng);
									place.setCity(city);
									place.setProvince(province);
									place.setCountry(country);
									place.setContinent(continent);
									place.setZipcode("SW1A 2");
									place.setInfor(description);
								}else
									sensor = sensorManager.getSpecifiedSensorWithPlaceId(place.getId());
								if(sensor==null){
									sensor = new Sensor();
									sensor.setName(name);
									sensor.setAuthor("admin");
									sensor.setSensorType("railwaystation");
									sensor.setSourceType("");
									sensor.setInfor("London RailwayStation");
									sensor.setCode(code);
									sensor.setSource(source);
									sensor.setTimes(time);
									sensor.setPlace(place);
									sensor.setUser(user);
									
									sensorManager.addObject(sensor);
								}
							}
						}catch(Exception e){
							e.printStackTrace();
							continue;
						}
					}
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
		LondonRailwayStationXMLParser.getLondonRailwayStationElementFromXMLFile();
		
	}

}
