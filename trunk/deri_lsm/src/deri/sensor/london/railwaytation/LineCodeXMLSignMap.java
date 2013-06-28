package deri.sensor.london.railwaytation;

import java.util.EnumMap;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LineCodeXMLSignMap {
public static EnumMap<LineCodeXMLSign,String> linecode_xml_sign = new EnumMap<LineCodeXMLSign,String>(LineCodeXMLSign.class);
	
	static{
		initialize_linecode_xml_sign();
	}
	
	private static void initialize_linecode_xml_sign(){
		linecode_xml_sign.put(LineCodeXMLSign.root, "/ROOT");		
		linecode_xml_sign.put(LineCodeXMLSign.S, "/ROOT/S");
		linecode_xml_sign.put(LineCodeXMLSign.Code, "/ROOT/S/@Code");
	}	
	
	public static String assembleLindeCodeSign(LineCodeXMLSign sign){
		String express = linecode_xml_sign.get(sign);
		return express;
	}
}
