package deri.sensor.google.hub.published;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.*;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class XMLParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    URL url = new URL("http://weather.yahooapis.com/forecastrss?w=1378250");
			Document doc = builder.parse(url.openStream());
			XPathExpression xPathExpression = null;
			String hub = xPathExpression.evaluate(doc);
			System.out.println(doc.getFirstChild().getNodeName());
		}catch(Exception e){}
	}

}
