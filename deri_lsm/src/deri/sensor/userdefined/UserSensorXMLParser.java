package deri.sensor.userdefined;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserSensorXMLParser {
	private HashMap<String,List> sensorMetaSignMap;
	private HashMap<String,List> placeMetaSignMap;
	private HashMap<String,List> propertiesDataSignMap;
	private HashMap<String, List<ArrayList>> extraPropertiesMap;
	private PlaceManager placeManager = ServiceLocator.getPlaceManager();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	private String type;
	private Date readerTime;
	private User user;
	
	public UserSensorXMLParser(User user){
		this.user = user;
	}
	public HashMap<String, List> getSensorMetaSignMap() {
		return sensorMetaSignMap;
	}

	public void setSensorMetaSignMap(HashMap<String, List> sensorMetaSignMap) {
		this.sensorMetaSignMap = sensorMetaSignMap;
	}

	public HashMap<String, List> getPlaceMetaSignMap() {
		return placeMetaSignMap;
	}

	public void setPlaceMetaSignMap(HashMap<String, List> placeMetaSignMap) {
		this.placeMetaSignMap = placeMetaSignMap;
	}

	
	public HashMap<String, List> getPropertiesDataSignMap() {
		return propertiesDataSignMap;
	}

	public void setPropertiesDataSignMap(HashMap<String, List> propertiesDataSignMap) {
		this.propertiesDataSignMap = propertiesDataSignMap;
	}

	
	public Date getReaderTime() {
		return readerTime;
	}

	public void setReaderTime(Date readerTime) {
		this.readerTime = readerTime;
	}

	@SuppressWarnings("unchecked")
	public void addUserSensorData(String xml){
		if(xml == null || xml.trim().equals("")){
			return ;
		}		
		try {
			Document document = DocumentHelper.parseText(xml);
			String xmlTag;
			extraPropertiesMap = new HashMap<String, List<ArrayList>>();
			List extraPlaceValue = new ArrayList();
			List extraSensorValue = new ArrayList();
			double lat = 0;
			double lng = 0;
						
			Place place = new Place();
			Set placeSignSet = placeMetaSignMap.entrySet();
			Iterator pIter = placeSignSet.iterator();
			while(pIter.hasNext()){
				List extraPro = new ArrayList();
				Map.Entry entry = (Map.Entry)pIter.next();
				Object value = null;
				xmlTag = ((List)entry.getValue()).get(1).toString();
				try{
					if(!xmlTag.contains("@")){
						Element elm = (Element)document.selectSingleNode(xmlTag);
						value = elm.getData();
					}else if(xmlTag.contains("@")){
						String parentPath = xmlTag.substring(0, xmlTag.indexOf("@")-1);
						String attrName = xmlTag.substring(xmlTag.indexOf("@")+1);
						Element elm = (Element)document.selectSingleNode(parentPath);
						Attribute attribute = elm.attribute(attrName);
						value = attribute.getData();
					}
				}catch(Exception e){				
					value = xmlTag;
				}
				if(entry.getKey().toString().equals("woeid"))
					place.setWoeid(value.toString());
				else if(entry.getKey().toString().equals("geonameid"))
					place.setGeonameid(value.toString());
				else if(entry.getKey().toString().equals("zipcode"))
					place.setZipcode(value.toString());
				else if(entry.getKey().toString().equals("street"))
					place.setStreet(value.toString());
				else if(entry.getKey().toString().equals("city"))
					place.setCity(value.toString());
				else if(entry.getKey().toString().equals("province"))					
					place.setProvince(value.toString());
				else if(entry.getKey().toString().equals("country"))					
					place.setCountry(value.toString());
				else if(entry.getKey().toString().equals("continent"))					
					place.setContinent(value.toString());
				else if(entry.getKey().toString().equals("lat")){
					lat = Double.parseDouble(value.toString());
					place.setLat(lat);
				}
				else if(entry.getKey().toString().equals("lng")){
					lng = Double.parseDouble(value.toString());
					place.setLng(lng);
				}
				else if(entry.getKey().toString().equals("infor"))
					place.setInfor(value.toString());
				else if(entry.getKey().toString().equals("author"))					
					place.setAuthor(value.toString());
				else{
					extraPro.add(((List)entry.getValue()).get(0));
					extraPro.add(value);
					extraPlaceValue.add(extraPro);
				}
			}
			Place existPlace = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
			if(existPlace!=null)
				place = existPlace;
			Sensor existSensor = sensorManager.getSpecifiedSensorWithLatLng(lat,lng);
			Sensor sensor = null;
			if(existSensor==null){				
				sensor = new Sensor();
				sensor.setPlace(place);
				sensor.setUser(user);
				Set sensorSignSet = sensorMetaSignMap.entrySet();
				Iterator sIter = sensorSignSet.iterator();
				while(sIter.hasNext()){
					List extraPro = new ArrayList();
					Map.Entry me = (Map.Entry)sIter.next();
					Object valueObj = null;
					xmlTag = ((List)me.getValue()).get(1).toString();
					try{
						if(!xmlTag.contains("@")){
							Element elm = (Element)document.selectSingleNode(xmlTag);
							valueObj = elm.getData();
						}else if(xmlTag.contains("@")){
							String parentPath = xmlTag.substring(0, xmlTag.indexOf("@"));
							String attrName = xmlTag.substring(xmlTag.indexOf("@")+1);
							Element elm = (Element)document.selectSingleNode(parentPath);
							Attribute attribute = elm.attribute(attrName);
							valueObj = attribute.getData();
						}
					}catch(Exception e){				
						valueObj = xmlTag;
					}
					
					if(me.getKey().toString().equals("name"))
						sensor.setName(valueObj.toString());
					else if(me.getKey().toString().equals("sensorType"))
						sensor.setSensorType(valueObj.toString());
					else if(me.getKey().toString().equals("source"))
						sensor.setSource(valueObj.toString());
					else if(me.getKey().toString().equals("sourceType"))
						sensor.setSourceType(valueObj.toString());
					else if(me.getKey().toString().equals("infor"))
						sensor.setInfor(valueObj.toString());
					else if(me.getKey().toString().equals("author"))					
						sensor.setAuthor(valueObj.toString());
					else if(me.getKey().toString().equals("times"))					
						sensor.setTimes(new Date());
					else{
						extraPro.add(((List)me.getValue()).get(0));
						extraPro.add(valueObj);
						extraSensorValue.add(extraPro);
					}
				}
			}else sensor = existSensor;
			Observation observation = new Observation();
			observation.setSensorId(sensor.getId());
			String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + sensor.getPlace().getLat()+sensor.getPlace().getLng();
			observation.setFeatureOfInterest(foi);
			
			List results = getObservationData(document, observation);
			Observation newest = sensorManager.getNewestObservationForOnePlace(lat, lng,type);
			if(newest == null || DateUtil.isBefore(newest.getTimes(), readerTime)){
				 for(Object obj:results)
					 sensorManager.addObject(obj);
			     //sensorManager.addNewObservationToGraph(observation);
			     if(existSensor==null){
			    	 extraPropertiesMap.put(VirtuosoConstantUtil.sensorObjectDataPrefix+sensor.getId(), extraSensorValue);
			    	 extraPropertiesMap.put(VirtuosoConstantUtil.placeObjectDataPrefix+sensor.getPlace().getId(), extraPlaceValue);
			    	 sensorManager.addNewSensorToGraph(sensor);			    	 
			    	 sensorManager.addExtraMetadataToGraph(extraPropertiesMap);
			    	 if(existPlace==null)
			    		 sensorManager.addNewPlaceToGraph(place);
			    	 sensorManager.runSpatialIndex();
			     }			     
			}
		} catch (Exception e) {
			e.printStackTrace();			
		} 
	}
	
	public ArrayList getObservationData(Document document,Observation observation){
		ArrayList arrayData = new ArrayList();
		readerTime = new Date();		
		observation.setTimes(readerTime);
		String xmlTag;
		Set set = propertiesDataSignMap.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			try {
				Map.Entry me = (Map.Entry) iter.next();
				xmlTag = me.getKey().toString();					
				Object value = null;				
				try{
					if(!xmlTag.contains("@")){
						Element elm = (Element)document.selectSingleNode(xmlTag);
						value = elm.getData();
					}else if(xmlTag.contains("@")){
						String parentPath = xmlTag.substring(0, xmlTag.indexOf("@")-1);
						String attrName = xmlTag.substring(xmlTag.indexOf("@")+1);
						Element elm = (Element)document.selectSingleNode(parentPath);
						Attribute attribute = elm.attribute(attrName);
						value = attribute.getData();
					}
				}catch(Exception e){				
					value = "";
				}
				System.out.println("value:"+value);
				String className = ((List)me.getValue()).get(0).toString();
				
				String methodName = "setValue";					
				Class objClass = Class.forName("deri.sensor.javabeans."+className);
				Object obj = objClass.newInstance();
				
				Class<?>[] clazz = null;					
				Method[] allMethods = objClass.getDeclaredMethods();
			      for (Method m : allMethods) {
			    	  String mName = m.getName();
			    	  if(mName.equals("setValue")){
			    		  Object[] arguments = new Object[1];
			    		  String type = m.getParameterTypes()[0].getSimpleName();
			    		  if(type.equals("double")){
			    			  if(value.equals(""))
			    				  value = ConstantsUtil.weather_defalut_value;
			    			  arguments[0] =  Double.parseDouble(value.toString());
			    		  }else if(type.equals("Date"))
			    			  if(value.equals(""))
//			    				  arguments[0] =  new Date();
			    				  arguments[0] =  DateUtil.standardString2Date(value.toString());
			    			  else arguments[0] =  DateUtil.standardString2Date(value.toString());
			    		  else if(type.equals("int"))
			    			  arguments[0] =  Integer.parseInt(value.toString());
			    		  else
			    			  arguments[0] =  value.toString();
			    		  m.invoke(obj,arguments);
			    		  System.out.println("value:"+((AbstractProperty)obj).getValue());				    		  
			    	  }else if(mName.equals("setUnit")){
			    		  Object[] arguments = new Object[1];				    		  
			    		  arguments[0] =  ((List)me.getValue()).get(1).toString();
			    		  m.invoke(obj,arguments);
			    		  System.out.println("unit:"+((AbstractProperty)obj).getUnit());				    		  
			    	  }else if(mName.equals("setObservedURL")){
			    		  Object[] arguments = new Object[1];				    		  
			    		  arguments[0] =  objClass.getSimpleName();
			    		  m.invoke(obj,arguments);
			    		  System.out.println("propertyName:"+((AbstractProperty)obj).getObservedURL());				    		  
			    	  }else if(mName.equals("setTimes")){
			    		  Object[] arguments = new Object[1];				    		  
			    		  arguments[0] =  new Date();
			    		  m.invoke(obj,arguments);
			    		  System.out.println("Date time:"+((AbstractProperty)obj).getTimes());				    		  
			    	  }else if(mName.equals("setObservation")){
			    		  Object[] arguments = new Object[1];				    		  
			    		  arguments[0] =  observation;
			    		  m.invoke(obj,arguments);
			    		  System.out.println("Observation:");
			    	  }
			      }
			      arrayData.add(obj);
			}catch(Exception e){
				e.printStackTrace();
				continue;				
			}
		}
		return arrayData;
	}
	
	public static void main(String[] args) throws SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Class c = Temperature.class;
		String fmt = "%24s: %s%n";
