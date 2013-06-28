package ssc.wrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.encoding.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Attribute;
import org.dom4j.Element;

public class WebServiceURLRetriever {



  public static String RetrieveFromURL(String url){
	  try{
			InputStream in = null;
			BufferedReader reader;
			String result="";
	    
		    URLConnection conn = new URL(url).openConnection();
		    in = conn.getInputStream();
		    reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = reader.readLine()) != null){
				result += line + "\n";
			}	
			//System.out.println(result); 
			return result;
	  }catch(Exception e){
		  e.printStackTrace();
		  System.out.println("The url is not existent");		  
	  }finally{
		  
	  }
	return null;
  }
  

  
  private static void getLeafNodeList(Element elm,List lst){		
		List<Element> elements = elm.elements();
		if(elements.size()==0){
			System.out.println(elm.getPath());
			Iterator<Attribute> iter = elm.attributeIterator();
			if(!iter.hasNext())
				lst.add(elm.getPath());
			else
				while(iter.hasNext()){
					Attribute attribute = iter.next();
					String name  = attribute.getName();
//					System.out.println(attribute.getPath());
					lst.add(attribute.getPath());
				}
		}
		else
			for(Element element:elements)
				getLeafNodeList(element,lst);
	}
  
  
  public static String RetrieveFromURLWithAuthentication(String url,String username,String password){
	  try{
			InputStream in = null;
			BufferedReader reader;
			String result="";

			URI lUri = new URI(url);
		    
		    HttpClient lHttpClient = new DefaultHttpClient();
		    HttpGet lHttpGet = new HttpGet();
		    lHttpGet.setURI(lUri);
		    lHttpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password), "UTF-8", false));
		    
		    HttpResponse lHttpResponse = lHttpClient.execute(lHttpGet);
		    in = lHttpResponse.getEntity().getContent();
		    reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while((line = reader.readLine()) != null){
				result += line + "\n";
			}	
			//System.out.println(result); 
			return result;
	  }catch(Exception e){
		  e.printStackTrace();
		  System.out.println("The url is not existent");		  
	  }finally{
		  
	  }
	return null;
  }
  
  public static void main(String[] agrvs){
	 String url = "https://api.cosm.com/v2/feeds/59928.xml";
//	 String xml = WebServiceURLRetriever.RetrieveFromURLWithAuthentication(url,"nmqhoan","nmqhoan");	 
	 String json = WebServiceURLRetriever.RetrieveFromURL("http://developer.mbta.com/lib/rthr/orange.json");
	 System.out.println(json);
//	 System.out.println(Base64.class.getProtectionDomain().getCodeSource().getLocation());
  }
}
