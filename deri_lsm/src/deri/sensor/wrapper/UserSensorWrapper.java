package deri.sensor.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;
import deri.sensor.userdefined.UserSensorXMLParser;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.ThreadUtil;
import deri.sensor.utils.XMLUtil;


public class UserSensorWrapper extends Thread{
	private User user;
	private String type;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	
	public UserSensorWrapper(){		
	}
	
	public UserSensorWrapper(String sensorType,User user){
		this.user = user;
		this.type = sensorType;
	}
	
	@Override
	public void run(){
		long count = 0;
		System.out.println("----------------------------------- User sensor update_"+this.getName()+" has started -----------------------------------");
		SensorManager sensorManager = ServiceLocator.getSensorManager();
//		SensorSourceType sourceTypeObj = sensorManager.getXMLMapForSpecifiedSourceType(type);
//		System.out.println(getMapFromXMLContent(sourceTypeObj.getSourceXMLContent()));
		for(;;){
		List<Sensor> sources = sensorManager.getAllSensorForSpecfiedUserAndType(type,user.getId());
			postUserSensorElement(sources);
			System.out.println("user sensor "+this.getName()+":\torder " + ++count + " has finished");
			ThreadUtil.sleepForHours(1);
		}		
	}
	
	
	@SuppressWarnings("unchecked")
	public void postUserSensorElement(List<Sensor> sensorList){
		UserSensorXMLParser userParser = new UserSensorXMLParser(user);	
		
		for(Sensor sensor : sensorList){
			try{
				String xml = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());
				Document document = DocumentHelper.parseText(xml);
				SensorSourceType sourceTypeObj = sensorManager.getXMLMapForSpecifiedSourceType(sensor.getSourceType());
				userParser.setPropertiesDataSignMap(getMapFromXMLContent(sourceTypeObj.getSourceXMLContent()));
				
				Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
				if(newest == null || DateUtil.isBefore(newest.getTimes(), userParser.getReaderTime())){
					Observation obs = new Observation();
					obs.setSensorId(sensor.getId());
					List results = userParser.getObservationData(document, obs);
					for(Object obj:results)
						 sensorManager.addObject(obj);
				}
				
			}catch(Exception e){
				continue;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addNewUserSensor(String url,String sourceType){
		UserSensorXMLParser userParser = new UserSensorXMLParser(user);
		try{
			String xml = WebServiceURLRetriever.RetrieveFromURL(url);
			Document document = DocumentHelper.parseText(xml);
			SensorSourceType sourceTypeObj = sensorManager.getXMLMapForSpecifiedSourceType(sourceType);
			HashMap sensorMap = getSensorMetaMapFromXMLContent(sourceTypeObj.getSourceXMLContent(), XMLUtil.sensorsource_sign_SENSOR);
			userParser.setSensorMetaSignMap(sensorMap);
			HashMap plHashMap = getSensorMetaMapFromXMLContent(sourceTypeObj.getSourceXMLContent(), XMLUtil.sensorsource_sign_PLACE);
			userParser.setPlaceMetaSignMap(plHashMap);
			userParser.setPropertiesDataSignMap(getMapFromXMLContent(sourceTypeObj.getSourceXMLContent()));
			userParser.addUserSensorData(xml);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap getMapFromXMLContent(String xmlContent){		
		String tagMapNode = "/sensorsource/tagmap";
		HashMap<String,List> hm = new HashMap<String, List>();
		String valueTag="";
		String property="";
		String unit="";
		try {
			Document document = DocumentHelper.parseText(xmlContent);
			Element tagMap = (Element)document.selectSingleNode(tagMapNode);
			List<Element> inner_elements = tagMap.elements();
			for(Element inner_element : inner_elements){				
				if(inner_element.getName().equals(XMLUtil.sensorsource_sign_MAP.toString())){
					List<Element> feeds_elements = inner_element.elements();
					for(Element feeds_element : feeds_elements){
						String content = feeds_element.getTextTrim();				
						if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_VALUETAG.toString()))							
							valueTag = content;
						else if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_PROPERTY.toString()))
							property = content;
						else if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_UNIT.toString()))
							unit = content;						
					}
					List<String> l = new ArrayList<String>();
					l.add(property);
					l.add(unit);
					hm.put(valueTag, l);
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(hm);
		return hm;
	}
	
	public HashMap getSensorMetaMapFromXMLContent(String xmlContent,String tagName){		
		String tagMapNode = "/sensorsource/"+tagName;
		HashMap<String,List> hm = new HashMap<String, List>();
		String valueTag="";
		String property="";
		String ontPro="";
		try {
			Document document = DocumentHelper.parseText(xmlContent);
			Element tagMap = (Element)document.selectSingleNode(tagMapNode);
			List<Element> inner_elements = tagMap.elements();
			for(Element inner_element : inner_elements){				
				if(inner_element.getName().equals(XMLUtil.sensorsource_sign_MAP.toString())){
					List<Element> feeds_elements = inner_element.elements();
					for(Element feeds_element : feeds_elements){
						String content = feeds_element.getTextTrim();				
						if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_VALUETAG.toString()))							
							valueTag = content;
						else if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_PROPERTY.toString()))
							property = content;
						else if(feeds_element.getName().equals(XMLUtil.sensorsource_sign_ONTOPRO.toString()))
							ontPro = content;						
					}
					List<String> l = new ArrayList<String>();					
					l.add(ontPro);
					l.add(valueTag);
					hm.put(property, l);
				}
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(hm);
		return hm;
	}
	
	public static void main(String[] args){
//		UserSensorWrapper uWrapper = new UserSensorWrapper("carweather","ff8081813114810c0131148183490001");
//		uWrapper.start();
		
	}
}
