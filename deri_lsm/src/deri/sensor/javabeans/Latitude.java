package deri.sensor.javabeans;

import java.util.Date;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Latitude extends AbstractProperty{
	private String id;
	private Observation observation;
//	private double value;
	private String unit;	
	private Date times;
	private String observedURL;
	private String propertyName;
	
	@Override
	public String getPropertyName() {
		return propertyName;
	}
	@Override
	public void setPropertyName(String propertyName) {
		super.setPropertyName(propertyName);
		this.propertyName = propertyName;
	}
	
	
	@Override
	public String getObservedURL() {
		return observedURL;
	}
	@Override
	public void setObservedURL(String observedURL) {
		super.setObservedURL(observedURL);
		this.observedURL = observedURL;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Observation getObservation() {
		return observation;
	}
	public void setObservation(Observation observation) {
		this.observation = observation;
	}
//	public double getValue() {
//		return value;
//	}
//	public void setValue(double value) {
//		super.setValue(value);
//		this.value = value;
//	}
	@Override
	public String getUnit() {
		return unit;
	}
	@Override
	public void setUnit(String unit) {
		super.setUnit(unit);
		this.unit = unit;
	}
	@Override
	public Date getTimes() {
		return times;
	}
	@Override
	public void setTimes(Date times) {
		super.setTimes(times);
		this.times = times;
	}
}
