package deri.sensor.places.yahoo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import deri.sensor.javabeans.Place;
import deri.sensor.json.JSONArray;
import deri.sensor.json.JSONException;
import deri.sensor.json.JSONObject;
import deri.sensor.utils.NumberUtil;
import deri.sensor.weather.yahoo.YahooWeatherXMLParser;
import deri.sensor.wrapper.WebServiceURLRetriever;


/**
 * @author Hoan Nguyen Mau Quoc
 */

public class YahooWhereURLXMLParser {
	
	public static final String[] appid = {
		"1K93XRLV34Fogg1KNHPsRT87L5mdyiejt.LAZyH9cKC2FOsSvVNGgB3cr3ZlQfw0rBc",
		"oTo9WVDV34FGnhjT7QNLwcQ8zc9nFAVKTGpMxWTgLybKzbayBhhfhjqGsvSKWA9feV_ao4RjhEvmZw",
		"7BENF0TV34FHCgYXhW18AohxFVyDo5oEH5v.m7pAdGRuefcAaLFYKl1IsV6W8g5.VfD2FNrQenO3UA",
		"YJTxUnvV34FkxPMBkRqwq9qcy18zkJz.CrwKPrISjEzC5IBjqsajf9tZFihgS4Topyzx.9QkIeeAsA",
		"f3BaTOnV34FLrDGlQk8j3xDmk0yyBX5VR3aKE7ePDrqlJW02UfqgMZ32MdR87FChT067b.277ETARw",
		"USO5JKXV34FOizH_2J_BuX8EsSMYYvpUBJoyQcpjVxefvzEA_xtJMnKtplT2zHON3DXp4xPxpfR5RA",
		"foxZ.7DV34Fv0LI6qNF8xcdkcddAgjXI1mvdGimSSy8EFHwKlZcdwSTibw65oxk"
	};
	public static final String wrongInputChars = ",()*!@#$&=|;:?/";
	public static final String blank_code = "%20";
	public static final String blank = " ";
	
	private static String getAppID(int index){
		return appid[index % appid.length];
	} 
	
	public static String trimWrongInputChars(String input){
		return input.replaceAll("["+wrongInputChars+"]", blank).replaceAll(blank + "+", blank_code);
	}
	
	public static String getWOEID4GeonameId(String geonameid, int index){
		String url = "http://where.yahooapis.com/v1/concordance/geonames/"+geonameid+"?appid=" + getAppID(index);
		String xml = WebServiceURLRetriever.RetrieveFromURL(url);
		if(xml != null && xml.contains("woeid")){
			int index1 = xml.indexOf("<woeid>");
			int index2 = xml.indexOf("</woeid>");
			String woeid = xml.substring(index1 + 7, index2);
			return woeid;
		}else{
			return "";
		}
	}
	
