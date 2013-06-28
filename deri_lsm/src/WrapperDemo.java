import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import deri.sensor.wrapper.WebServiceURLRetriever;


public class WrapperDemo {

	public static void bar(Document document) {
        List list = document.selectNodes( "/rss/channel/yweather" );

        
        Element node = (Element)document.selectSingleNode( "/rss/channel/yweather:location" );
        Iterator it = node.attributeIterator();
        while(it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            System.out.println(attribute.getName() +":" + attribute.getData());
        }
        
        
        //System.out.println(node.toString());
        String name = node.valueOf( "@city" );
        System.out.println(name);
    }
	
	public static void getListAttribute(String url){
		String xml = WebServiceURLRetriever.RetrieveFromURL(url);
		List<String> attribute = new ArrayList<String>();
		
		try{
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<String> lst = new ArrayList<String>();
			getLeafNodeList(root,lst);
			System.out.println(lst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void getLeafNodeList(Element elm,List<String> lst){		
		List<Element> elements = elm.elements();
		if(elements.size()==0){
			System.out.println(elm.getName());
			lst.add(elm.getName());
		}
		else
			for(Element element:elements)
				getLeafNodeList(element,lst);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		
		String url = "http://weather.yahooapis.com/forecastrss?p=15231";
		String result = "";
		InputStream in = null;
		BufferedReader reader = null;
		try {
			url = "http://api.pachube.com/v2/feeds/29047.xml";
			getListAttribute(url);			
			
		}catch(Exception e){
			
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
