package deri.sensor.javabeans;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SystemSensorType {
	private String id;
	private String sensorType;
	private String sensorProperties;
	private User user;
	private Wrapper wrapper;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSensorType() {
		return sensorType;
	}
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	public String getSensorProperties() {
		return sensorProperties;
	}
	public void setSensorProperties(String sensorProperties) {
		this.sensorProperties = sensorProperties;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Wrapper getWrapper() {
		return wrapper;
	}
	public void setWrapper(Wrapper wrapper) {
		this.wrapper = wrapper;
	}
	
}