	/**
	 * 
	 * @param place
	 * @return
	 */
	public static List<String> getPlacesWithName(String place) {
		List<String> list = new ArrayList<String>();
		String woeid = "", lat = "", lng = "", city = "", province = "", county="",state = "", country = "";
		String zipcode="";
		place = trimWrongInputChars(place);
		//String url = "http://where.yahooapis.com/v1/places.q('"+place+"')?appid="+getAppID(2)+"&format=json";
		String url = "http://where.yahooapis.com/geocode?location="+place+"&gflags=R&flags=RJ&appid="+getAppID(2);
		
		String jsonString = WebServiceURLRetriever.RetrieveFromURL(url);
		if(jsonString == null || jsonString.trim().equals("") || !jsonString.contains("woeid")){
			return null;
		}		
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
			jo = jo.getJSONObject("ResultSet");
			if(jo != null){
				JSONArray ja = jo.getJSONArray("Results");
				for(int i=0;i<ja.length();i++){
					jo = ja.getJSONObject(i);
					woeid = jo.getString("woeid");
					city = jo.getString("city");
					state = jo.getString("state");
					county = jo.getString("county");
					zipcode = jo.getString("uzip");
					country = jo.getString("country");					
					lat = jo.getString("latitude");
					lng = jo.getString("longitude");
					String content = 
					  (city.trim().equals("")    ? "" : (city + ", "))
					+ (state.trim().equals("")  ? "" : (state + ", "))
					+ (county.trim().equals("")  ? "" : (county + ", "))
					+ (zipcode.trim().equals("")  ? "" : (zipcode + ", "))
					+ (country.trim().equals("") ? "" : (country + ", ")) 
					+ "woeid:" + woeid + ","
					+ "lat:" + lat + ", "
					+ "lng:" + lng + ", ";					
					list.add(content);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @param woeid
	 * @return
	 */
	public static List<String> getPlacesWithWOEID(String woeid) {
		List<String> list = new ArrayList<String>();
		String lat = "", lng = "", city = "", admin1 = "", admin2 = "", admin3 = "", country = "";
		
		String url = "http://where.yahooapis.com/v1/places.woeid('"+woeid+"')?appid="+getAppID(0)+"&format=json";
		
		String jsonString = WebServiceURLRetriever.RetrieveFromURL(url);
		if(jsonString == null || jsonString.trim().equals("") || !jsonString.contains("woeid")){
			return null;
		}
		
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
			jo = jo.getJSONObject("places");
			if(jo != null){
				JSONArray ja = jo.getJSONArray("place");
				for(int i=0;i<ja.length();i++){
					jo = ja.getJSONObject(i);
					city = jo.getString("name");
					admin1 = jo.getString("admin1");
					admin2 = jo.getString("admin2");
					admin3 = jo.getString("admin3");
					country = jo.getString("country");
					jo = jo.getJSONObject("centroid");
					lat = jo.getString("latitude");
					lng = jo.getString("longitude");
					String content = 
					  (city.trim().equals("")    ? "" : (city + ", "))
					+ (admin3.trim().equals("")  ? "" : (admin3 + ", "))
					+ (admin2.trim().equals("")  ? "" : (admin2 + ", "))
					+ (admin1.trim().equals("")  ? "" : (admin1 + ", "))
					+ (country.trim().equals("") ? "" : (country + ", ")) 
					+ "woeid:" + woeid + ", "
					+ "lat:" + lat + ", "
					+ "lng:" + lng + ", "; 
					
					list.add(content);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @param placeStr one item of the result of getPlacesWithName or getPlacesWithWOEID
	 * @return
	 */
	public static Place placeStr2PlaceObj(String placeStr){
		List<String> list = new ArrayList<String>();
		String woeid = "", lat = "", lng = "", city = "", province = "", county="",state = "", country = "";
		String zipcode="";
		placeStr = trimWrongInputChars(placeStr);
		//String url = "http://where.yahooapis.com/v1/places.q('"+place+"')?appid="+getAppID(2)+"&format=json";
		String url = "http://where.yahooapis.com/geocode?location="+placeStr+"&gflags=R&flags=J&appid="+getAppID(2);
		
		String jsonString = WebServiceURLRetriever.RetrieveFromURL(url);
		if(jsonString == null || jsonString.trim().equals("") || !jsonString.contains("woeid")){
			return null;
		}		
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
			jo = jo.getJSONObject("ResultSet");
			if(jo != null){
				JSONArray ja = jo.getJSONArray("Results");
				for(int i=0;i<ja.length();i++){
					jo = ja.getJSONObject(i);
					woeid = jo.getString("woeid");
					city = jo.getString("city");
					state = jo.getString("state");
					county = jo.getString("county");
					zipcode = jo.getString("uzip");
					country = jo.getString("country");					
					lat = jo.getString("latitude");
					lng = jo.getString("longitude");
					String content = 
					  (city.trim().equals("")    ? "" : (city + ", "))
					+ (state.trim().equals("")  ? "" : (state + ", "))
					+ (county.trim().equals("")  ? "" : (county + ", "))
					+ (zipcode.trim().equals("")  ? "" : (zipcode + ", "))
					+ (country.trim().equals("") ? "" : (country + ", ")) 
					+ "woeid:" + woeid + ","
					+ "lat:" + lat + ", "
					+ "lng:" + lng + ", ";
					
					if(content.contains(city) && content.contains(country) && content.contains(lat) && content.contains(lng)){
						Place place = new Place();
						place.setCity(city);
						place.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
						place.setCountry(country);
						place.setZipcode(zipcode);
						place.setLat(NumberUtil.formatWith6Digits(lat));
						place.setLng(NumberUtil.formatWith6Digits(lng));
						place.setTimes(new Date());
						place.setWoeid(woeid);
						return place;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param placeStr one item of the result of getPlacesWithName or getPlacesWithWOEID
	 * @return
	 */
	public static Place woeid2PlaceObj(String woeid){
		String lat = "", lng = "", city = "", postal = "", country = "";
		String url = "http://where.yahooapis.com/v1/places.woeid('"+woeid+"')?appid="+getAppID(0)+"&format=json";
		String jsonString = WebServiceURLRetriever.RetrieveFromURL(url);
		if(jsonString == null || jsonString.trim().equals("") || !jsonString.contains("woeid")){
			return null;
		}
		
		JSONObject jo = null;
		try {
			jo = new JSONObject(jsonString);
			jo = jo.getJSONObject("places");
			if(jo != null){
				JSONArray ja = jo.getJSONArray("place");
				jo = ja.getJSONObject(0);
				
				city = jo.getString("name");
				postal = jo.getString("postal");
				country = jo.getString("country");
				jo = jo.getJSONObject("centroid");
				lat = jo.getString("latitude");
				lng = jo.getString("longitude");
			
				Place place = new Place();
				place.setCity(city);
				place.setCountry(country);
				place.setZipcode(postal);
				place.setLat(NumberUtil.formatWith6Digits(lat));
				place.setLng(NumberUtil.formatWith6Digits(lng));
				place.setTimes(new Date());
				place.setWoeid(woeid);
				return place;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * get the weather information for a particular woeid
	 * @param woeid
	 * @return
	 */
	public static String getWeatherXML4Place(String woeid){
		String url = "http://weather.yahooapis.com/forecastrss?w="+woeid+"&u=c";
		return WebServiceURLRetriever.RetrieveFromURL(url);
	}
	
	/**
	 * Get the information needed from the yahoo weather data service
	 * @param yahooData
	 * @return
	 */
	public static Map<String,String> getWeatherMapFromYahooXML(String yahooXML){
		if(yahooXML.contains("Error")){
			return null;
		}
		return YahooWeatherXMLParser.getWeatherMapFromYahooXML(yahooXML);
	}

	
	public static void main(String[] args) throws Exception{
		//System.out.println(YahooWhereURLXMLParser.getPlacesWithName("16.463461+107.584709"));
		//System.out.println(YahooWhereURLXMLParser.getPlacesWithName("Hue,Vietnam"));
		//Place pl = YahooWhereURLXMLParser.placeStr2PlaceObj("Hue,Vnam");
		System.out.println(WebServiceURLRetriever.RetrieveFromURL("http://www.google.com/ig/api?weather=Tam+Ky,Vietnam"));
	}
}