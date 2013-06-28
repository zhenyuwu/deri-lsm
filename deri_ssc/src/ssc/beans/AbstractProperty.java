package ssc.beans;

import java.util.Date;

public class AbstractProperty {
	private String value;
	private Date times;
	private String observedURL;	
	private String propertyName;
	private String unit;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public void setValue(double value) {
		this.value = Double.toString(value);
	}
	
	public void setValue(int value) {
		this.value = Integer.toString(value);
	}
	
	public void setValue(Date value) {
		this.value = value.toString();
	}
	
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	public String getObservedURL() {
		return observedURL;
	}
	public void setObservedURL(String observedURL) {
		this.observedURL = observedURL;
	}	
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getSimpleContent(){
		String result = "";		
		//result += observedURL+": " + strValue + "\t";
		if(unit!=null)
			result += value + " (" + unit + ")\t";
		else result += value +  "\t";
		return result;
	}
}
