package ssc.beans;

import java.util.Date;


public class Observation {
	private String id;	
	private Date times;	
//	private Sensor sensor;
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
	public String getSensor() {
		return sensorId;
	}
	public void setSensor(String sensorId) {
		this.sensorId = sensorId;
	}
	public String getFeatureOfInterest() {
		return featureOfInterest;
	}
	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}
}
