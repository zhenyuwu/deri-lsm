package deri.sensor.database;



import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import deri.sensor.manager.PlaceManagerImpl;
import deri.sensor.manager.SensorManagerImpl;
import deri.sensor.manager.UserActiveManagerImpl;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class ServiceLocator {
	public static ApplicationContext ctx;
	static{
		ctx = new ClassPathXmlApplicationContext("applicationContext-*.xml");
	}
	
	private ServiceLocator(){}
	
	public static SessionFactory getSessionFactory(){
		return (SessionFactory)ctx.getBean("sessionFactory");
	}
	
	public static SensorManagerImpl getSensorManager(){
		return (SensorManagerImpl)ctx.getBean("sensorManager");
	}
	
	public static PlaceManagerImpl getPlaceManager(){
		return (PlaceManagerImpl)ctx.getBean("placeManager");
	}
	
	public static UserActiveManagerImpl getUserActiveManager(){
		return (UserActiveManagerImpl)ctx.getBean("userActiveManager");
	}
}
