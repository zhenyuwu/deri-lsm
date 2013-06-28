package deri.sensor.javabeans;

import java.util.Date;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class StringValue extends AbstractProperty{
	private String id;
	private Observation observation;
	private String value;		
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
	public void setObservedURL(String propertyName) {
		super.setObservedURL(propertyName);
		this.observedURL = propertyName;
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
	@Override
	public Date getTimes() {
		return times;
	}
	@Override
	public void setTimes(Date times) {
		super.setTimes(times);
		this.times = times;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		super.setValue(value);
		this.value = value;
	}
}
