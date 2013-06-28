package ssc.sensor.places.yahoo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import ssc.beans.Place;
import ssc.json.ConvertJSONtoXMLNoHints;
import ssc.sensor.json.JSONArray;
import ssc.sensor.json.JSONException;
import ssc.sensor.json.JSONObject;
import ssc.utils.NumberUtil;
import ssc.wrapper.WebServiceURLRetriever;






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
	
	private static final Logger log = Logger.getLogger(SignPostTest.class);
	public static final String wrongInputChars = ",()*!@#$&=|;:?/";
	public static final String blank_code = "%20";
	public static final String blank = " ";
	
	protected static String yahooServer = "http://yboss.yahooapis.com/geo/";
	private static String consumer_key = "dj0yJmk9cGxGcWQwdkJWaGtYJmQ9WVdrOVFtMTJkVE5MTjJNbWNHbzlNVGs1TVRnNE9ERTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xMQ--";
	private static String consumer_secret = "5e90d0237c540b80bff878e6553620694606d607";	
	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();	
	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";	
	/** Call Type */
	private static final String callType = "placefinder";	
	private static final int HTTP_STATUS_OK = 200;	
	private static final String RESPONSE_FORMAT = "&flags=J";
	
	private static String getAppID(int index){
		return appid[index % appid.length];
	} 
	
	public static String trimWrongInputChars(String input){
		return input.replaceAll("["+wrongInputChars+"]", blank).replaceAll(blank + "+", blank_code);
	}
	
	private static boolean isConsumerKeyExists() {
		if(consumer_key.isEmpty()) {
			return false;
		}
		return true;
	}
	
	private static boolean isConsumerSecretExists() {
		if(consumer_secret.isEmpty()) {			
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public static String returnPlaceFinderHttpData(String place){		
		if(isConsumerKeyExists() && isConsumerSecretExists()) {			
			// Start with call Type
			String params = callType;			
			// Add query
			params = params.concat("?q=");			
			// Encode Query string before concatenating
			try {
				params = params.concat(URLEncoder.encode(place, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			// Create final URL
			String url = yahooServer + params + RESPONSE_FORMAT;
			System.out.println(url);
			// Create oAuth Consumer 
			OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);			
			// Set the HTTP request correctly
			httpRequest.setOAuthConsumer(consumer);			
			try {
				log.info("sending get request to" + URLDecoder.decode(url, ENCODE_FORMAT));
				int responseCode = httpRequest.sendGetRequest(url); 				
				// Send the request
				if(responseCode == HTTP_STATUS_OK) {
					log.info("Response ");
				} else {
					log.error("Error in response due to status code = " + responseCode);
				}
				log.info(httpRequest.getResponseBody());
				System.out.println(httpRequest.getResponseBody());
				return httpRequest.getResponseBody();
			} catch(UnsupportedEncodingException e) {
				log.error("Encoding/Decording error");
			} catch (IOException e) {
				log.error("Error with HTTP IO", e);
			} catch (Exception e) {
				log.error(httpRequest.getResponseBody(), e);
				return null;
			}			
		} else {
			log.error("Key/Secret does not exist");
		}
		return null;
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
		
		String jsonString = returnPlaceFinderHttpData(placeStr);
		System.out.println("location:"+jsonString);
		if(jsonString == null || jsonString.trim().equals("") || !jsonString.contains("woeid")){
			return null;
		}		
		JSONObject jo = null;
		JSONObject jp = null;
		JSONArray ja = null;
		JSONObject jR = null;
		try {
			jo = new JSONObject(jsonString);
			jo = jo.getJSONObject("bossresponse");
			if(jo != null){	
				jp = jo.optJSONObject("placefinder");
				if(jp != null){
					jR = jp.optJSONObject("results");
					if(jR != null){
						ja = new JSONArray();
						ja.put(jR);
					}else
						ja = jp.optJSONArray("results");
					for(int i=0;i<ja.length();i++){
						jo = ja.getJSONObject(i);
						woeid = jo.getString("woeid");
						city = jo.getString("city");
						state = jo.getString("state");
						county = jo.getString("county");
	//					zipcode = jo.getString("uzip");
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
	
	
	public static void main(String[] args) throws Exception{
		//System.out.println(YahooWhereURLXMLParser.getPlacesWithName("16.463461+107.584709"));
//		System.out.println(YahooWhereURLXMLParser.getPlacesWithName("Hue,Vietnam"));
		Place pl = YahooWhereURLXMLParser.placeStr2PlaceObj("HaNoi,Vnam");
		System.out.println(pl.toString()+","+pl.getLat()+","+pl.getLng());
//		System.out.println(WebServiceURLRetriever.RetrieveFromURL("http://www.google.com/ig/api?weather=Tam+Ky,Vietnam"));
	}
}