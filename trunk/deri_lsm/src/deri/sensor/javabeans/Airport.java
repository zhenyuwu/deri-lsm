package deri.sensor.javabeans;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Airport {
	private String id;
	private String AirportCode;	
	private String City;
	private String Country;
	private double Latitude;
	private double Longitude;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAirportCode() {
		return AirportCode;
	}
	public void setAirportCode(String airportCode) {
		AirportCode = airportCode;
	}
	public String getCity() {
		return City;
	}
	public void setCity(String city) {
		City = city;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}	
}
