package deri.sensor.javabeans;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Wrapper {
	private String id;	
	private int timeToUpdate;
	private String timeToUpdateUnit;
	private String dataFormat;
	private String currentStatus;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public int getTimeToUpdate() {
		return timeToUpdate;
	}
	public void setTimeToUpdate(int timeToUpdate) {
		this.timeToUpdate = timeToUpdate;
	}
	public String getTimeToUpdateUnit() {
		return timeToUpdateUnit;
	}
	public void setTimeToUpdateUnit(String timeToUpdateUnit) {
		this.timeToUpdateUnit = timeToUpdateUnit;
	}
	public String getDataFormat() {
		return dataFormat;
	}
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	
}
