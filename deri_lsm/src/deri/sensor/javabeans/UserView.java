package deri.sensor.javabeans;


/**
 * @author Hoan Nguyen Mau Quoc
 */
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


public class UserView {
	private String id;
	private String username;
	private String viewname;
	private String viewXMLContent;
	
	public String getId() {
		return id;
	}
	@SuppressWarnings("unused")
	private void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getViewname() {
		return viewname;
	}
	public void setViewname(String viewname) {
		this.viewname = viewname;
	}
	
	
	/**
	 * viewXMLContent format is like this:
	 * @return
	 */
	public String getViewXMLContent() {
		return viewXMLContent;
	}
	
	public void setViewXMLContent(String viewXMLContent) {
		this.viewXMLContent = viewXMLContent;
	}
	
	public static UserViewObject getFilterType(String viewXMLContent){
		String lat_express = "/view/center/lat";
		String lng_express = "/view/center/lng";
		String zoom_express = "/view/zoom";
		String search_express = "/view/search/city";
		String type_express = "/view/filter/type";
		String region_express = "/view/filter/region";
		
		UserViewObject userview = new UserViewObject();
		try {
			Document document = DocumentHelper.parseText(viewXMLContent);
			Element lat = (Element)document.selectSingleNode(lat_express);
			String lat_str = lat.getTextTrim();
			double lat_double = Double.parseDouble(lat_str);
			userview.setLat(lat_double);
			
			
			Element lng = (Element)document.selectSingleNode(lng_express);
			String lng_str = lng.getTextTrim();
			double lng_double = Double.parseDouble(lng_str);
			userview.setLng(lng_double);
			
			
			Element zoom = (Element)document.selectSingleNode(zoom_express);
			String zoom_str = zoom.getTextTrim();
			int zoom_int = Integer.parseInt(zoom_str);
			userview.setZoom(zoom_int);
			
			
			
			Element search = (Element)document.selectSingleNode(search_express);
			String search_str = search.getTextTrim();
			if(search_str!= null && !search_str.trim().equals("") && !search_str.trim().equals(",")){
				Attribute only = search.attribute("only");
				String value = only.getValue();
				boolean isSearchOnly = value.equals("true") ? true : false;
				userview.setCity(search_str);
				userview.setSearch_only(isSearchOnly);
			}
			
			
			
			
			Element type = (Element)document.selectSingleNode(type_express);
			String type_str = type.getTextTrim();
			List<String> type_list = null;
			if(type_str != null && !type_str.trim().equals("") && type_str.contains(",")){
				type_list = new ArrayList<String>();
				String[] type_array = type_str.split("\\,");
				for(String type_item : type_array){
					type_list.add(type_item);
				}
			}
			userview.setFilterTypes(type_list);
			
			
			Element region = (Element)document.selectSingleNode(region_express);
			String region_str = region.getTextTrim();
			List<String> region_list = null;
			if(region_str != null && !region_str.trim().equals("") && region_str.contains(",")){
				region_list = new ArrayList<String>();
				String[] region_array = region_str.split("\\,");
				for(String region_item : region_array){
					region_list.add(region_item);
				}
			}
			userview.setFilterRegion(region_list);
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		return userview;
	}
	
	public static void main(String[] args) {
		String xml = "<view><center><lat>39.876019</lat><lng>116.411133</lng></center><zoom>1</zoom><filter><type>south america,asia,africa,Canada,Mexico,United States,</type><region>WEBCAM,FLOOD,SNOW,snow_depth,snow_fall,</region></filter></view>";
		UserViewObject userview = getFilterType(xml);
		System.out.println(userview.getLat());
		System.out.println(userview.getLng());
		System.out.println(userview.getZoom());
		System.out.println(userview.getFilterTypes());
		System.out.println(userview.getFilterRegion());
	}
	
}
