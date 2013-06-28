import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import deri.sensor.wrapper.WebServiceURLRetriever;


public class TestXMLFile {

	public static void readXMLFromURL(){
		String url = "http://www.google.com/ig/api?weather=Hue";
		String xml = WebServiceURLRetriever.RetrieveFromURL(url);
		System.out.println(xml);
	}
	public static void main(String argv[]) {
		String xmlFileName = "/root/Station locations.kml"; 
		Document document = null;
	      SAXReader reader = new SAXReader();
	      try
	      {
	         document = reader.read( xmlFileName );
//	         Element root = document.getRootElement();
//	         List<Element> elements = root.elements();
//	         System.out.println(document.getRootElement().getName());
//	         for(Element ele:elements){
//	        	 System.out.println(ele.getName());
//	        	 List<Element> subelements = ele.elements();
//	        	 for(Element el:subelements){
//		        	 System.out.println(el.getPath());
//	        	 }
//	         }
	      }
	      catch (DocumentException e)
	      {
	         e.printStackTrace();
	      }
	      readXMLFromURL();
	}

}
