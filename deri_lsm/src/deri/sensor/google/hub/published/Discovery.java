package deri.sensor.google.hub.published;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class Discovery {

	public Discovery() {
	} 
	
	public String hasHub(Document doc) throws Exception {
		String hub = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;
		
		try{
			xPathExpression = xPath.compile("/feed/link[@rel='hub']/@href");
			hub = xPathExpression.evaluate(doc);
			if ((hub==null)||(hub=="")){
				xPathExpression = xPath.compile("//link[@rel='hub']/@href");
				hub = xPathExpression.evaluate(doc);			
			}	
			
			if (hub ==""){
				return null;
			}
			
			return hub;
		
		} catch (XPathExpressionException e) {
			return null;
		}
	}

	public String hasTopic(Document doc){
		String topic = null;
		
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;

		try {
			xPathExpression = xPath.compile("/feed/link[@rel='self']/@href");
			topic = xPathExpression.evaluate(doc);
			if ((topic==null)||(topic.equals(""))){
				xPathExpression = xPath.compile("//link[@rel='self']/@href");
				topic = xPathExpression.evaluate(doc);			
			}
			
			if (topic.equals("")){
				return null;
			}
			return topic;
			
		} catch (XPathExpressionException e) {
		    return null;
		}
		
	}
	
	public String getContents(String feed) throws Exception {
		String response = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(feed);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		response = (responseBody);

		httpclient.getConnectionManager().shutdown();

		return response;
	}

	public String getHub(String feedurl) throws Exception {
		DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = Factory.newDocumentBuilder();
//		Document doc = builder.parse(new InputSource(new StringReader(
//				getContents(feedurl))));
		URL url = new URL(feedurl);
		Document doc = builder.parse(url.openStream());
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		XPathExpression xPathExpression;
		String hub;
		xPathExpression = xPath.compile("/feed/link[@rel='hub']/@href");
		hub = xPathExpression.evaluate(doc);
		if ((hub==null)||(hub.equals(""))){
			xPathExpression = xPath.compile("//link[@rel='hub']/@href");
			hub = xPathExpression.evaluate(doc);			
		}
		return hub;
	}

	public HashMap<String, String> getHubs(ArrayList<String> feedurls) {
		Iterator<String> i = feedurls.iterator();
		HashMap<String, String> hashtable = new HashMap<String, String>();
		while (i.hasNext()) {
			String feedurl = i.next();
			try {
				hashtable.put(feedurl, getHub(feedurl));
			} catch (Exception e) {
			}
		}
		return hashtable;
	}
}
