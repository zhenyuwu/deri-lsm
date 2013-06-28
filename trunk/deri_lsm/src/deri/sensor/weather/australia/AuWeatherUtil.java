package deri.sensor.weather.australia;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import deri.sensor.utils.ConstantsUtil;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AuWeatherUtil {
	public final static String basic_url_shtml = "http://www.bom.gov.au/products/";
	public final static String basic_url_json =  "http://www.bom.gov.au/fwo/";
	
	public final static Map<String,String> allBasicURLMap = new HashMap<String,String>();
	static{
		initializeAllBasicURLMap();
	}
	
	private static void initializeAllBasicURLMap(){
		allBasicURLMap.put("New South Wales","http://www.bom.gov.au/nsw/observations/nswall.shtml");
		allBasicURLMap.put("Victoria","http://www.bom.gov.au/vic/observations/vicall.shtml");
		allBasicURLMap.put("Queensland","http://www.bom.gov.au/qld/observations/qldall.shtml");
		allBasicURLMap.put("Western Australia","http://www.bom.gov.au/wa/observations/waall.shtml");
		allBasicURLMap.put("South Australia","http://www.bom.gov.au/sa/observations/saall.shtml");
		allBasicURLMap.put("Tasmania","http://www.bom.gov.au/tas/observations/tasall.shtml");
		allBasicURLMap.put("Northern Territory","http://www.bom.gov.au/nt/observations/ntall.shtml");
	}
	
	public static String assembleID_JSONURL(String id){
		String jsonURL = basic_url_json + id + ".json";
		return jsonURL;
	}
	
	public static String assembleID_SHTMLURL(String id){
		String shtmlURL = basic_url_shtml + id + ".shtml";
		return shtmlURL;
	}
	
	public static double[] getPositionFromSHTMLContent(String content){
		String regex = "(<td> *<b>Lat:</b> *)"+ConstantsUtil.double_regex+"( *</td><td> *<b>Lon:</b> *)"+ConstantsUtil.double_regex+"( *</td>)";
		Matcher matcher = Pattern.compile(regex).matcher(content);
		if(matcher.find()){
			double[] result = new double[2];
			double lat = Double.parseDouble(matcher.group(2));
			double lng = Double.parseDouble(matcher.group(5));
			result[0] = lat;
			result[1] = lng;
			return result;
		}else{
			return null;
		}
	}
	
	public static Map<String,String> getAllID_Cities(){
		Map<String,String> map = new HashMap<String,String>();
//		<a href="/products/IDN60801/IDN60801.94596.shtml">Ballina</a>
		String regex = "(/products/)(\\w+/\\w+\\.\\d+)(\\.shtml)(\">)(\\w+)(</a>)";
		for(String url : allBasicURLMap.values()){
			String sourceCode = WebServiceURLRetriever.RetrieveFromURL(url);
			System.out.println(url);
			System.out.println(sourceCode);
			Matcher matcher = Pattern.compile(regex).matcher(sourceCode);
			while(matcher.find()){
				String ID = matcher.group(2);System.out.println(ID);
				String city = matcher.group(5);System.out.println(city);
				map.put(ID, city);
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		Map<String,String> id_cities = getAllID_Cities();
		System.out.println(id_cities.size());//447
	}
	
}
