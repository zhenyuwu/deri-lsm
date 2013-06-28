package deri.sensor.javabeans;

import java.util.Date;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Observation {
	private String id;	
	private Date times;	
	private Sensor sensor;
	private String sensorId;
	private String featureOfInterest; 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	public String getSensorId() {
		return sensorId;
	}
	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}
	
	public String getFeatureOfInterest() {
		return featureOfInterest;
	}
	public Sensor getSensor() {
		return sensor;
	}
	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}
}
