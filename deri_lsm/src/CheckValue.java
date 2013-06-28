import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;


public class CheckValue {
	
    public static void main(String[] args) throws ParseException {
    	try{
	    SensorManager sensormanager = ServiceLocator.getSensorManager();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	} // end method
}
