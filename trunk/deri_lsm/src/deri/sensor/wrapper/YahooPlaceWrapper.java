package deri.sensor.wrapper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import deri.sensor.javabeans.Place;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;
import deri.sensor.utils.ThreadUtil;

public class YahooPlaceWrapper extends Thread{	
	private String dataIn;
	private String url;
	private Place currentPlaceData = null;
	
	Configuration cfg;
	SessionFactory sessions;
	Session session;
	Transaction tx;
	
	public YahooPlaceWrapper(){
		
	}
	
	public YahooPlaceWrapper(String url){
		this.url = url;
		currentPlaceData = getOutPutFormat();
		
		cfg = new Configuration().configure();
		sessions = cfg.buildSessionFactory();
		//SchemaExport export = new SchemaExport(cfg);
		//export.create(true, true);
	}
	
	@Override
	public void run(){
		while(true){
			currentPlaceData = getOutPutFormat();			
			session = sessions.getCurrentSession();
			tx = session.beginTransaction();
			postStreamElement();
			tx.commit();			
			ThreadUtil.sleepForSeconds(10);
		}
	}
	
	public Place getOutPutFormat(){
		dataIn = WebServiceURLRetriever.RetrieveFromURL(url);
		Place place = YahooWhereURLXMLParser.woeid2PlaceObj("2507854"); 
		return place;
	}
	
	public void postStreamElement(){
		//currentPlaceData = getOutPutFormat();		
		//session = sessions.getCurrentSession();
		//tx = session.beginTransaction();
		session.save(currentPlaceData);
		//tx.commit();							
	}
	
	public Place getCurrentPlaceData(){
		return currentPlaceData;
	}
}
