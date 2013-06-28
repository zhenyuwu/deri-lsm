package deri.sensor.wrapper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Information;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.ObservedProperty;
import deri.sensor.javabeans.Picture;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.ThreadUtil;

public class LinkValueWrapper extends Thread {
private SensorManager sensorManager = ServiceLocator.getSensorManager();
private HashMap hsm;
	public LinkValueWrapper(){
		hsm = new HashMap<String, String>();
		List<ObservedProperty> lstObsP = sensorManager.getAllObservedProperty();
		for(ObservedProperty osp:lstObsP){
			hsm.put(osp.getClassURI(),osp.getUrl());
		}
	}
	
	@Override
	public void run() {
		long count = 0;
		System.out.println("----------------------------------- LinkValueWrapperUpdateThread has started -----------------------------------");		
		for(String type: SensorType.getTypeList()){
			if(type.equals("webcam")||type.equals("radar")||type.equals("satellite")){				
				List<Sensor> sensors = sensorManager.getAllSensorWithSpecifiedSensorType(type.toString());
				postWebcamElement(sensors);
			}else continue;
			
			System.out.println("Linkvalue:\torder " + ++count + " has finished");						
		}
		ThreadUtil.sleepForHours(5);
	}
		
	public void update(){
		long count = 0;
		System.out.println("----------------------------------- LinkValueWrapperUpdate has started -----------------------------------");		
		for(String type: SensorType.getTypeList()){
			if(type.equals("webcam")){				
				List<Sensor> sensors = sensorManager.getAllSensorWithSpecifiedSensorType(type.toString());
				postWebcamElement(sensors);
			}else continue;
			System.out.println("Linkvalue:\torder " + ++count + " has finished");						
		}
	}
	
	public void postWebcamElement(List<Sensor> streamSourceList){
		BufferedImage image = null;
		for(Sensor sensor : streamSourceList){			
			try {				
				String filePath="";
			 	URL urlImg = new URL(sensor.getSource());
			 	URLConnection urlConn = urlImg.openConnection();
			 	urlConn.setConnectTimeout(4000);
			 	//urlConn.setReadTimeout(4000);
			 	urlConn.connect();			 	
	            //BufferedImage image = ImageIO.read(urlImg) ;
			 	image = ImageIO.read(urlConn.getInputStream());	            
	            if(image!=null){
	            	String fileName = ConstantsUtil.imageWebcam_path+"image" + System.nanoTime();
	            	String ext ="jpg";
	            	File file = new File(fileName + "." + ext);	    		
	            	ImageIO.write(image, ext, file);  // ignore returned boolean
	            	filePath = ConstantsUtil.image_feed_prefix+"?name="+file.getName()+"&type=webcam";
	            }else 
	            	continue;
	            
                Picture picture = new Picture();
                picture.setValue(filePath);
                Date time = new Date();
                picture.setTimes(time);
                String className = MappingMap.table2ClassURI.get(picture.getClass().getSimpleName().toLowerCase());
                picture.setObservedURL(hsm.get(className).toString());
                picture.setPropertyName(picture.getClass().getSimpleName());
                
                Information infor = new Information();
                infor.setValue(sensor.getSensorType() + " picture");
                infor.setTimes(time);
                className = MappingMap.table2ClassURI.get(infor.getClass().getSimpleName().toLowerCase());
                infor.setObservedURL(hsm.get(className).toString());
                infor.setPropertyName(infor.getClass().getSimpleName());
                
				Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
				if(newest == null || DateUtil.isBefore(newest.getTimes(), picture.getTimes())){
					Observation observation = new Observation();
					observation.setSensorId(sensor.getId());
					observation.setTimes(time);
					String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
							Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
							Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");
					observation.setFeatureOfInterest(foi);
					
					picture.setObservation(observation);
					infor.setObservation(observation);				
					sensorManager.addObject(picture);
					sensorManager.addObject(infor);
				}	                
	        }catch(IOException e) {
	        	image=null;
	           e.printStackTrace();	           
	           continue;
	        }finally{
	        	if(image==null){
	        		sensorManager.deleteAllObservationsForSpecifiedSensor(sensor);
	        		sensorManager.deleteObject(sensor);
	        	}
//	        		System.out.println("error");
	        }
		   //ThreadUtil.sleepForSeconds(60);
		}
	}	
	
	public static void main(String[] args) {
		LinkValueWrapper lw = new LinkValueWrapper();
		lw.start();
	}
}
