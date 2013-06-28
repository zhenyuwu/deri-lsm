package deri.sensor.wrapper;

import java.util.List;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Sensor;
import deri.sensor.london.railwaytation.PredictionDetailedXMLParser;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.ThreadUtil;

public class LondonRailwayStationWrapper extends Thread{

	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	
	@Override
	public void run(){		
		System.out.println("----------------------------------- London railwaystation_"+this.getName()+" has started -----------------------------------");		
		for(;;){
			List<Sensor> sensors = null;
			sensors = sensorManager.getAllSensorWithSpecifiedSensorType("railwaystation");
			PredictionDetailedXMLParser preParser = new PredictionDetailedXMLParser();
			for(Sensor sensor:sensors)
				preParser.getPredictionDetailedForOneStation(sensor);
			System.out.println("London railwaystation update has finished");
			ThreadUtil.sleepForDays(1);
		}
	}
	
	public void updateOneRailwayStation(String id){
		Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(id);
		PredictionDetailedXMLParser preParser = new PredictionDetailedXMLParser();
		preParser.getPredictionDetailedForOneStation(sensor);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LondonRailwayStationWrapper railwayWrapper = new LondonRailwayStationWrapper();
		railwayWrapper.start();
	}

}
