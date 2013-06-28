package deri.sensor.hashmaps.util;

import java.util.HashMap;
import java.util.Map;

import deri.sensor.meta.SourceType;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class XSLTMapFile {
		
	public static Map<SourceType,String> sensordata2xslt = new HashMap<SourceType,String>();
	public static String sensormeta2xslt = "/xslt/Sensormeta.xsl";
	static{
		initialize_sensordata2xslt_map();
	}
	
	private static void initialize_sensordata2xslt_map(){
		
		sensordata2xslt.put(SourceType.yahoo,"/xslt/YahooWeather.xsl");
		sensordata2xslt.put(SourceType.australia,"/xslt/WAustralia.xsl");
		sensordata2xslt.put(SourceType.wunderground,"/xslt/WUnderground.xsl");
		sensordata2xslt.put(SourceType.weatherbug,"/xslt/WBug.xsl");
		sensordata2xslt.put(SourceType.traffic,"/xslt/LondonTraffic.xsl");
		sensordata2xslt.put(SourceType.bikehire,"/xslt/LondonBikeHire.xsl");
		sensordata2xslt.put(SourceType.railwaystation,"/xslt/LondonStation.xsl");		
	}
	
	

}