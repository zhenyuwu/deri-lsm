package deri.sensor.javabeans;


import java.util.Date;

import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Place {
	private String id;
	private String woeid = "";
	private String geonameid = "";
	private String zipcode = "";
	private String street = "";
	private String city = "";
	private String province = "";
	private String country = "";
	private String continent = "";
	private double lat;// not null
	private double lng;// not null
	private String infor = "no infor";
	private String author = "admin";// not null and not empty
	private Date times = new Date();// not null created time
	private int agree = ConstantsUtil.agree_default_value;
	private int disagree = ConstantsUtil.disagree_default_value;
	
	public Place() {
		super();
	}
	
	public Place(double lat, double lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}

	public String getId() {
		return id;
	}

	@SuppressWarnings("unused")
	public void setId(String id) {
		this.id = id;
	}


	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getWoeid() {
		return woeid;
	}

	public void setWoeid(String woeid) {
		this.woeid = woeid;
	}

	public String getGeonameid() {
		return geonameid;
	}

	public void setGeonameid(String geonameid) {
		this.geonameid = geonameid;
	}

	public String getStreet() {
		return street;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getTimes() {
		return times;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public void setTimes(Date times) {
		this.times = times;
	}
	
	public int getAgree() {
		return agree;
	}

	public void setAgree(int agree) {
		this.agree = agree;
	}

	public int getDisagree() {
		return disagree;
	}

	public void setDisagree(int disagree) {
		this.disagree = disagree;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append(geonameid == null ? "" : (geonameid.trim().equals("") ? "" : ("geonameid:" + geonameid + ", ")));
		sb.append(woeid == null ? "" : (woeid.trim().equals("") ? "" : ("woeid:" + woeid + ", ")));
		sb.append(zipcode == null ? "" :(zipcode.trim().equals("") ? "" : ("zipcode:" + zipcode + ", ")));
		sb.append(street == null ? "" : (street.trim().equals("") ? "" : ("street:" + street + ", ")));
		sb.append(city == null ? "" : (city.trim().equals("") ? "" : ("city:" + city + ", ")));
		sb.append(province == null ? "" : (province.trim().equals("") ? "" : ("province:" + province + ", ")));
		sb.append(country == null ? "" : (country.trim().equals("") ? "" : ("country:" + country + ", ")));
		sb.append(continent == null ? "" : (continent.trim().equals("") ? "" : ("continent:" + continent + ", ")));
		sb.append(infor == null ? "" : (infor.trim().equals("") ? "" : ("information:" + infor + ", ")));
		sb.append("lat:" + lat + ", ");
		sb.append("lng:" + lng + ", ");
		sb.append("author:"+ author + ", ");
		sb.append("times:"+ DateUtil.date2StandardString(times) + ", ");
		
		return sb.toString();
	}
	
	public static Place placeStrToPlaceObj(String placeStr){
		Place place = new Place();
		
		String geonameid = getOneItemFromPlaceStr(placeStr,"geonameid");
		if(geonameid != null){
			place.setGeonameid(geonameid);
		}
		
		String woeid = getOneItemFromPlaceStr(placeStr,"woeid");
		if(woeid != null){
			place.setWoeid(woeid);
		}
		
		String zipcode = getOneItemFromPlaceStr(placeStr,"zipcode");
		if(zipcode != null){
			place.setZipcode(zipcode);
		}

		String street = getOneItemFromPlaceStr(placeStr,"street");
		if(street != null){
			place.setStreet(street);
		}

		String city = getOneItemFromPlaceStr(placeStr,"city");
		if(city != null){
			place.setCity(city);
		}

		String province = getOneItemFromPlaceStr(placeStr,"province");
		if(province != null){
			place.setProvince(province);
		}

		String country = getOneItemFromPlaceStr(placeStr,"country");
		if(country != null){
			place.setCountry(country);
		}

		String continent = getOneItemFromPlaceStr(placeStr,"continent");
		if(continent != null){
			place.setContinent(continent);
		}

		String information = getOneItemFromPlaceStr(placeStr,"information");
		if(information != null){
			place.setInfor(information);
		}

		String lat = getOneItemFromPlaceStr(placeStr,"lat");
		if(lat != null){
			place.setLat(Double.parseDouble(lat));
		}

		String lng = getOneItemFromPlaceStr(placeStr,"lng");
		if(lng != null){
			place.setLng(Double.parseDouble(lng));
		}

		String author = getOneItemFromPlaceStr(placeStr,"author");
		if(author != null){
			place.setAuthor(author);
		}

		String times = getOneItemFromPlaceStr(placeStr,"times");
		if(times != null){
			place.setTimes(DateUtil.standardString2Date(times));
		}
		
		return place;
	}
	
	private static String getOneItemFromPlaceStr(String placeStr, String item){
		int index = placeStr.indexOf(item);
		if(index >= 0){
			int index_comma = placeStr.indexOf(",", index);
			String value = placeStr.substring(index + item.length() + 1, index_comma);
			return value.trim();
		}
		
		return null;
	}

	
	
	public String getSimpleContent(){
		String simple = this.getCity();
		if(simple.trim().equals("")){
			simple = this.getProvince();
		}
		if(simple.trim().equals("")){
			simple = this.getCountry();
		}
		
		return simple;
	}
	
	public static double[] getLatLngFromString(String content){
		if(content.contains("lat:") && content.contains("lng:")){
			double[] lat_lng = new double[2];
			int lat_index = content.indexOf("lat:");
			int lat_index_comma = content.indexOf(",",lat_index);
			double lat = Double.parseDouble(content.substring(lat_index + 4, lat_index_comma));
			
			int lng_index = content.indexOf("lng:");
			int lng_index_comma = content.indexOf(",",lng_index);
			double lng = Double.parseDouble(content.substring(lng_index + 4, lng_index_comma));
			
			lat_lng[0] = lat;
			lat_lng[1] = lng;
			
			return lat_lng;
		}
		return null;
	}
}
