package deri.sensor.javabeans;

import java.util.Date;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class RoadActivityTime {
	private String id;
	private Observation observation;
	private Date value;
	private String unit;
	private Date times;
	
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
	public Date getValue() {
		return value;
	}
	public void setValue(Date value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
}
