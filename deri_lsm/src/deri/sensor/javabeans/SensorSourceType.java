package deri.sensor.javabeans;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorSourceType {
	private String id;	
	private String sourceType;
	private String sensorType;
	private User user;
	private String sourceXMLContent;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getsensorType() {
		return sensorType;
	}
	public void setsensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public String getSourceXMLContent() {
		return sourceXMLContent;
	}
	public void setSourceXMLContent(String sourceXMLContent) {
		this.sourceXMLContent = sourceXMLContent;
	}
	
	
}
