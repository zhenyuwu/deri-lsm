package deri.sensor.components.Map;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.DesktopUnavailableException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Html;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.SensorTypeToProperties;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.Direction;
import deri.sensor.javabeans.Latitude;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.Status;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.NumberUtil;
import deri.sensor.utils.ThreadUtil;
import deri.sensor.weather.weatherbug.WeatherBugXMLParser;
import deri.sensor.weather.wunderground.WUnderGroundXMLParser;
import deri.sensor.weather.yahoo.YahooWeatherXMLParser;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorMarkerContentThread extends Thread {
	private final Desktop _desktop;
    private final Component _info;
    private SensorManager sensorManager;
    private double lat,lng;
    private CallSign callSign=null;
    private String type;
	public SensorMarkerContentThread(Component info,double lat,double lng){
		_desktop = info.getDesktop();
        _info = info;
        this.lat = lat;
        this.lng = lng;
        sensorManager = ServiceLocator.getSensorManager();
	}
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	@Override
	public void run() {
        try {
            while (true) {
                if (_info.getDesktop() == null
                        || !_desktop.isServerPushEnabled()) {
                    _desktop.enableServerPush(false);
                    return;
                }
                Executions.activate(_desktop);
                String ct="";
                try {
                	SensorMarker sm = (SensorMarker) _info;
                	this.setType(sm.getType());
                	if(type.equals("weather"))                	
                		sm.setContent(updateWeatherContent());                		
                	else if(type.equals("webcam")||type.equals("traffic")||type.equals("satellite"))
                		sm.setContent(updateImageContent());
                	else if(type.equals(SensorTypeEnum.ADSBHub.toString()))
                		sm.setContent(updateFlightContent());
                	else if(type.equals(SensorTypeEnum.railwaystation.toString()))
                		sm.setContent(updateRailwayStationContent());
                	else
                		sm.setContent(updateContent());
                	                    
                }finally {
                    Executions.deactivate(_desktop);
                }
                ThreadUtil.sleepForSeconds(60);
            }
        } catch (DesktopUnavailableException ex) {
            System.out.println("The server push thread interrupted");
        } catch (InterruptedException e) {
            System.out.println("The server push thread interrupted");
        }
    }
	
	@SuppressWarnings("unchecked")
	public String updateWeatherContent(){	
		Html html = new Html();
		String content = "<H5 align=\"center\"> Weather information update </H5>";	
		content+="<p><font size=\"1\">";
		ArrayList<AbstractProperty> weatherInfo = null;
//		List<String> weatherSource = sensorManager.getAllSourcesWithSpecifiedLatLngSensorType(lat, lng, "weather");
		Sensor sensor = sensorManager.getSpecifiedSensorWithLatLng(lat, lng);
//		for(String st: weatherSource){
			try{ 
				weatherInfo=new ArrayList<AbstractProperty>();
				
//				content+="<b>City</b>:<font color=\"red\">" + ServiceLocator.getPlaceManager().getPlaceWithSpecifiedLatLng(lat, lng).getCity()+"</font>";
				content+="<b>City</b>:<font color=\"red\">" + sensor.getPlace().getCity()+"</font>";
//				String sourceType = sensorManager.getSpecifiedSensorWithSource(st).getSourceType();		
				String sourceType = sensor.getSourceType();	
				String xml = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());	
				if(sourceType.equals("wunderground"))
					weatherInfo = WUnderGroundXMLParser.getWeatherElementsFromWUnderGroundXML(xml);
				else if(sourceType.equals("weatherbug"))
					weatherInfo = WeatherBugXMLParser.getWeatherElementsFromWeatherBugXML(xml);
				else if(sourceType.equals("yahoo"))
					weatherInfo = YahooWeatherXMLParser.getWeatherElementsFromYahooXML(xml);
				if(weatherInfo!=null){
					for(AbstractProperty obj:weatherInfo){		
						String v="";
						if(obj.getPropertyName().contains("Temperature")){
							content+="<br><b>Temperature: </b><font color=\"red\">" + obj.getValue()+"</font>";				
						}else if(obj.getPropertyName().contains("Windchill")){
							v = obj.getValue();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Wind chill:</b> <font color=\"red\">" + v+"</font>";							
						}else if(obj.getPropertyName().contains("WindSpeed")){
							v = obj.getValue();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Wind speed: </b><font color=\"red\">" + v+"</font>";							
						}else if(obj.getPropertyName().contains("Direction")){
							content+="<br><b>Direction: </b><font color=\"red\">" + obj.getValue()+"</font>";
						}else if(obj.getPropertyName().contains("AtmosphereHumidity")){
							v = obj.getValue();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Humidity: </b><font color=\"red\">" + v+"</font>";							
						}else if(obj.getPropertyName().contains("AtmospherePressure")){
							v = obj.getValue();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Pressure: </b><font color=\"red\">" + v+"</font>";
						}else if(obj.getPropertyName().contains("AtmosphereVisibility")){
							v = obj.getValue();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Visibility: </b><font color=\"red\">" + v+"</font>";
						}else if(obj.getPropertyName().contains("Status")){
							content+="<br><b>Status: </b><font color=\"red\">" + obj.getValue()+"</font>";
						}
					}
					content+="<br><b>Sensor time: </b><font color=\"red\">" + weatherInfo.get(0).getTimes()+"</font>";
					content+="<br><b>System time: </b><font color=\"red\">" + (new Date()).toString()+"</font>";
				}else{
					Observation newestObs = sensorManager.getNewestObservationForOneSensor(sensor.getId());
					List<ArrayList> temp = sensorManager.getReadingDataOfObservation(newestObs.getId());					
					for(ArrayList arr:temp){						
						String v="";
						if(arr.get(0).toString().contains("Temperature")){
							content+="<br><b>Temperature: </b><font color=\"red\">" + arr.get(1).toString()+"</font>";				
						}else if(arr.get(0).toString().contains("Temperature")){
							v = arr.get(1).toString();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Wind chill:</b> <font color=\"red\">" + v+"</font>";							
						}else if(arr.get(0).toString().contains("WindSpeed")){
							v = arr.get(1).toString();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Wind speed: </b><font color=\"red\">" + v+"</font>";							
						}else if(arr.get(0).toString().contains("Direction")){
							content+="<br><b>Direction: </b><font color=\"red\">" + arr.get(1).toString()+"</font>";
						}else if(arr.get(0).toString().contains("AtmosphereHumidity")){
							v = arr.get(1).toString();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Humidity: </b><font color=\"red\">" + v+"</font>";							
						}else if(arr.get(0).toString().contains("AtmospherePressure")){
							v = arr.get(1).toString();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Pressure: </b><font color=\"red\">" + v+"</font>";
						}else if(arr.get(0).toString().contains("AtmosphereVisibility")){
							v = arr.get(1).toString();
							if (Double.parseDouble(v)!= ConstantsUtil.weather_defalut_value)
								content+= "<br><b>Atmosphere Visibility: </b><font color=\"red\">" + v+"</font>";
						}else if(arr.get(0).toString().contains("Status")){
							content+="<br><b>Status: </b><font color=\"red\">" + arr.get(1).toString()+"</font>";
						}
					}
					//content+=", Sensor time: " + ((AbstractProperty)weatherInfo.get(0)).getTimes();
					content+="<br><b>System time: " + (new Date()).toString();
					
				}				
			}catch(Exception e){
				e.printStackTrace();				
			}
//		}
		content+="</font></p>";
		html.setContent(content);
		if(weatherInfo!=null)
			return html.getContent();
		else return ("This sensor is not available at this moment");
	}
	
	public String updateRailwayStationContent(){
		String htmlContent = "<H5 align=\"center\"> Railway station information update </H5>";
		htmlContent+="<p><font size=\"1\">";
		Observation newObs = null;
		try{			
			Sensor sensor = sensorManager.getSpecifiedSensorWithLatLng(lat, lng);
			newObs = sensorManager.getNewestObservationForOneSensor(sensor.getId());
			htmlContent+="<b>Station:</b><font color=\"red\">" + sensor.getName()+"</font>";
			htmlContent+="<br><b>City:</b><font color=\"red\">" +  sensor.getPlace().getCity()+"</font>";
			List<String> observations = sensorManager.getObservationsWithTimeCriteria(sensor.getId(), "=", newObs.getTimes(), null);
			htmlContent+="<P ALIGN=left><MARQUEE WIDTH=100% HEIGHT=30 BEHAVIOR=scroll SCROLLAMOUNT=\"3\">";
			for(String obs:observations){
				String content=null;
				List<ArrayList> temp = sensorManager.getReadingDataOfObservation(obs);
				for(ArrayList arr:temp){						
					String unit=null;
					String sign;
					if(arr.get(0).toString().contains("SecondToStation"))
						sign = "ArriveToNextStation";
					else if(arr.get(0).toString().contains("TimeToStation"))
						sign = "ArriveToDestination";
					else sign = arr.get(0).toString().substring(arr.get(0).toString().lastIndexOf("#")+1);
					htmlContent+="<font size=\"2\" color=\"red\">";
					htmlContent+="<b>"+sign+": "+"<b></font>";
					try{
						content = arr.get(1).toString();
						unit = arr.get(2).toString();				
					}catch(Exception e){}
					if(unit == null){
						unit = "";
					}
					try {				
						htmlContent+="<font size=\"2\" color=\"green\">";
						if(content == null){
							htmlContent+="not_supported";
						}if(NumberUtil.isDouble(content)&&unit!=""){						
							int data =(int) Double.parseDouble(content);
							htmlContent+=String.valueOf(data) + " (" + unit + ")";
						}else{
							htmlContent+=content;
						}
						htmlContent+=".</font>";
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			htmlContent+= System.getProperty("line.separator");
			htmlContent+="</MARQUEE><br><b>Sensor latest update time: </b><font color=\"red\">" + newObs.getTimes()+"</font>";
			htmlContent+="</font></p>";
		}catch(Exception e){
			e.printStackTrace();
		}		
		if(newObs!=null)
			return htmlContent;
		else return ("This sensor is temporary shut down");
	}
	public String updateFlightContent(){

		String htmlContent = "<H5 align=\"center\"> Flight information update </H5>";
		htmlContent+="<p><font size=\"1\">";
		Latitude latitude = null;
		Observation newObs = null;		
		try{			
			if(callSign==null){
				latitude = sensorManager.getFlightLatitudeWithCurrentLat(lat);
				newObs = latitude.getObservation();
				callSign = (CallSign)sensorManager.getSpecifiedReadingForOneObservation(newObs.getId(), "CallSign");
			}else 
				newObs = callSign.getObservation();			
			List<String> tblList = SensorTypeToProperties.sensorTypeToTableProperties.get(type.toString());
			htmlContent+="<P ALIGN=left><MARQUEE WIDTH=100% HEIGHT=30 BEHAVIOR=scroll SCROLLAMOUNT=\"3\">";
			for(String tbl: tblList){
				try{
					AbstractProperty absPro = sensorManager.getSpecifiedReadingForOneObservation(newObs.getId(), tbl);
					htmlContent+="<font size=\"2\" color=\"red\">";
					htmlContent+="<b>"+tbl+": "+"<b></font>";
					htmlContent+="<font size=\"2\" color=\"green\">"+absPro.getSimpleContent()+".</font>";
					//htmlContent+="<b>"+tbl+":</b> "+absPro.getSimpleContent()+"<br>";
				}catch(Exception e){
					continue;
				}
			}
			//htmlContent+="</MARQUEE><br><b>Sensor latest update time: </b><font color=\"red\">" + newObs.getTimes()+"</font>";
			htmlContent+="</MARQUEE><br><b>System latest update time: </b><font color=\"red\">" + (new Date()).toString()+"</font>";
			//htmlContent+="<br><b>System latest update time: </b>" + (new Date()).toString();
			htmlContent+="</font></p>";
		}catch(Exception e){
			e.printStackTrace();
		}		
		if(newObs!=null)
			return htmlContent;
		else return ("This flight is crashed");
	}
	
	public String updateContent(){
		String content = "<H5 align=\"center\"> Sensor information update </H5>";
		content+="<p><font size=\"1\">";
		Observation newObs = null;
		String sign;
		try{		
			Sensor sensor = sensorManager.getSpecifiedSensorWithLatLng(lat, lng);
			newObs = sensorManager.getNewestObservationForOneSensor(sensor.getId());
			content+="<b>City:</b><font color=\"red\">" + sensor.getPlace().getCity()+"</font>";
			List<ArrayList> readingList = sensorManager.getReadingDataOfObservation(newObs.getId());
			for(ArrayList reading: readingList){
				try{
					if(type.equals("cosm"))
						sign = reading.get(3).toString();
					else
						sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);					
					if(reading.get(2)!=null)
						content+="<br><b>"+sign+":</b><font color=\"red\"> "+reading.get(1)+"("+reading.get(2)+")</font>";
					else
						content+="<br><b>"+sign+":</b><font color=\"red\"> "+reading.get(1)+"</font>";
				}catch(Exception e){
					continue;
				}
			}
			content+= System.getProperty("line.separator");
			content+="<br><b>Sensor latest update time: </b><font color=\"red\">" + newObs.getTimes()+"</font>";
			content+="<br><b>System latest update time: </b><font color=\"red\">" + (new Date()).toString()+"</font>";
			content+="</font></p>";
		}catch(Exception e){
			e.printStackTrace();
		}		
		if(newObs!=null)
			return content;
		else return ("This sensor is temporary shut down");
	}
	
	public String updateImageContent(){
		Html html = new Html();
		String content = "<H5 align=\"center\"> Sensor information update </H5>";
		content+="<p><font size=\"1\">";
		Observation newObs = null;
		try{			
			//newObs = sensorManager.getNewestObservationForOnePlace(lat, lng, type.toString());
			List<Sensor> sensors = sensorManager.getAllSensorWithSpecifiedLatLngSensorType(lat,lng,type.toString());
			if(sensors==null) return content;
			Sensor sensor = sensors.get(0);
			content+="<b>City:</b>" + sensor.getPlace().getCity();			
			try{
				content+="<div align=\"center\">";
				content+="<a href=\""+ sensor.getSource()+"\">";
				content+="<img src=\""+sensor.getSource()+"\" " +
						"height=\"70\" width=\"80\"/>";
				content+="</a>";
				content+="</div>";
			}catch(Exception e){
					
			}			
			content+= System.getProperty("line.separator");
//			content+="<br><b>Sensor latest update time: </b>" + newObs.getTimes();
			content+="<br><b>System latest update time: </b><font color=\"red\">" + (new Date()).toString()+"</font>";
			content+="</font></p>";
		}catch(Exception e){
			
		}		
		//if(newObs!=null)
			return content;
		//else return ("This sensor is temporary shut down");
	}
}
