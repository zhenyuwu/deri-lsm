package deri.sensor.london.railwaytation;

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
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;
import deri.sensor.wrapper.TriplesDataRetriever;
import deri.sensor.wrapper.WebServiceURLRetriever;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class PredictionDetailedXMLParser {

	@SuppressWarnings("unchecked")
	public void getPredictionDetailedForOneStation(Sensor sensor){		
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		try{
			String xml = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();		
			//System.out.println(root.getName());
			Date time = new Date();			
			List<Element> elements = root.elements();
			for(Element element:elements){
				//System.out.println(element.getName());
				String content;
				if(element.getName().equals(PredictionDetailedSign.WhenCreated.toString())){
					content = element.getTextTrim();
					time = DateUtil.string2Date(content,"dd MMM yyyy kk:mm:ss");
				}else if(element.getName().equals(PredictionDetailedSign.S.toString())){					
					List<Element> inner_elements = element.elements();
					for(Element inner_element : inner_elements){
						try{													
							//System.out.println(inner_element.getName());
							if(inner_element.getName().equals(PredictionDetailedSign.P.toString())){
								String platform="";
								int trainNum = 0;
								int secondTo = 0;
								int timeTo = 0;
								boolean isOk = true;
								String currentLocation = "";
								String finaldestination="";
								Attribute attribute = inner_element.attribute(PredictionDetailedSign.N.toString());
								platform = attribute.getValue();
								List<Element> item_elements = inner_element.elements();
								for(Element item_element:item_elements){
									//System.out.println(item_element.getName());
									if(item_element.getName().equals(PredictionDetailedSign.T.toString())){
										List<Attribute> attributes = item_element.attributes();
										for(Attribute att:attributes){
											//System.out.println(att.getName());
											if(att.getName().equals(PredictionDetailedSign.SetNo.toString())){
												content = att.getValue();
												//System.out.println(content);
												if(NumberUtil.isInteger(content))
													trainNum = Integer.parseInt(content);
												else isOk = false;
											}else if(att.getName().equals(PredictionDetailedSign.SecondsTo.toString())){
												content = att.getValue();
												if(NumberUtil.isInteger(content))
													secondTo = Integer.parseInt(content);
												else isOk = false;
											}else if(att.getName().equals(PredictionDetailedSign.TimeTo.toString())){
												content = att.getValue();
												try{
													if(content.equals("0")) timeTo = 0;
													else{
														String[] timeArr = content.split(":");
														int minutes = Integer.parseInt(timeArr[0])*60+Integer.parseInt(timeArr[1]);
														timeTo = minutes;
													}
												}catch(Exception e){
													isOk = false;
												}													
											}else if(att.getName().equals(PredictionDetailedSign.Location.toString())){
												currentLocation = att.getValue();												
											}else if(att.getName().equals(PredictionDetailedSign.Destination.toString())){
												finaldestination = att.getValue();								
											}												
										}
										
										HashMap hsm = new HashMap<String, String>();
							    		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("railwaystation");
							    		for(int i=0;i<lstObsP.get(0).size();i++){
							    			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
							    		}
							    		
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
//						                className = className.substring(className.lastIndexOf("#")+1);
						                stationPlatform.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(stationPlatform.getPropertyName(), stationPlatform.getValue(), observationId, 
						                		stationPlatform.getObservedURL(), time);
						                
						                AbstractProperty train = new AbstractProperty();
										train.setValue(trainNum);
										train.setTimes(time);
						                className = MappingMap.table2ClassURI.get("trainnumber");						                
						                train.setPropertyName(className);
//						                className = className.substring(className.lastIndexOf("#")+1);
						                train.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(train.getPropertyName(), train.getValue(), observationId, 
						                		train.getObservedURL(), time);
						                
						                AbstractProperty se2Sta = new AbstractProperty();
						                se2Sta.setValue(secondTo);
						                se2Sta.setUnit("second");
						                se2Sta.setTimes(time);
						                className = MappingMap.table2ClassURI.get("secondtostation");						                
						                se2Sta.setPropertyName(className);
//						                className = className.substring(className.lastIndexOf("#")+1);
						                se2Sta.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasUnit(se2Sta.getPropertyName(), se2Sta.getValue(), se2Sta.getUnit(),
						                		observationId, se2Sta.getObservedURL(), time);
						                
						                AbstractProperty time2Sta = new AbstractProperty();
						                time2Sta.setValue(timeTo);
						                time2Sta.setUnit("minute");
						                time2Sta.setTimes(time);
						                className = MappingMap.table2ClassURI.get("timetostation");						                
						                time2Sta.setPropertyName(className);
//						                className = className.substring(className.lastIndexOf("#")+1);
						                time2Sta.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasUnit(time2Sta.getPropertyName(), time2Sta.getValue(), time2Sta.getUnit(),
						                		observationId, time2Sta.getObservedURL(), time);
						                
						                AbstractProperty status = new AbstractProperty();
						                status.setValue(currentLocation);
						                status.setTimes(time);
						                className = MappingMap.table2ClassURI.get("status");						                
						                status.setPropertyName(className);
//						                className = className.substring(className.lastIndexOf("#")+1);
						                status.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(status.getPropertyName(), status.getValue(), observationId, 
						                		status.getObservedURL(), time);
						                
						                AbstractProperty destination = new AbstractProperty();
						                destination.setValue(finaldestination);
						                destination.setTimes(time);
						                className = MappingMap.table2ClassURI.get("destination");
						                destination.setPropertyName(className);
//						                className = className.substring(className.lastIndexOf("#")+1);
						                destination.setObservedURL(hsm.get(className).toString());
						                triples+=TriplesDataRetriever.getTripleDataHasNoUnit(destination.getPropertyName(), destination.getValue(), observationId, 
						                		destination.getObservedURL(), time);
//						                System.out.println(triples);
						                sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
									}
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
		String url = "http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/P/ACT";
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		Sensor sensor = sensorManager.getSpecifiedSensorWithSource("http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/P/ACT");
		PredictionDetailedXMLParser pre = new PredictionDetailedXMLParser();
		pre.getPredictionDetailedForOneStation(sensor);
//		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());
		//PredictionDetailedXMLParser.getPredictionDetailedElementFromXMLUrl(xml);
		
	}
}