//		Method[] allMethods = c.getDeclaredMethods();
//	      for (Method m : allMethods) {
//	    	  System.out.println(m.getName());
//	        out.format("%s%n", m.toGenericString());
//
//	        out.format(fmt, "ReturnType", m.getReturnType());
//	        out.format(fmt, "GenericReturnType", m.getGenericReturnType());
//
//	        Class<?>[] pType = m.getParameterTypes();
//	        Type[] gpType = m.getGenericParameterTypes();
//	        for (int i = 0; i < pType.length; i++) {
//	          out.format(fmt, "ParameterType", pType[i]);
//	          out.format(fmt, "GenericParameterType", gpType[i]);
//	        }
//	      }	
			String methodName = "setTimes";

			Object obj = c.newInstance();
			Method[] allMethods = c.getDeclaredMethods();
		      for (Method m : allMethods) {
		    	  String mName = m.getName();
		    	  if(mName.equals(methodName)){
		    		  Object[] arguments = new Object[1];
		    		  Class<?> type = m.getParameterTypes()[0];
		    		  //if()
		    		  System.out.println(m.getParameterTypes()[0].getSimpleName());
		    		  arguments[0] =  "15";
		    		  //m.invoke(obj,arguments);
		    		  System.out.println(((AbstractProperty)obj).getValue());
		    		  break;
		    	  }
		      }
	}
}
